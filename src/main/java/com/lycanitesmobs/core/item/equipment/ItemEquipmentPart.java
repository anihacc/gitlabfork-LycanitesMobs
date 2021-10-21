package com.lycanitesmobs.core.item.equipment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.client.ClientManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.helpers.JSONHelper;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.ElementManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.BaseItem;
import com.lycanitesmobs.core.item.ChargeItem;
import com.lycanitesmobs.core.item.equipment.features.EquipmentFeature;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.text.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ItemEquipmentPart extends BaseItem {
	/** The base amount of experience needed to level up, this is increased by the part's level scaled. **/
	public static int BASE_LEVELUP_EXPERIENCE = 500;

	/** A map of mob classes and parts that they drop. **/
	public static Map<String, List<ItemEquipmentPart>> MOB_PART_DROPS = new HashMap<>();

	/** A list of all features this part has. **/
	public List<EquipmentFeature> features = new ArrayList<>();

	/** The Elements of this part, used to determine what charges can be used to upgrade this part along with other future features. **/
	public List<ElementInfo> elements = new ArrayList<>();

	/** The slot type that this part must fit into. Can be: base, head, blade, axe, pike or jewel. **/
	public String slotType;

	/** The id of the mob that drops this part. **/
	public String dropMobId;

	/** The default chance of the part being dropped by a mob. **/
	public float dropChance = 1;

	/** The minimum random level that this part can be. **/
	public int levelMin = 1;

	/** The maximum random level that this part can be. **/
	public int levelMax = 3;


	/**
	 * Constructor
	 * @param groupInfo The group that this part belongs to.
	 */
	public ItemEquipmentPart(Item.Properties properties, ModInfo groupInfo) {
		super(properties);
		this.modInfo = groupInfo;
	}

	/** Loads this part from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		this.itemName = "equipmentpart_" + json.get("itemName").getAsString();

		this.slotType = json.get("slotType").getAsString();

		if(json.has("dropMobId")) {
			this.dropMobId = json.get("dropMobId").getAsString();
			if(!"".equals(this.dropMobId)) {
				if(!MOB_PART_DROPS.containsKey(this.dropMobId)) {
					MOB_PART_DROPS.put(this.dropMobId, new ArrayList<>());
				}
				MOB_PART_DROPS.get(this.dropMobId).add(this);
			}
		}

		if(json.has("dropChance"))
			this.dropChance = json.get("dropChance").getAsFloat();

		if(json.has("levelMin"))
			this.levelMin = json.get("levelMin").getAsInt();

		if(json.has("levelMax"))
			this.levelMax = json.get("levelMax").getAsInt();

		// Elements:
		List<String> elementNames = new ArrayList<>();
		if(json.has("elements")) {
			elementNames = JSONHelper.getJsonStrings(json.get("elements").getAsJsonArray());
		}
		this.elements.clear();
		for(String elementName : elementNames) {
			ElementInfo element = ElementManager.getInstance().getElement(elementName);
			if (element == null) {
				throw new RuntimeException("[Equipment] Unable to initialise Equipment Part " + this.getDescription().getString() + " as the element " + elementName + " cannot be found.");
			}
			this.elements.add(element);
		}

		// Features:
		if(json.has("features")) {
			JsonArray jsonArray = json.get("features").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				JsonObject featureJson = jsonIterator.next().getAsJsonObject();
				if(!this.addFeature(EquipmentFeature.createFromJSON(featureJson))) {
					LycanitesMobs.logWarning("", "[Equipment] The feature " + featureJson.toString() + " was unable to be added, check the JSON format.");
				}
			}
		}
		this.sortFeatures();

		this.setup();

		TextureManager.addTexture(this.itemName, this.modInfo, "textures/equipment/" + this.itemName + ".png");
	}

	@Override
	public Component getName(ItemStack itemStack) {
		BaseComponent displayName = new TranslatableComponent(this.getDescriptionId(itemStack).replace("equipmentpart_", ""));
		displayName.append(" ")
			.append(new TranslatableComponent("equipment.level"))
			.append(" " + this.getPartLevel(itemStack));
		return displayName;
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, world, tooltip, tooltipFlag);
		for(Component description : this.getAdditionalDescriptions(itemStack, world, tooltipFlag)) {
			tooltip.add(description);
		}
	}

	@Override
	public Component getDescription(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		return new TranslatableComponent("item.lycanitesmobs.equipmentpart.description").withStyle(ChatFormatting.DARK_GREEN);
	}

	public List<Component> getAdditionalDescriptions(ItemStack itemStack, @Nullable Level world, TooltipFlag tooltipFlag) {
		List<Component> descriptions = new ArrayList<>();
		int level = this.getPartLevel(itemStack);
		int experience = this.getExperience(itemStack);
		int experienceMax = this.getExperienceForNextLevel(itemStack);

		// Condition:
		descriptions.add(new TextComponent("-------------------"));
		descriptions.add(new TranslatableComponent("equipment.sharpness").append(" " + this.getSharpness(itemStack) + "/" + ItemEquipment.SHARPNESS_MAX).withStyle(ChatFormatting.BLUE));
		descriptions.add(new TranslatableComponent("equipment.mana").append(" " + this.getMana(itemStack) + "/" + ItemEquipment.MANA_MAX).withStyle(ChatFormatting.BLUE));

		// Base Stats:
		descriptions.add(new TextComponent("-------------------"));
		descriptions.add(new TranslatableComponent("equipment.slottype").append(" " + this.slotType).withStyle(ChatFormatting.GOLD));
		descriptions.add(new TranslatableComponent("equipment.level").append(" " + level + "/" + this.levelMax).withStyle(ChatFormatting.GOLD));
		if(level < this.levelMax) {
			descriptions.add(new TranslatableComponent("entity.experience").append(": " + experience + "/" + experienceMax).withStyle(ChatFormatting.GOLD));
		}
		if(!this.elements.isEmpty()) {
			descriptions.add(new TranslatableComponent("equipment.element").append(" ").append(this.getElementNames()).withStyle(ChatFormatting.DARK_AQUA));
		}
		descriptions.add(new TextComponent("-------------------"));


		for(EquipmentFeature feature : this.features) {
			Component featureDescription = feature.getDescription(itemStack, level);
			if(featureDescription != null && !"".equals(featureDescription.getString())) {
				descriptions.add(featureDescription);
			}
		}
		return descriptions;
	}

	@OnlyIn(Dist.CLIENT)
	@Nullable
	@Override
	public net.minecraft.client.gui.Font getFontRenderer(ItemStack stack) {
		return ClientManager.getInstance().getFontRenderer();
	}

	/** Sets up this equipment part, this is called when the provided stack is dropped and needs to have its level randomized, etc. **/
	public void randomizeLevel(Level world, ItemStack itemStack) {
		int level = this.levelMax;
		if(this.levelMin < this.levelMax) {
			level = this.levelMin + world.random.nextInt(this.levelMax - this.levelMin + 1);
		}
		this.setLevel(itemStack, level);
	}

	/**
	 * Adds a new Feature to this Equipment Part.
	 * @return True on success and false on failure.
	 **/
	public boolean addFeature(EquipmentFeature feature) {
		if(feature == null) {
			LycanitesMobs.logWarning("", "[Equipment] Unable to add a null feature to " + this);
			return false;
		}
		if(feature.featureType == null) {
			LycanitesMobs.logWarning("", "[Equipment] Feature type not set for part " + this);
			return false;
		}
		this.features.add(feature);
		return true;
	}

	/** Cycles through all features and organises them. **/
	public void sortFeatures() {
		Comparator<EquipmentFeature> comparator = (o1, o2) -> o1.featureType.compareToIgnoreCase(o2.featureType);
		this.features.sort(comparator);
	}

	/** Sets the level of the provided Equipment Item Stack. **/
	public void setLevel(ItemStack itemStack, int level) {
		CompoundTag nbt = this.getTagCompound(itemStack);
		nbt.putInt("equipmentLevel", level);
		itemStack.setTag(nbt);
	}

	/** Returns an Equipment Part Level for the provided ItemStack. **/
	public int getPartLevel(ItemStack itemStack) {
		CompoundTag nbt = this.getTagCompound(itemStack);
		int level = 1;
		if(nbt.contains("equipmentLevel")) {
			level = nbt.getInt("equipmentLevel");
		}
		return level;
	}

	/** Sets the experience of the provided Equipment Item Stack. **/
	public void setExperience(ItemStack itemStack, int experience) {
		CompoundTag nbt = this.getTagCompound(itemStack);
		nbt.putInt("equipmentExperience", experience);
		itemStack.setTag(nbt);
	}

	/** Increases the experience of the provided Equipment Item Stack. This will also level up the part if the experience is enough. **/
	public void addExperience(ItemStack itemStack, int experience) {
		int currentLevel = this.getPartLevel(itemStack);
		if(currentLevel >= this.levelMax) {
			this.setExperience(itemStack, 0);
		}
		int increasedExperience = this.getExperience(itemStack) + experience;
		int nextLevelExperience = this.getExperienceForNextLevel(itemStack);
		if(increasedExperience >= nextLevelExperience) {
			increasedExperience = increasedExperience - nextLevelExperience;
			this.setLevel(itemStack, currentLevel + 1);
		}
		this.setExperience(itemStack, increasedExperience);
	}

	/** Returns the Equipment Part Experience for the provided ItemStack. **/
	public int getExperience(ItemStack itemStack) {
		CompoundTag nbt = this.getTagCompound(itemStack);
		int experience = 0;
		if(nbt.contains("equipmentExperience")) {
			experience = nbt.getInt("equipmentExperience");
		}
		return experience;
	}

	/**
	 * Determines how much experience the part needs in order to level up.
	 * @return Experience required for a level up.
	 */
	public int getExperienceForNextLevel(ItemStack itemStack) {
		return BASE_LEVELUP_EXPERIENCE + Math.round(BASE_LEVELUP_EXPERIENCE * (this.getPartLevel(itemStack) - 1) * 0.25F);
	}

	/**
	 * Determines if the provided itemstack can be consumed to add experience this part.
	 * @param itemStack The possible leveling itemstack.
	 * @return True if this part should consume the itemstack and gain experience.
	 */
	public boolean isLevelingChargeItem(ItemStack itemStack) {
		if(itemStack.getItem() instanceof ChargeItem) {
			ChargeItem chargeItem = (ChargeItem)itemStack.getItem();
			for(ElementInfo elementInfo : this.elements) {
				if (chargeItem.getElements().contains(elementInfo)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Determines how much experience the provided charge itemstack can grant this part.
	 * @param itemStack The possible leveling itemstack.
	 * @return The amount of experience to gain.
	 */
	public int getExperienceFromChargeItem(ItemStack itemStack) {
		int experience = 0;
		if(itemStack.getItem() instanceof ChargeItem) {
			ChargeItem chargeItem = (ChargeItem)itemStack.getItem();
			for(ElementInfo elementInfo : this.elements) {
				if (chargeItem.getElements().contains(elementInfo)) {
					experience += ChargeItem.CHARGE_EXPERIENCE;
				}
			}
		}
		return experience;
	}

	/** Returns the Equipment Part Sharpness for the provided ItemStack. **/
	public int getSharpness(ItemStack itemStack) {
		CompoundTag nbt = this.getTagCompound(itemStack);
		int sharpness = ItemEquipment.SHARPNESS_MAX;
		if(nbt.contains("equipmentSharpness")) {
			sharpness = nbt.getInt("equipmentSharpness");
		}
		return sharpness;
	}

	/** Sets the sharpness of the provided Equipment Item Stack. **/
	public void setSharpness(ItemStack itemStack, int sharpness) {
		CompoundTag nbt = this.getTagCompound(itemStack);
		nbt.putInt("equipmentSharpness", Math.max(Math.min(sharpness, ItemEquipment.SHARPNESS_MAX), 0));
		itemStack.setTag(nbt);
	}

	/** Increases the sharpness of the provided Equipment Item Stack. This will also level up the part if the sharpness is enough. **/
	public boolean addSharpness(ItemStack itemStack, int sharpness) {
		int currentSharpness = this.getSharpness(itemStack);
		if(currentSharpness >= ItemEquipment.SHARPNESS_MAX) {
			return false;
		}
		this.setSharpness(itemStack, currentSharpness + sharpness);
		return true;
	}

	/** Decreases the sharpness of the provided Equipment Item Stack. This will also level up the part if the sharpness is enough. **/
	public boolean removeSharpness(ItemStack itemStack, int sharpness) {
		int currentSharpness = this.getSharpness(itemStack);
		if(currentSharpness <= 0) {
			return false;
		}
		this.setSharpness(itemStack, currentSharpness - sharpness);
		return true;
	}

	/** Returns the Equipment Part Mana for the provided ItemStack. **/
	public int getMana(ItemStack itemStack) {
		CompoundTag nbt = this.getTagCompound(itemStack);
		int mana = ItemEquipment.MANA_MAX;
		if(nbt.contains("equipmentMana")) {
			mana = nbt.getInt("equipmentMana");
		}
		return mana;
	}

	/** Sets the mana of the provided Equipment Item Stack. **/
	public void setMana(ItemStack itemStack, int mana) {
		CompoundTag nbt = this.getTagCompound(itemStack);
		nbt.putInt("equipmentMana", Math.max(Math.min(mana, ItemEquipment.MANA_MAX), 0));
		itemStack.setTag(nbt);
	}

	/** Increases the mana of the provided Equipment Item Stack. This will also level up the part if the mana is enough. **/
	public boolean addMana(ItemStack itemStack, int mana) {
		int currentMana = this.getMana(itemStack);
		if(currentMana >= ItemEquipment.MANA_MAX) {
			return false;
		}
		this.setMana(itemStack, currentMana + mana);
		return true;
	}

	/** Decreases the mana of the provided Equipment Item Stack. This will also level up the part if the mana is enough. **/
	public boolean removeMana(ItemStack itemStack, int mana) {
		int currentMana = this.getMana(itemStack);
		if(currentMana <= 0) {
			return false;
		}
		this.setMana(itemStack, currentMana - mana);
		return true;
	}

	/** Returns the dyed color for the provided ItemStack. **/
	public Vec3 getColor(ItemStack itemStack) {
		CompoundTag nbt = this.getTagCompound(itemStack);
		double r = 1;
		double g = 1;
		double b = 1;
		if(nbt.contains("equipmentColorR")) {
			r = nbt.getFloat("equipmentColorR");
		}
		if(nbt.contains("equipmentColorG")) {
			g = nbt.getFloat("equipmentColorG");
		}
		if(nbt.contains("equipmentColorB")) {
			b = nbt.getFloat("equipmentColorB");
		}
		return new Vec3(r, g, b);
	}

	/** Set the dyed color for the provided ItemStack. **/
	public void setColor(ItemStack itemStack, float red, float green, float blue) {
		CompoundTag nbt = this.getTagCompound(itemStack);
		nbt.putFloat("equipmentColorR", red);
		nbt.putFloat("equipmentColorG", green);
		nbt.putFloat("equipmentColorB", blue);
		itemStack.setTag(nbt);
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
		if(!this.allowdedIn(tab)) {
			return;
		}

		for(int level = 1; level <= this.levelMax; level++) {
			ItemStack itemStack = new ItemStack(this, 1);
			this.setLevel(itemStack, level);
			items.add(itemStack);
		}
	}

	/**
	 * Returns if this Part has the provided element.
	 * @param element The element to check for.
	 * @return True if this part has the element.
	 */
	public boolean hasElement(ElementInfo element) {
		return this.elements.contains(element);
	}

	/**
	 * Returns a comma separated list of Elements used by this Part.
	 * @return The Elements used by this Part.
	 */
	public Component getElementNames() {
		BaseComponent elementNames = new TextComponent("");
		boolean firstElement = true;
		for(ElementInfo element : this.elements) {
			if(!firstElement) {
				elementNames.append(", ");
			}
			firstElement = false;
			elementNames.append(element.getTitle());
		}
		return elementNames;
	}
}
