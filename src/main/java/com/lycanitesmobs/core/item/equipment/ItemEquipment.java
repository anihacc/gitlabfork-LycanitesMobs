package com.lycanitesmobs.core.item.equipment;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.item.BaseItem;
import com.lycanitesmobs.core.item.equipment.features.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ItemEquipment extends BaseItem {
	/** The maximum amount of parts that can be added to an Equipment Piece. **/
	public static int PART_LIMIT = 20;

	public static int SHARPNESS_MAX = 1500;
	public static int MANA_MAX = 1500;

	/**
	 * Constructor
	 */
	public ItemEquipment(Item.Properties properties) {
		super(properties);
		this.itemName = "equipment";
		this.modInfo = LycanitesMobs.modInfo;
		this.setRegistryName(this.modInfo.modid, this.itemName);
		properties.stacksTo(1);
	}


	// ==================================================
	//                      Info
	// ==================================================
	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, world, tooltip, tooltipFlag);
		Font fontRenderer = Minecraft.getInstance().font;
		for(Component description : this.getAdditionalDescriptions(itemStack, world, tooltipFlag)) {
			tooltip.add(description);
		}
	}

	@Override
	public Component getDescription(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		return new TextComponent("");
	}

	public List<Component> getAdditionalDescriptions(ItemStack itemStack, @Nullable Level world, TooltipFlag tooltipFlag) {
		List<Component> descriptions = new ArrayList<>();

		// Condition:
		descriptions.add(new TranslatableComponent("equipment.sharpness").append(" " + this.getSharpness(itemStack) + "/" + SHARPNESS_MAX).withStyle(ChatFormatting.BLUE));
		descriptions.add(new TranslatableComponent("equipment.mana").append(" " + this.getMana(itemStack) + "/" + MANA_MAX).withStyle(ChatFormatting.BLUE));
		descriptions.add(new TextComponent("-------------------"));

		// Part Names:
		for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(itemStack)) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null)
				continue;
			descriptions.add(equipmentPart.getName(itemStack).plainCopy().withStyle(ChatFormatting.DARK_GREEN));
		}
		descriptions.add(new TextComponent("-------------------"));

		// Damage:
		descriptions.add(new TranslatableComponent("equipment.feature.damage")
				.append(" " + String.format("%.0f", this.getDamageAmount(itemStack) + 1)).withStyle(ChatFormatting.GOLD));
		descriptions.add(new TranslatableComponent("equipment.feature.damage.cooldown")
				.append(" " + String.format("%.1f", this.getDamageCooldown(itemStack))).withStyle(ChatFormatting.GOLD));
		descriptions.add(new TranslatableComponent("equipment.feature.damage.knockback")
				.append(" " + String.format("%.0f", this.getDamageKnockback(itemStack))).withStyle(ChatFormatting.GOLD));
		descriptions.add(new TranslatableComponent("equipment.feature.damage.range")
				.append(" " + String.format("%.1f", this.getDamageRange(itemStack))).withStyle(ChatFormatting.GOLD));
		descriptions.add(new TranslatableComponent("equipment.feature.damage.sweep")
				.append(" " + String.format("%.0f", Math.min(this.getDamageSweep(itemStack), 360))).withStyle(ChatFormatting.GOLD));

		// Summaries:
		Component harvestSummaries = this.getFeatureSummaries(itemStack, "harvest");
		Component effectSummaries = this.getFeatureSummaries(itemStack, "effect");
		Component projectileSummaries = this.getFeatureSummaries(itemStack, "projectile");
		Component summonSummaries = this.getFeatureSummaries(itemStack, "summon");
		if(!"".equals(harvestSummaries.getString()) || !"".equals(effectSummaries.getString()) || !"".equals(projectileSummaries.getString()) || !"".equals(summonSummaries.getString())) {
			descriptions.add(new TextComponent("-------------------"));

			// Harvest:
			if (!"".equals(harvestSummaries.getString())) {
				descriptions.add(new TranslatableComponent("equipment.feature.harvest")
						.append(" ").append(harvestSummaries).withStyle(ChatFormatting.DARK_PURPLE));
			}

			// Effect:
			if (!"".equals(effectSummaries.getString())) {
				descriptions.add(new TranslatableComponent("equipment.feature.effect")
						.append(" ").append(effectSummaries).withStyle(ChatFormatting.DARK_PURPLE));
			}

			// Projectile:
			if (!"".equals(projectileSummaries.getString())) {
				descriptions.add(new TranslatableComponent("equipment.feature.projectile")
						.append(" ").append(projectileSummaries).withStyle(ChatFormatting.DARK_PURPLE));
			}

			// Summon:
			if (!"".equals(summonSummaries.getString())) {
				descriptions.add(new TranslatableComponent("equipment.feature.summon")
						.append(" ").append(summonSummaries).withStyle(ChatFormatting.DARK_PURPLE));
			}
		}

		return descriptions;
	}


	/**
	 * Gets comma separated text of the description summary of the provided feature type.
	 * @param itemStack The Equipment Piece itemstack to get parts and features from.
	 * @param featureType The feature type to get the summaries of.
	 * @return A string of summaries, empty if none are of the type are found.
	 */
	public Component getFeatureSummaries(ItemStack itemStack, String featureType) {
		Map<EquipmentFeature, ItemStack> effectFeatures = this.getFeaturesByTypeWithPartStack(itemStack, featureType);
		BaseComponent featureSummaries = new TextComponent("");
		boolean first = true;
		for (EquipmentFeature equipmentFeature : effectFeatures.keySet()) {
			if(!first) {
				featureSummaries.append(", ");
			}
			first = false;
			Component featureSummary = equipmentFeature.getSummary(effectFeatures.get(equipmentFeature), this.getPartLevel(effectFeatures.get(equipmentFeature)));
			if(featureSummary != null) {
				featureSummaries.append(featureSummary);
			}
		}
		return featureSummaries;
	}

	@Override
	public boolean showDurabilityBar(ItemStack itemStack) {
		return (double)this.getSharpness(itemStack) < SHARPNESS_MAX;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack itemStack) {
		return 1D - (double)this.getSharpness(itemStack) / SHARPNESS_MAX;
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
		CompoundTag nbt = this.getTagCompound(itemStack);
		if(nbt.contains("Items")) {
			ContainerHelper.loadAllItems(nbt, itemStacks); // Reads ItemStacks into a List from "Items" tag.
		}
		return itemStacks;
	}

	/**
	 * Counts how many Equipment Parts this Piece is made out of.
	 * @param itemStack The Equipment ItemStack to count the Equipment Part ItemStacks from.
	 * @return How many Equipment Parts make this Equipment Piece.
	 */
	public int getEquipmentPartCount(ItemStack itemStack) {
		int partCount = 0;
		for(ItemStack partStack : this.getEquipmentPartStacks(itemStack)) {
			if(!partStack.isEmpty()) {
				partCount++;
			}
		}
		return partCount;
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
		CompoundTag nbt = this.getTagCompound(equipmentStack);
		ContainerHelper.saveAllItems(nbt, itemStacks);
		equipmentStack.setTag(nbt);
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
				if(feature.isActive(equipmentPartStack, equipmentPart.getPartLevel(equipmentPartStack)) && feature.featureType.equalsIgnoreCase(featureType)) {
					features.add(feature);
				}
			}
		}
		return features;
	}

	/**
	 * Searches for the provided active features by type and returns a map of them with the feature as the key and the stack they are from as the value.
	 * @param equipmentStack The itemStack of the Equipment.
	 * @param featureType The type of feature to search for.
	 * @return A list of features.
	 */
	public Map<EquipmentFeature, ItemStack> getFeaturesByTypeWithPartStack(ItemStack equipmentStack, String featureType) {
		Map<EquipmentFeature, ItemStack> features = new HashMap<>();
		for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null) {
				continue;
			}
			for (EquipmentFeature feature : equipmentPart.features) {
				if(feature.isActive(equipmentPartStack, equipmentPart.getPartLevel(equipmentPartStack)) && feature.featureType.equalsIgnoreCase(featureType)) {
					features.put(feature, equipmentPartStack);
				}
			}
		}
		return features;
	}

	/**
	 * Cycles through each Equipment Part and lowers their level to the provided level cap, used by lower level Forges.
	 * @param equipmentStack The itemStack of the Equipment.
	 * @param levelCap The level cap to lower parts to.
	 */
	public void applyLevelCap(ItemStack equipmentStack, int levelCap) {
		for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null) {
				continue;
			}
			equipmentPart.setLevel(equipmentPartStack, Math.min(levelCap, equipmentPart.getPartLevel(equipmentPartStack)));
		}
	}

	/**
	 * Cycles through each Equipment Part returns the highest part level found.
	 * @param equipmentStack The itemStack of the Equipment.
	 * @return The highest part level found.
	 */
	public int getHighestLevel(ItemStack equipmentStack) {
		int highestLevel = 0;
		for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null) {
				continue;
			}
			int partLevel = equipmentPart.getPartLevel(equipmentPartStack);
			if(partLevel > highestLevel) {
				highestLevel = partLevel;
			}
		}
		return highestLevel;
	}

	/**
	 * Gets the Equipment Part level from the provided itemstack.
	 * @param partStack The itemstack to get a part level from.
	 * @return The part level or 1 if the itemstack is invalid.
	 */
	public int getPartLevel(ItemStack partStack) {
		Item featureItem = partStack.getItem();
		if(!(featureItem instanceof ItemEquipmentPart)) {
			return 1;
		}
		ItemEquipmentPart featurePart = (ItemEquipmentPart)featureItem;
		return featurePart.getPartLevel(partStack);
	}

	/**
	 * Determines the Sharpness of this Equipment using the part with the least amount of Sharpness.
	 * @param equipmentStack The itemStack of the Equipment.
	 * @return The lowest part sharpness found.
	 */
	public int getSharpness(ItemStack equipmentStack) {
		int lowestSharpness = SHARPNESS_MAX;
		for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null) {
				continue;
			}
			int partSharpness = equipmentPart.getSharpness(equipmentPartStack);
			if(partSharpness < lowestSharpness) {
				lowestSharpness = partSharpness;
			}
		}
		return lowestSharpness;
	}

	/**
	 * Increases the Sharpness of this Equipment by increasing the Sharpness of all parts.
	 * @param equipmentStack The itemStack of the Equipment.
	 * @return True if Sharpness was increased at all.
	 */
	public boolean addSharpness(ItemStack equipmentStack, int sharpness) {
		boolean sharpnessIncreased = false;
		NonNullList<ItemStack> itemStacks = this.getEquipmentPartStacks(equipmentStack);
		for(ItemStack equipmentPartStack : itemStacks) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null) {
				continue;
			}
			sharpnessIncreased = equipmentPart.addSharpness(equipmentPartStack, sharpness) || sharpnessIncreased;
		}

		CompoundTag nbt = this.getTagCompound(equipmentStack);
		ContainerHelper.saveAllItems(nbt, itemStacks);
		equipmentStack.setTag(nbt);
		return sharpnessIncreased;
	}

	/**
	 * Decreases the Sharpness of this Equipment by decreasing the Sharpness of all parts.
	 * @param equipmentStack The itemStack of the Equipment.
	 * @return True if Sharpness was decreased at all.
	 */
	public boolean removeSharpness(ItemStack equipmentStack, int sharpness) {
		boolean sharpnessDecreased = false;
		NonNullList<ItemStack> itemStacks = this.getEquipmentPartStacks(equipmentStack);
		for(ItemStack equipmentPartStack : itemStacks) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null) {
				continue;
			}
			sharpnessDecreased = equipmentPart.removeSharpness(equipmentPartStack, sharpness) || sharpnessDecreased;
		}

		CompoundTag nbt = this.getTagCompound(equipmentStack);
		ContainerHelper.saveAllItems(nbt, itemStacks);
		equipmentStack.setTag(nbt);
		return sharpnessDecreased;
	}

	/**
	 * Determines the Mana of this Equipment using the part with the least amount of Mana.
	 * @param equipmentStack The itemStack of the Equipment.
	 * @return The lowest part mana found.
	 */
	public int getMana(ItemStack equipmentStack) {
		int lowestMana = MANA_MAX;
		for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null) {
				continue;
			}
			int partMana = equipmentPart.getMana(equipmentPartStack);
			if(partMana < lowestMana) {
				lowestMana = partMana;
			}
		}
		return lowestMana;
	}

	/**
	 * Increases the Mana of this Equipment by increasing the Mana of all parts.
	 * @param equipmentStack The itemStack of the Equipment.
	 * @return True if Mana was increased at all.
	 */
	public boolean addMana(ItemStack equipmentStack, int mana) {
		boolean manaIncreased = false;
		NonNullList<ItemStack> itemStacks = this.getEquipmentPartStacks(equipmentStack);
		for(ItemStack equipmentPartStack : itemStacks) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null) {
				continue;
			}
			manaIncreased = equipmentPart.addMana(equipmentPartStack, mana) || manaIncreased;
		}

		CompoundTag nbt = this.getTagCompound(equipmentStack);
		ContainerHelper.saveAllItems(nbt, itemStacks);
		equipmentStack.setTag(nbt);
		return manaIncreased;
	}

	/**
	 * Decreases the Mana of this Equipment by decreasing the Mana of all parts.
	 * @param equipmentStack The itemStack of the Equipment.
	 * @return True if Mana was decreased at all.
	 */
	public boolean removeMana(ItemStack equipmentStack, int mana) {
		boolean manaDecreased = false;
		NonNullList<ItemStack> itemStacks = this.getEquipmentPartStacks(equipmentStack);
		for(ItemStack equipmentPartStack : itemStacks) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null) {
				continue;
			}
			manaDecreased = equipmentPart.removeMana(equipmentPartStack, mana) || manaDecreased;
		}

		CompoundTag nbt = this.getTagCompound(equipmentStack);
		ContainerHelper.saveAllItems(nbt, itemStacks);
		equipmentStack.setTag(nbt);
		return manaDecreased;
	}


	// ==================================================
	//                       Using
	// ==================================================
	@Override
	public UseAnim getUseAnimation(ItemStack itemStack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 1800;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		if(this.getMana(itemStack) <= 0) {
			return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
		}
		boolean active = false;

		// Projectiles:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
			ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
			if(projectileFeature.onUseSecondary(world, player, hand)) {
				active = true;
			}
		}

		if(active) {
			player.startUsingItem(hand);
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
		}
		return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
	}

	@Override
	public void onUsingTick(ItemStack itemStack, LivingEntity user, int count) {
		if(!user.isUsingItem() || this.getMana(itemStack) <= 0) {
			return;
		}
		boolean usedMana = false;

		// Projectiles:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
			ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
			usedMana = projectileFeature.onHoldSecondary(user, count) || usedMana;
		}

		if (usedMana) {
			this.removeMana(itemStack, 1);
		}
	}

	/**
	 * Called when the player left clicks with this equipment. This is called via the left click empty or block events (a network packet is called for left click empty).
	 * @param world The world the player is in.
	 * @param player The player using the equipment.
	 * @param hand The active hand.
	 */
	public void onItemLeftClick(Level world, Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		if(this.getMana(itemStack) <= 0) {
			return;
		}
		boolean usedMana = false;

		// Projectiles:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
			ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
			usedMana = projectileFeature.onUsePrimary(world, player, hand) || usedMana;
		}

		if (usedMana) {
			this.removeMana(itemStack, 1);
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		boolean active = false;
		ItemStack itemStack = context.getItemInHand();
		if(this.getMana(itemStack) <= 0) {
			return super.useOn(context);
		}

		// Harvesting:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			if(harvestFeature.onBlockUsed(context)) {
				active = true;
			}
		}

		if(active) {
			context.getPlayer().startUsingItem(context.getHand());
			this.removeSharpness(itemStack, 1);
			return InteractionResult.SUCCESS;
		}
		return super.useOn(context);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity target, InteractionHand hand) {
		boolean entityInteraction = false;

		// Harvesting:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			if(harvestFeature.onEntityInteraction(player, target, itemStack)) {
				entityInteraction = true;
			}
		}

		if(entityInteraction) {
			this.removeSharpness(itemStack, 1);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}


	// ==================================================
	//                     Harvesting
	// ==================================================
	@Override
	public boolean canPerformAction(ItemStack itemStack, net.minecraftforge.common.ToolAction toolAction) {
		if(this.getSharpness(itemStack) <= 0) {
			return false;
		}
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			if(harvestFeature.getToolActions().contains(toolAction)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack itemStack, BlockState blockState) {
		if(this.getSharpness(itemStack) <= 0) {
			return false;
		}

		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			if(harvestFeature.canHarvestBlock(blockState)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
		if(this.getSharpness(itemStack) <= 0) {
			return 1;
		}

		float speed = 1;
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			speed += harvestFeature.getHarvestSpeed(blockState);
		}
		return speed;
	}

	@Override
	public boolean mineBlock(ItemStack itemStack, Level world, BlockState blockState, BlockPos pos, LivingEntity entityLiving) {
		if(this.getSharpness(itemStack) <= 0) {
			return super.mineBlock(itemStack, world, blockState, pos, entityLiving);
		}
		if(!world.isClientSide) {
			for (EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
				HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature) equipmentFeature;
				harvestFeature.onBlockDestroyed(world, blockState, pos, entityLiving);
			}
		}
		this.removeSharpness(itemStack, 1);
		return super.mineBlock(itemStack, world, blockState, pos, entityLiving);
	}


	// ==================================================
	//                      Damaging
	// ==================================================
	/**
	 * Called when an entity is hit with this item.
	 * @param itemStack The ItemStack being hit with.
	 * @param primaryTarget The target entity being hit.
	 * @param attacker The entity using this item to hit.
	 * @return True on successful hit.
	 */
	@Override
	public boolean hurtEnemy(ItemStack itemStack, LivingEntity primaryTarget, LivingEntity attacker) {
		if(this.getSharpness(itemStack) <= 0) {
			return true;
		}

		ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(attacker);
		boolean attackOnCooldown = false;
		if(extendedEntity != null) {
			int currentCooldown = extendedEntity.getProjectileCooldown(1, "equipment_melee");
			int weaponCooldown = 9 * (int)this.getDamageCooldown(itemStack);
			attackOnCooldown = currentCooldown > 0;
			if(currentCooldown < weaponCooldown) {
				extendedEntity.setProjectileCooldown(1, "equipment_melee", weaponCooldown);
			}
		}

		List<LivingEntity> targets = new ArrayList<>();
		targets.add(primaryTarget);

		// Sweeping:
		if(!attacker.getCommandSenderWorld().isClientSide && !attacker.isShiftKeyDown() && !attackOnCooldown) {
			double sweepAngle = this.getDamageSweep(itemStack);
			if(sweepAngle > 0) {
				float sweepDamage = (float) this.getDamageAmount(itemStack);
				double sweepRange = 1 + this.getDamageRange(itemStack);
				List<LivingEntity> possibleTargets = attacker.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, attacker.getBoundingBox().inflate(sweepRange, sweepRange, sweepRange));
				for (LivingEntity possibleTarget : possibleTargets) {
					// Valid Sweep Target:
					if (possibleTarget == attacker || possibleTarget == primaryTarget) {
						continue;
					}
					if (!possibleTarget.isAlive()) {
						continue;
					}
					if (possibleTarget instanceof Player) {
						continue;
					}
					if (possibleTarget instanceof TamableAnimal) {
						TamableAnimal possibleTameableTarget = (TamableAnimal) possibleTarget;
						if (possibleTameableTarget.getOwner() != null && !attacker.getCommandSenderWorld().getServer().isPvpAllowed()) {
							continue;
						}
						if (possibleTameableTarget.getOwner() == attacker) {
							continue;
						}
					}
					if (possibleTarget instanceof TameableCreatureEntity) {
						TameableCreatureEntity possibleTameableTarget = (TameableCreatureEntity) possibleTarget;
						if (possibleTameableTarget.getPlayerOwner() != null && !attacker.getCommandSenderWorld().getServer().isPvpAllowed()) {
							continue;
						}
						if (possibleTameableTarget.getPlayerOwner() == attacker) {
							continue;
						}
					}

					// Check Angle:
					double targetXDist = possibleTarget.position().x() - attacker.position().x();
					double targetZDist = attacker.position().z() - possibleTarget.position().z();
					double targetAngleAbsolute = 180 + Math.toDegrees(Math.atan2(targetXDist, targetZDist));
					double targetAngle = Math.abs(targetAngleAbsolute - attacker.getYRot());
					if(targetAngle > 180) {
						targetAngle = 180 - (targetAngle - 180);
					}
					if(targetAngle > sweepAngle) {
						continue;
					}

					targets.add(possibleTarget);
					DamageSource sweepSource = DamageSource.GENERIC;
					if (attacker instanceof Player) {
						sweepSource = DamageSource.playerAttack((Player) attacker);
					}
					possibleTarget.hurt(sweepSource, sweepDamage);
				}
			}
		}

		// Sweep Particles:
		if(attacker instanceof Player && targets.size() > 1) {
			Player playerAttacker = (Player)attacker;
			playerAttacker.sweepAttack();
			attacker.getCommandSenderWorld().playSound(null, attacker.position().x(), attacker.position().y(), attacker.position().z(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.0F, 1.0F);
		}

		boolean usedMana = false;
		for(LivingEntity target : targets) {
			// Knockback:
			double knockback = this.getDamageKnockback(itemStack);
			if (knockback != 0 && attacker != null && target != null) {
				double xDist = attacker.position().x() - target.position().x();
				double zDist = attacker.position().z() - target.position().z();
				double xzDist = Math.max(Mth.sqrt((float)(xDist * xDist + zDist * zDist)), 0.01D);
				double motionCap = 10;
				if (target.getDeltaMovement().x() < motionCap && target.getDeltaMovement().x() > -motionCap && target.getDeltaMovement().z() < motionCap && target.getDeltaMovement().z() > -motionCap) {
					target.push(
							-(xDist / xzDist * knockback + target.getDeltaMovement().x() * knockback),
							0,
							-(zDist / xzDist * knockback + target.getDeltaMovement().z() * knockback)
					);
				}
			}

			// Effects:
			for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "effect")) {
				EffectEquipmentFeature effectFeature = (EffectEquipmentFeature)equipmentFeature;
				effectFeature.onHitEntity(itemStack, target, attacker);
			}

			// Summons:
			for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "summon")) {
				SummonEquipmentFeature summonFeature = (SummonEquipmentFeature)equipmentFeature;
				usedMana = summonFeature.onHitEntity(itemStack, target, attacker) || usedMana;
			}

			// Projectiles:
			for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
				ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
				usedMana = projectileFeature.onHitEntity(itemStack, target, attacker) || usedMana;
			}
		}

		if (usedMana) {
			this.removeMana(itemStack, 1);
		}
		this.removeSharpness(itemStack, 1);

		return true;
	}

	/**
	 * Adds attribute modifiers provided by this equipment.
	 * @param slot The slot that this equipment is in.
	 * @return The Attribute Modifier multimap with changes applied to it.
	 */
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack itemStack) {
		Multimap<Attribute, AttributeModifier> multimap = MultimapBuilder.hashKeys().arrayListValues().build(super.getAttributeModifiers(slot, itemStack));
		if (slot == EquipmentSlot.MAINHAND) {
			multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.getDamageAmount(itemStack), AttributeModifier.Operation.ADDITION));
			multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -this.getDamageCooldown(itemStack), AttributeModifier.Operation.ADDITION));
		}

		return multimap;
	}

	/**
	 * Returns how much damage this equipment will do.
	 * @param itemStack The equipment ItemStack.
	 * @return The amount of base damage.
	 */
	public double getDamageAmount(ItemStack itemStack) {
		if(this.getSharpness(itemStack) <= 0) {
			return 0;
		}

		double damage = 0;
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "damage")) {
			DamageEquipmentFeature damageFeature = (DamageEquipmentFeature)equipmentFeature;
			damage += damageFeature.damageAmount;
		}
		return damage;
	}

	/**
	 * Returns the attack cooldown of this equipment.
	 * @param itemStack The equipment ItemStack.
	 * @return The amount of cooldown in ticks.
	 */
	public double getDamageCooldown(ItemStack itemStack) {
		double cooldown = 0;
		double i = 0;
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "damage")) {
			DamageEquipmentFeature damageFeature = (DamageEquipmentFeature)equipmentFeature;
			cooldown += damageFeature.damageCooldown;
			i++;
		}

		if(i == 0) {
			return 2.4D;
		}

		cooldown = 2.4D * (cooldown / i);
		return Math.min(3.5D, cooldown);
	}

	/**
	 * Returns the attack range of this equipment.
	 * @param itemStack The equipment ItemStack.
	 * @return The amount of range.
	 */
	public double getDamageRange(ItemStack itemStack) {
		double range = 0;
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "damage")) {
			DamageEquipmentFeature damageFeature = (DamageEquipmentFeature)equipmentFeature;
			range += damageFeature.damageRange;
		}
		return range;
	}

	/**
	 * Returns the attack knockback of this equipment.
	 * @param itemStack The equipment ItemStack.
	 * @return The amount of knockback.
	 */
	public double getDamageKnockback(ItemStack itemStack) {
		if(this.getSharpness(itemStack) <= 0) {
			return 0;
		}

		double knockback = 0;
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "damage")) {
			DamageEquipmentFeature damageFeature = (DamageEquipmentFeature)equipmentFeature;
			knockback += damageFeature.damageKnockback;
		}
		return knockback;
	}

	/**
	 * Returns the attack sweep angle of this equipment.
	 * @param itemStack The equipment ItemStack.
	 * @return The amount of knockback.
	 */
	public double getDamageSweep(ItemStack itemStack) {
		if(this.getSharpness(itemStack) <= 0) {
			return 0;
		}

		double sweep = 0;
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "damage")) {
			DamageEquipmentFeature damageFeature = (DamageEquipmentFeature)equipmentFeature;
			sweep += damageFeature.damageSweep;
		}
		return sweep;
	}


	// ==================================================
	//                      Visuals
	// ==================================================
	/** Returns the texture to use for the provided ItemStack. **/
	public ResourceLocation getTexture(ItemStack itemStack) {
		return TextureManager.getTexture(this.itemName);
	}
}
