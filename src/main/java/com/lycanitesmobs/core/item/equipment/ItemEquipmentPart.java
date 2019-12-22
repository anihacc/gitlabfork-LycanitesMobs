package com.lycanitesmobs.core.item.equipment;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.BaseItem;
import com.lycanitesmobs.core.item.equipment.features.EquipmentFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

public class ItemEquipmentPart extends BaseItem {
	/** A map of mob classes and parts that they drop. **/
	public static Map<String, List<ItemEquipmentPart>> MOB_PART_DROPS = new HashMap<>();

	/** A list of all features this part has. **/
	public List<EquipmentFeature> features = new ArrayList<>();

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

	/** Loads this feature from a JSON object. **/
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


	// ==================================================
	//                      Info
	// ==================================================
	@Override
	public ITextComponent getDisplayName(ItemStack itemStack) {
		ITextComponent displayName = new TranslationTextComponent(this.getTranslationKey(itemStack).replace("equipmentpart_", ""));
		displayName.appendText(" ")
			.appendSibling(new TranslationTextComponent("equipment.level"))
			.appendText(" " + this.getLevel(itemStack));
		return displayName;
	}

	@Override
	public void addInformation(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {
		super.addInformation(itemStack, world, tooltip, tooltipFlag);
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		for(ITextComponent description : this.getAdditionalDescriptions(itemStack, world, tooltipFlag)) {
			List<String> formattedDescriptionList = fontRenderer.listFormattedStringToWidth("-------------------\n" + description.getFormattedText(), DESCRIPTION_WIDTH);
			for (String formattedDescription : formattedDescriptionList) {
				tooltip.add(new StringTextComponent(formattedDescription));
			}
		}
	}

	@Override
	public ITextComponent getDescription(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		return new TranslationTextComponent("item.equipmentpart.description");
	}

	public List<ITextComponent> getAdditionalDescriptions(ItemStack itemStack, @Nullable World world, ITooltipFlag tooltipFlag) {
		List<ITextComponent> descriptions = new ArrayList<>();
		int level = this.getLevel(itemStack);

		ITextComponent baseFeature = new TranslationTextComponent("equipment.slottype")
				.appendText(" " + this.slotType)
				.appendText("\n").appendSibling(new TranslationTextComponent("equipment.level"))
				.appendText(" " + level + "/" + this.levelMax);
		descriptions.add(baseFeature);

		for(EquipmentFeature feature : this.features) {
			ITextComponent featureDescription = feature.getDescription(itemStack, level);
			if(featureDescription != null && !"".equals(featureDescription.getFormattedText())) {
				descriptions.add(featureDescription);
			}
		}
		return descriptions;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		return super.getAttributeModifiers(slot, stack);
	}

	/** Gets or creates an NBT Compound for the provided itemstack. **/
	public CompoundNBT getTagCompound(ItemStack itemStack) {
		if(itemStack.hasTag()) {
			return itemStack.getTag();
		}
		return new CompoundNBT();
	}

	@OnlyIn(Dist.CLIENT)
	@Nullable
	@Override
	public net.minecraft.client.gui.FontRenderer getFontRenderer(ItemStack stack) {
		return ClientManager.getInstance().getFontRenderer();
	}


	// ==================================================
	//                   Equipment Part
	// ==================================================
	/** Sets up this equipment part, this is called when the provided stack is dropped and needs to have its level randomized, etc. **/
	public void initializePart(World world, ItemStack itemStack) {
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
		CompoundNBT nbt = this.getTagCompound(itemStack);
		if(!nbt.contains("equipmentLevel")) {
			nbt.putInt("equipmentLevel", level);
		}
		itemStack.setTag(nbt);
	}

	/** Returns an Equipment Part Level for the provided ItemStack. **/
	public int getLevel(ItemStack itemStack) {
		CompoundNBT nbt = this.getTagCompound(itemStack);
		int level = 1;
		if(nbt.contains("equipmentLevel")) {
			level = nbt.getInt("equipmentLevel");
		}
		return level;
	}

	/** Returns the dyed color for the provided ItemStack. **/
	public Vec3d getColor(ItemStack itemStack) {
		CompoundNBT nbt = this.getTagCompound(itemStack);
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
		return new Vec3d(r, g, b);
	}


	// ==================================================
	//                    Item Group
	// ==================================================
	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> items) {
		if(!this.isInGroup(tab)) {
			return;
		}

		for(int level = 1; level <= this.levelMax; level++) {
			ItemStack itemStack = new ItemStack(this, 1);
			this.setLevel(itemStack, level);
			items.add(itemStack);
		}
	}
}
