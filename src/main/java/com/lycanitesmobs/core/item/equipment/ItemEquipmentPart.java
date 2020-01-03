package com.lycanitesmobs.core.item.equipment;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.helpers.JSONHelper;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.ElementManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.ChargeItem;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.features.EquipmentFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.vecmath.Vector4f;
import java.util.*;

public class ItemEquipmentPart extends ItemBase {
	/** The base amount of experience needed to level up, this is increased by the part's level scaled. **/
	public static int BASE_LEVELUP_EXPERIENCE = 1000;

	/** I am sorry, I couldn't find another way. Set in getMetadata(ItemStack) as it's called just before rendering. **/
	public static ItemStack ITEMSTACK_TO_RENDER;

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
	public ItemEquipmentPart(ModInfo groupInfo) {
		super();
		this.modInfo = groupInfo;
		this.setMaxStackSize(1);
		this.setCreativeTab(LycanitesMobs.equipmentPartsTab);
	}

	/** Loads this feature from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		this.itemName = json.get("itemName").getAsString();

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
				throw new RuntimeException("[Equipment] Unable to initialise Equipment Part " + this.getUnlocalizedName() + " as the element " + elementName + " cannot be found.");
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

		this.setRegistryName(this.modInfo.modid, this.itemName);
		this.setUnlocalizedName(this.itemName);

		AssetManager.addTexture(this.itemName, this.modInfo, "textures/equipment/" + this.itemName + ".png");
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemStack) {
		String displayName = LanguageManager.translate(this.getUnlocalizedName(itemStack) + ".name");
		displayName += " " + LanguageManager.translate("equipment.level") + " " + this.getLevel(itemStack);
		return displayName;
	}

	@Override
	public void addInformation(ItemStack itemStack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
		super.addInformation(itemStack, world, tooltip, tooltipFlag);
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		for(String description : this.getAdditionalDescriptions(itemStack, world, tooltipFlag)) {
			List formattedDescriptionList = fontRenderer.listFormattedStringToWidth("-------------------\n" + description, DESCRIPTION_WIDTH);
			for (Object formattedDescription : formattedDescriptionList) {
				if (formattedDescription instanceof String)
					tooltip.add("\u00a73" + formattedDescription);
			}
		}
	}

	@Override
	public String getDescription(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		return LanguageManager.translate("item.equipmentpart.description");
	}

	public List<String> getAdditionalDescriptions(ItemStack itemStack, @Nullable World world, ITooltipFlag tooltipFlag) {
		List<String> descriptions = new ArrayList<>();
		int level = this.getLevel(itemStack);
		int experience = this.getExperience(itemStack);
		int experienceMax = this.getExperienceForNextLevel(itemStack);

		String baseFeature = LanguageManager.translate("equipment.slottype") + " " + this.slotType;
		baseFeature += "\n" + LanguageManager.translate("equipment.level") + " " + level + "/" + this.levelMax;
		if(level < this.levelMax) {
			baseFeature += "\n" + LanguageManager.translate("entity.experience") + ": " + experience + "/" + experienceMax;
		}
		descriptions.add(baseFeature);

		if(!this.elements.isEmpty()) {
			String elementFeature = LanguageManager.translate("equipment.element") + " " + this.getElementNames();
			descriptions.add(elementFeature);
		}

		for(EquipmentFeature feature : this.features) {
			String featureDescription = feature.getDescription(itemStack, level);
			if(featureDescription != null && !"".equals(featureDescription)) {
				descriptions.add(featureDescription);
			}
		}

		return descriptions;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		return super.getAttributeModifiers(slot, stack);
	}


	@Override
	public int getMetadata(ItemStack stack) {
		ITEMSTACK_TO_RENDER = stack; // Render hack.
		return super.getMetadata(stack);
	}

	/** Gets or creates an NBT Compound for the provided itemstack. **/
	public NBTTagCompound getTagCompound(ItemStack itemStack) {
		if(itemStack.hasTagCompound()) {
			return itemStack.getTagCompound();
		}
		return new NBTTagCompound();
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	@Override
	public net.minecraft.client.gui.FontRenderer getFontRenderer(ItemStack stack) {
		return LycanitesMobs.proxy.getFontRenderer();
	}

	/** Sets up this equipment part, this is called when the provided stack is dropped and needs to have its level randomized, etc. **/
	public void randomizeLevel(World world, ItemStack itemStack) {
		int level = this.levelMax;
		if(this.levelMin < this.levelMax) {
			level = this.levelMin + world.rand.nextInt(this.levelMax - this.levelMin + 1);
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
		NBTTagCompound nbt = this.getTagCompound(itemStack);
		nbt.setInteger("equipmentLevel", level);
		itemStack.setTagCompound(nbt);
	}

	/** Returns an Equipment Part Level for the provided ItemStack. **/
	public int getLevel(ItemStack itemStack) {
		NBTTagCompound nbt = this.getTagCompound(itemStack);
		int level = 1;
		if(nbt.hasKey("equipmentLevel")) {
			level = nbt.getInteger("equipmentLevel");
		}
		return level;
	}

	/** Sets the experience of the provided Equipment Item Stack. **/
	public void setExperience(ItemStack itemStack, int experience) {
		NBTTagCompound nbt = this.getTagCompound(itemStack);
		nbt.setInteger("equipmentExperience", experience);
		itemStack.setTagCompound(nbt);
	}

	/** Increases the experience of the provided Equipment Item Stack. This will also level up the part if the experience is enough. **/
	public void addExperience(ItemStack itemStack, int experience) {
		int currentLevel = this.getLevel(itemStack);
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
		NBTTagCompound nbt = this.getTagCompound(itemStack);
		int experience = 0;
		if(nbt.hasKey("equipmentExperience")) {
			experience = nbt.getInteger("equipmentExperience");
		}
		return experience;
	}

	/**
	 * Determines how much experience the part needs in order to level up.
	 * @return Experience required for a level up.
	 */
	public int getExperienceForNextLevel(ItemStack itemStack) {
		return BASE_LEVELUP_EXPERIENCE + Math.round(BASE_LEVELUP_EXPERIENCE * (this.getLevel(itemStack) - 1) * 0.25F);
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

	/** Returns the dyed color for the provided ItemStack. **/
	public Vector4f getColor(ItemStack itemStack) {
		NBTTagCompound nbt = this.getTagCompound(itemStack);
		float r = 1;
		float g = 1;
		float b = 1;
		if(nbt.hasKey("equipmentColorR")) {
			r = nbt.getFloat("equipmentColorR");
		}
		if(nbt.hasKey("equipmentColorG")) {
			r = nbt.getFloat("equipmentColorG");
		}
		if(nbt.hasKey("equipmentColorB")) {
			r = nbt.getFloat("equipmentColorB");
		}
		return new Vector4f(r, g, b, 1);
	}

	/** Set the dyed color for the provided ItemStack. **/
	public void setColor(ItemStack itemStack, float red, float green, float blue) {
		NBTTagCompound nbt = this.getTagCompound(itemStack);
		nbt.setFloat("equipmentColorR", red);
		nbt.setFloat("equipmentColorG", green);
		nbt.setFloat("equipmentColorB", blue);
		itemStack.setTagCompound(nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(!this.isInCreativeTab(tab)) {
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
	public String getElementNames() {
		String elementNames = "";
		boolean firstElement = true;
		for(ElementInfo element : this.elements) {
			if(!firstElement) {
				elementNames += ", ";
			}
			firstElement = false;
			elementNames += element.getTitle();
		}
		return elementNames;
	}

	@Override
	public ModelResourceLocation getModelResourceLocation() {
		return new ModelResourceLocation(new ResourceLocation(LycanitesMobs.modid, "equipmentpart"), "inventory");
	}

	/** Returns the texture to use for the provided ItemStack. **/
	public ResourceLocation getTexture(ItemStack itemStack, String suffix) {
		if(AssetManager.getTexture(this.itemName + suffix) == null)
			AssetManager.addTexture(this.itemName + suffix, this.modInfo, "textures/equipment/" + this.itemName.toLowerCase() + suffix + ".png");
		return AssetManager.getTexture(this.itemName + suffix);
	}
}
