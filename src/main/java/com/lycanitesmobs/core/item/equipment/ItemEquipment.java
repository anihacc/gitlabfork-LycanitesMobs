package com.lycanitesmobs.core.item.equipment;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.features.EquipmentFeature;
import com.lycanitesmobs.core.item.equipment.features.HarvestEquipmentFeature;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemEquipment extends ItemBase {
	/** I am sorry, I couldn't find another way. Set in getMetadata(ItemStack) as it's called just before rendering. **/
	public static ItemStack ITEMSTACK_TO_RENDER;

	/** The maximum amount of parts that can be added to an Equipment Piece. **/
	public static int PART_LIMIT = 20;


	/**
	 * Constructor
	 */
	public ItemEquipment() {
		super();
		this.itemName = "equipment";
		this.group = LycanitesMobs.group;
		this.setRegistryName(this.group.filename, this.itemName);
		this.setUnlocalizedName(this.itemName);
		this.setMaxStackSize(1);
	}


	// ==================================================
	//                      Info
	// ==================================================
	@Override
	public void addInformation(ItemStack itemStack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
		super.addInformation(itemStack, world, tooltip, tooltipFlag);
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		for(String description : this.getAdditionalDescriptions(itemStack, world, tooltipFlag)) {
			List formattedDescriptionList = fontRenderer.listFormattedStringToWidth("-------------------\n" + description, descriptionWidth);
			for (Object formattedDescription : formattedDescriptionList) {
				if (formattedDescription instanceof String)
					tooltip.add("\u00a73" + formattedDescription);
			}
		}
	}

	@Override
	public String getDescription(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		return I18n.translateToLocal("item.equipment.description");
	}

	public List<String> getAdditionalDescriptions(ItemStack itemStack, @Nullable World world, ITooltipFlag tooltipFlag) {
		List<String> descriptions = new ArrayList<>();
		for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(itemStack)) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null)
				continue;
			descriptions.addAll(equipmentPart.getAdditionalDescriptions(equipmentPartStack, world, tooltipFlag));
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


	// ==================================================
	//                  Equipment Piece
	// ==================================================
	/**
	 * Returns a list of all Equipment Part ItemStacks for each slot.
	 * @param itemStack The Equipment ItemStack to get the Equipment Part ItemStacks from.
	 * @return The map of all Equipment Part ItemStacks for each slot.
	 */
	public NonNullList<ItemStack> getEquipmentPartStacks(ItemStack itemStack) {
		NonNullList<ItemStack> itemStacks = NonNullList.withSize(PART_LIMIT, ItemStack.EMPTY);
		NBTTagCompound nbt = this.getTagCompound(itemStack);
		if(nbt.hasKey("Items")) {
			ItemStackHelper.loadAllItems(nbt, itemStacks); // Reads ItemStacks into a List from "Items" tag.
		}
		return itemStacks;
	}


	/**
	 * Returns the Equipment Part for the provided ItemStack or null in empty or a different item.
	 * @param itemStack The Equipment Part ItemStack to get the Equipment Part from.
	 * @return The Equipment Part or null if the stack is empty.
	 */
	public ItemEquipmentPart getEquipmentPart(ItemStack itemStack) {
		if(itemStack.isEmpty()) {
			return null;
		}
		if(!(itemStack.getItem() instanceof ItemEquipmentPart)) {
			return null;
		}
		return (ItemEquipmentPart)itemStack.getItem();
	}


	/**
	 * Adds an Equipment Part ItemStack to the provided Equipment ItemStack.
	 * @param equipmentStack The Equipment ItemStack to add a part to.
	 * @param equipmentPartStack The Equipment Part ItemStack to add.
	 * @param slotIndex The slot index to connect the part to.
	 */
	public void addEquipmentPart(ItemStack equipmentStack, ItemStack equipmentPartStack, int slotIndex) {
		if(slotIndex >= PART_LIMIT) {
			return;
		}
		NonNullList<ItemStack> itemStacks = this.getEquipmentPartStacks(equipmentStack);
		itemStacks.set(slotIndex, equipmentPartStack);
		NBTTagCompound nbt = this.getTagCompound(equipmentStack);
		ItemStackHelper.saveAllItems(nbt, itemStacks);
		equipmentStack.setTagCompound(nbt);
	}


	/**
	 * Searches for the provided active features by type and returns a list of them.
	 * @param equipmentStack The itemStack of the Equipment.
	 * @param featureType The type of feature to search for.
	 * @return A list of features.
	 */
	public List<EquipmentFeature> getFeaturesByType(ItemStack equipmentStack, String featureType) {
		List<EquipmentFeature> features = new ArrayList<>();
		for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null) {
				continue;
			}
			for (EquipmentFeature feature : equipmentPart.features) {
				if(equipmentPart.getLevel(equipmentStack) >= feature.levelMin && equipmentPart.getLevel(equipmentStack) <= feature.levelMax && feature.featureType.equalsIgnoreCase(featureType)) {
					features.add(feature);
				}
			}
		}
		return features;
	}


	// ==================================================
	//                       Usage
	// ==================================================
	@Override
	public boolean canHarvestBlock(IBlockState blockState, ItemStack itemStack) {
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			if(harvestFeature.canHarvestBlock(blockState)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public float getDestroySpeed(ItemStack itemStack, IBlockState blockState) {
		float speed = 1;
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			speed += harvestFeature.getHarvestSpeed(blockState);
		}
		return speed;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemStack, World worldIn, IBlockState blockState, BlockPos pos, EntityLivingBase entityLiving)
	{
		if(worldIn.isRemote) {
			return super.onBlockDestroyed(itemStack, worldIn, blockState, pos, entityLiving);
		}
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			harvestFeature.onBlockDestroyed(worldIn, blockState, pos, entityLiving);
		}
		return super.onBlockDestroyed(itemStack, worldIn, blockState, pos, entityLiving);
	}


	// ==================================================
	//                      Visuals
	// ==================================================
	@Override
	public ModelResourceLocation getModelResourceLocation() {
		return new ModelResourceLocation(new ResourceLocation(LycanitesMobs.modid, "equipmentpart"), "inventory");
	}

	/** Returns the texture to use for the provided ItemStack. **/
	public ResourceLocation getTexture(ItemStack itemStack) {
		return AssetManager.getTexture(this.itemName);
	}
}
