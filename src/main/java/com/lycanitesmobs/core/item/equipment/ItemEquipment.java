package com.lycanitesmobs.core.item.equipment;

import com.google.common.collect.Multimap;
import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.item.BaseItem;
import com.lycanitesmobs.core.item.equipment.features.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ItemEquipment extends BaseItem {
	/** The maximum amount of parts that can be added to an Equipment Piece. **/
	public static int PART_LIMIT = 20;


	/**
	 * Constructor
	 */
	public ItemEquipment(Item.Properties properties) {
		super(properties);
		this.itemName = "equipment";
		this.modInfo = LycanitesMobs.modInfo;
		this.setRegistryName(this.modInfo.modid, this.itemName);
		properties.maxStackSize(1);
	}


	// ==================================================
	//                      Info
	// ==================================================
	@Override
	public void addInformation(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {
		super.addInformation(itemStack, world, tooltip, tooltipFlag);
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		for(ITextComponent description : this.getAdditionalDescriptions(itemStack, world, tooltipFlag)) {
			List<String> formattedDescriptionList = fontRenderer.listFormattedStringToWidth(description.getFormattedText(), DESCRIPTION_WIDTH + 100);
			for (String formattedDescription : formattedDescriptionList) {
				tooltip.add(new StringTextComponent(formattedDescription));
			}
		}
	}

	@Override
	public ITextComponent getDescription(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		return new StringTextComponent("");
	}

	public List<ITextComponent> getAdditionalDescriptions(ItemStack itemStack, @Nullable World world, ITooltipFlag tooltipFlag) {
		List<ITextComponent> descriptions = new ArrayList<>();

		// Part Names:
		for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(itemStack)) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null)
				continue;
			int partLevel = equipmentPart.getLevel(equipmentPartStack);
			descriptions.add(equipmentPart.getDisplayName(itemStack));
		}
		descriptions.add(new StringTextComponent("-------------------\n"));

		// Damage:
		ITextComponent damageDescription = new TranslationTextComponent("equipment.feature.damage")
				.appendText(" " + String.format("%.0f", this.getDamageAmount(itemStack) + 1));
		damageDescription.appendText("\n")
				.appendSibling(new TranslationTextComponent("equipment.feature.damage.cooldown"))
				.appendText(" " + String.format("%.1f", this.getDamageCooldown(itemStack)));
		damageDescription.appendText("\n")
				.appendSibling(new TranslationTextComponent("equipment.feature.damage.knockback"))
				.appendText(" " + String.format("%.0f", this.getDamageKnockback(itemStack)));
		damageDescription.appendText("\n")
				.appendSibling(new TranslationTextComponent("equipment.feature.damage.range"))
				.appendText(" " + String.format("%.1f", this.getDamageRange(itemStack)));
		damageDescription.appendText("\n")
				.appendSibling(new TranslationTextComponent("equipment.feature.damage.sweep"))
				.appendText(" " + String.format("%.0f", Math.min(this.getDamageSweep(itemStack), 360)));
		descriptions.add(damageDescription);

		// Summaries:
		ITextComponent harvestSummaries = this.getFeatureSummaries(itemStack, "harvest");
		ITextComponent effectSummaries = this.getFeatureSummaries(itemStack, "effect");
		ITextComponent projectileSummaries = this.getFeatureSummaries(itemStack, "projectile");
		ITextComponent summonSummaries = this.getFeatureSummaries(itemStack, "summon");
		if(!"".equals(harvestSummaries.getFormattedText()) || !"".equals(effectSummaries.getFormattedText()) || !"".equals(projectileSummaries.getFormattedText()) || !"".equals(summonSummaries.getFormattedText())) {
			descriptions.add(new StringTextComponent("-------------------\n"));

			// Harvest:
			if (!"".equals(harvestSummaries.getFormattedText())) {
				descriptions.add(new TranslationTextComponent("equipment.feature.harvest")
						.appendText(" ").appendSibling(harvestSummaries));
			}

			// Effect:
			if (!"".equals(effectSummaries.getFormattedText())) {
				descriptions.add(new TranslationTextComponent("equipment.feature.effect")
						.appendText(" ").appendSibling(effectSummaries));
			}

			// Projectile:
			if (!"".equals(projectileSummaries.getFormattedText())) {
				descriptions.add(new TranslationTextComponent("equipment.feature.projectile")
						.appendText(" ").appendSibling(projectileSummaries));
			}

			// Summon:
			if (!"".equals(summonSummaries.getFormattedText())) {
				descriptions.add(new TranslationTextComponent("equipment.feature.summon")
						.appendText(" ").appendSibling(summonSummaries));
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
	public ITextComponent getFeatureSummaries(ItemStack itemStack, String featureType) {
		Map<EquipmentFeature, ItemStack> effectFeatures = this.getFeaturesByTypeWithPartStack(itemStack, featureType);
		ITextComponent featureSummaries = new StringTextComponent("");
		boolean first = true;
		for (EquipmentFeature equipmentFeature : effectFeatures.keySet()) {
			if(!first) {
				featureSummaries.appendText(", ");
			}
			first = false;
			ITextComponent featureSummary = equipmentFeature.getSummary(effectFeatures.get(equipmentFeature), this.getPartLevel(effectFeatures.get(equipmentFeature)));
			if(featureSummary != null) {
				featureSummaries.appendSibling(featureSummary);
			}
		}
		return featureSummaries;
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
	//                  Equipment Piece
	// ==================================================
	/**
	 * Returns a list of all Equipment Part ItemStacks for each slot.
	 * @param itemStack The Equipment ItemStack to get the Equipment Part ItemStacks from.
	 * @return The map of all Equipment Part ItemStacks for each slot.
	 */
	public NonNullList<ItemStack> getEquipmentPartStacks(ItemStack itemStack) {
		NonNullList<ItemStack> itemStacks = NonNullList.withSize(PART_LIMIT, ItemStack.EMPTY);
		CompoundNBT nbt = this.getTagCompound(itemStack);
		if(nbt.contains("Items")) {
			ItemStackHelper.loadAllItems(nbt, itemStacks); // Reads ItemStacks into a List from "Items" tag.
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
		CompoundNBT nbt = this.getTagCompound(equipmentStack);
		ItemStackHelper.saveAllItems(nbt, itemStacks);
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
				if(feature.isActive(equipmentPartStack, equipmentPart.getLevel(equipmentPartStack)) && feature.featureType.equalsIgnoreCase(featureType)) {
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
				if(feature.isActive(equipmentPartStack, equipmentPart.getLevel(equipmentPartStack)) && feature.featureType.equalsIgnoreCase(featureType)) {
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
			equipmentPart.setLevel(equipmentPartStack, Math.min(levelCap, equipmentPart.getLevel(equipmentPartStack)));
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
			int partLevel = equipmentPart.getLevel(equipmentPartStack);
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
		return featurePart.getLevel(partStack);
	}


	// ==================================================
	//                       Using
	// ==================================================
	@Override
	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 1800;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
		boolean active = false;

		// Projectiles:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
			ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
			if(projectileFeature.onUseSecondary(world, player, hand)) {
				active = true;
			}
		}

		if(active) {
			player.setActiveHand(hand);
			return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
		}
		return new ActionResult<>(ActionResultType.FAIL, itemStack);
	}

	@Override
	public void onUsingTick(ItemStack itemStack, LivingEntity user, int count) {
		if(!user.isHandActive()) {
			return;
		}

		// Projectiles:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
			ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
			projectileFeature.onHoldSecondary(user, count);
		}
	}

	/**
	 * Called when the player left clicks with this equipment. This is called via the left click empty or block events (a network packet is called for left click empty).
	 * @param world The world the player is in.
	 * @param player The player using the equipment.
	 * @param hand The active hand.
	 */
	public void onItemLeftClick(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getHeldItem(hand);

		// Projectiles:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
			ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
			projectileFeature.onUsePrimary(world, player, hand);
		}
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		boolean active = false;

		// Harvesting:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(context.getItem(), "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			if(harvestFeature.onBlockUsed(context)) {
				active = true;
			}
		}

		if(active) {
			context.getPlayer().setActiveHand(context.getHand());
			return ActionResultType.SUCCESS;
		}
		return super.onItemUse(context);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack itemStack, PlayerEntity player, LivingEntity target, Hand hand) {
		boolean entityInteraction = false;

		// Harvesting:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			if(harvestFeature.onEntityInteraction(player, target, itemStack)) {
				entityInteraction = true;
			}
		}

		if(entityInteraction)
			return true;
		return false;
	}


	// ==================================================
	//                     Harvesting
	// ==================================================
	@Override
	@Nonnull
	public Set<ToolType> getToolTypes(ItemStack itemStack) {
		Map<ToolType, Boolean> toolTypes = new HashMap<>();
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			ToolType toolType = harvestFeature.getToolType();
			if(toolType != null) {
				toolTypes.put(toolType, true);
			}
		}
		return toolTypes.keySet();
	}

	@Override
	public int getHarvestLevel(ItemStack itemStack, ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
		int harvestLevel = -1;
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			if(harvestLevel < harvestFeature.harvestLevel && tool == harvestFeature.getToolType()) {
				harvestLevel = harvestFeature.harvestLevel;
			}
		}
		return harvestLevel;
	}

	@Override
	public boolean canHarvestBlock(ItemStack itemStack, BlockState blockState) {
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
		float speed = 1;
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			speed += harvestFeature.getHarvestSpeed(blockState);
		}
		return speed;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemStack, World world, BlockState blockState, BlockPos pos, LivingEntity entityLiving) {
		if(!world.isRemote) {
			for (EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
				HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature) equipmentFeature;
				harvestFeature.onBlockDestroyed(world, blockState, pos, entityLiving);
			}
		}
		return super.onBlockDestroyed(itemStack, world, blockState, pos, entityLiving);
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
	public boolean hitEntity(ItemStack itemStack, LivingEntity primaryTarget, LivingEntity attacker) {
		ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(attacker);
		boolean attackOnCooldown = false;
		if(extendedEntity != null) {
			int currentCooldown = extendedEntity.getProjectileCooldown(1, "equipment_melee");
			int weaponCooldown = 10 * (int)this.getDamageCooldown(itemStack);
			attackOnCooldown = currentCooldown > 0;
			if(currentCooldown < weaponCooldown) {
				extendedEntity.setProjectileCooldown(1, "equipment_melee", weaponCooldown);
			}
		}

		// Sweeping:
		List<LivingEntity> targets = new ArrayList<>();
		targets.add(primaryTarget);
		if(attacker != null && !attacker.getEntityWorld().isRemote && !attacker.isShiftKeyDown() && !attackOnCooldown) {
			double sweepAngle = this.getDamageSweep(itemStack);
			if(sweepAngle > 0) {
				float sweepDamage = (float) this.getDamageAmount(itemStack);
				double sweepRange = 1 + this.getDamageRange(itemStack);
				List<LivingEntity> possibleTargets = attacker.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, attacker.getBoundingBox().grow(sweepRange, sweepRange, sweepRange));
				for (LivingEntity possibleTarget : possibleTargets) {
					// Valid Sweep Target:
					if (possibleTarget == attacker || possibleTarget == primaryTarget) {
						continue;
					}
					if (!possibleTarget.isAlive()) {
						continue;
					}
					if (possibleTarget instanceof PlayerEntity) {
						continue;
					}
					if (possibleTarget instanceof TameableEntity) {
						TameableEntity possibleTameableTarget = (TameableEntity) possibleTarget;
						if (possibleTameableTarget.getOwner() != null && !attacker.getEntityWorld().getServer().isPVPEnabled()) {
							continue;
						}
						if (possibleTameableTarget.getOwner() == attacker) {
							continue;
						}
					}
					if (possibleTarget instanceof TameableCreatureEntity) {
						TameableCreatureEntity possibleTameableTarget = (TameableCreatureEntity) possibleTarget;
						if (possibleTameableTarget.getPlayerOwner() != null && !attacker.getEntityWorld().getServer().isPVPEnabled()) {
							continue;
						}
						if (possibleTameableTarget.getPlayerOwner() == attacker) {
							continue;
						}
					}

					// Check Angle:
					double targetXDist = possibleTarget.getPositionVec().getX() - attacker.getPositionVec().getX();
					double targetZDist = attacker.getPositionVec().getZ() - possibleTarget.getPositionVec().getZ();
					double targetAngleAbsolute = 180 + Math.toDegrees(Math.atan2(targetXDist, targetZDist));
					double targetAngle = Math.abs(targetAngleAbsolute - attacker.rotationYaw);
					if(targetAngle > 180) {
						targetAngle = 180 - (targetAngle - 180);
					}
					if(targetAngle > sweepAngle) {
						continue;
					}

					targets.add(possibleTarget);
					DamageSource sweepSource = DamageSource.GENERIC;
					if (attacker instanceof PlayerEntity) {
						sweepSource = DamageSource.causePlayerDamage((PlayerEntity) attacker);
					}
					possibleTarget.attackEntityFrom(sweepSource, sweepDamage);
				}
			}
		}

		// Sweep Particles:
		if(attacker instanceof PlayerEntity && targets.size() > 1) {
			PlayerEntity playerAttacker = (PlayerEntity)attacker;
			playerAttacker.spawnSweepParticles();
			attacker.getEntityWorld().playSound(null, attacker.getPositionVec().getX(), attacker.getPositionVec().getY(), attacker.getPositionVec().getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0F, 1.0F);
		}

		for(LivingEntity target : targets) {
			// Knockback:
			double knockback = this.getDamageKnockback(itemStack);
			if (knockback != 0 && attacker != null && target != null) {
				double xDist = attacker.getPositionVec().getX() - target.getPositionVec().getX();
				double zDist = attacker.getPositionVec().getZ() - target.getPositionVec().getZ();
				double xzDist = Math.max(MathHelper.sqrt(xDist * xDist + zDist * zDist), 0.01D);
				double motionCap = 10;
				if (target.getMotion().getX() < motionCap && target.getMotion().getX() > -motionCap && target.getMotion().getZ() < motionCap && target.getMotion().getZ() > -motionCap) {
					target.addVelocity(
							-(xDist / xzDist * knockback + target.getMotion().getX() * knockback),
							0,
							-(zDist / xzDist * knockback + target.getMotion().getZ() * knockback)
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
				summonFeature.onHitEntity(itemStack, target, attacker);
			}

			// Projectiles:
			for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
				ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
				projectileFeature.onHitEntity(itemStack, target, attacker);
			}
		}

		return true;
	}

	/**
	 * Adds attribute modifiers provided by this equipment.
	 * @param slot The slot that this equipment is in.
	 * @return The Attribute Modifier multimap with changes applied to it.
	 */
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack itemStack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, itemStack);
		if (slot == EquipmentSlotType.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.getDamageAmount(itemStack), AttributeModifier.Operation.ADDITION));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -this.getDamageCooldown(itemStack), AttributeModifier.Operation.ADDITION));
		}

		return multimap;
	}

	/**
	 * Returns how much damage this equipment will do.
	 * @param itemStack The equipment ItemStack.
	 * @return The amount of base damage.
	 */
	public double getDamageAmount(ItemStack itemStack) {
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
