package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SummonEquipmentFeature extends EquipmentFeature {
	/** The id of the mob to summon. **/
	public String summonMobId;

	/** The chance on summoning mobs. **/
	public double summonChance = 0.05;

	/** How long in ticks the summoned creature lasts for. **/
	public int summonDuration = 60;

	/** The minimum amount of mobs to summon. **/
	public int summonCountMin = 1;

	/** The maximum amount of mobs to summon. **/
	public int summonCountMax = 1;

	/** The size scale of summoned mobs. **/
	public double sizeScale = 1;


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);

		this.summonMobId = json.get("summonMobId").getAsString();

		if(json.has("summonChance"))
			this.summonChance = json.get("summonChance").getAsDouble();

		if(json.has("summonDuration"))
			this.summonDuration = json.get("summonDuration").getAsInt();

		if(json.has("summonCountMin"))
			this.summonCountMin = json.get("summonCountMin").getAsInt();

		if(json.has("summonCountMax"))
			this.summonCountMax = json.get("summonCountMax").getAsInt();

		if(json.has("sizeScale"))
			this.sizeScale = json.get("sizeScale").getAsDouble();
	}

	@Override
	public ITextComponent getDescription(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		ITextComponent description = new TranslationTextComponent("equipment.feature." + this.featureType)
				.appendText(" ")
				.appendSibling(new TranslationTextComponent("entity." + this.summonMobId + ".name"))
				.appendText("\n")
				.appendSibling(new TranslationTextComponent("equipment.feature.summon.chance"))
				.appendText(" " + Math.round(this.summonChance * 100) + "%");

		if(this.summonDuration > 0) {
			description.appendText("\n")
					.appendSibling(new TranslationTextComponent("equipment.feature.effect.duration"))
					.appendText(" " + ((float)this.summonDuration / 20));
		}

		if(this.summonCountMin != this.summonCountMax) {
			description.appendText("\n")
					.appendSibling(new TranslationTextComponent("equipment.feature.summon.count"))
					.appendText(" " + (this.summonCountMin + " - " + this.summonCountMax));
		}
		else {
			description.appendText("\n")
					.appendSibling(new TranslationTextComponent("equipment.feature.summon.count"))
					.appendText(" " + this.summonCountMax);
		}

		return description;
	}

	/**
	 * Called when an entity is hit by equipment with this feature.
	 * @param itemStack The ItemStack being hit with.
	 * @param target The target entity being hit.
	 * @param attacker The entity using this item to hit.
	 */
	public void onHitEntity(ItemStack itemStack, LivingEntity target, LivingEntity attacker) {
		if(target == null || attacker == null || attacker.getEntityWorld().isRemote) {
			return;
		}

		EntityType entityType = null;
		CreatureInfo creatureInfo = CreatureManager.getInstance().getCreatureFromId(this.summonMobId);
		if(creatureInfo != null) {
			entityType = creatureInfo.getEntityType();
		}
		else {
			Object entityTypeObj = GameRegistry.findRegistry(EntityType.class).getValue(new ResourceLocation(this.summonMobId));
			if(entityTypeObj instanceof EntityType) {
				entityType = (EntityType)entityTypeObj;
			}
		}
		if(entityType == null) {
			return;
		}

		// Summon:
		if(attacker.getRNG().nextDouble() <= this.summonChance) {
			try {
				Entity entity = entityType.create(attacker.getEntityWorld());
				entity.setLocationAndAngles(attacker.getPosition().getX(), attacker.getPosition().getY(), attacker.getPosition().getZ(), attacker.rotationYaw, 0.0F);
				if(entity instanceof BaseCreatureEntity) {
					BaseCreatureEntity entityCreature = (BaseCreatureEntity)entity;
					entityCreature.setMinion(true);
					entityCreature.setTemporary(this.summonDuration * 20);
					entityCreature.setSizeScale(this.sizeScale);

					if(attacker instanceof PlayerEntity && entityCreature instanceof TameableCreatureEntity) {
						TameableCreatureEntity entityTameable = (TameableCreatureEntity)entityCreature;
						entityTameable.setPlayerOwner((PlayerEntity)attacker);
						entityTameable.setSitting(false);
						entityTameable.setFollowing(true);
						entityTameable.setPassive(false);
						entityTameable.setAssist(true);
						entityTameable.setAggressive(true);
						entityTameable.setPVP(target instanceof PlayerEntity);
					}

					float randomAngle = 45F + (45F * attacker.getRNG().nextFloat());
					if(attacker.getRNG().nextBoolean()) {
						randomAngle = -randomAngle;
					}
					BlockPos spawnPos = entityCreature.getFacingPosition(attacker, -1, randomAngle);
					entity.setLocationAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), attacker.rotationYaw, 0.0F);
					entityCreature.setAttackTarget(target);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
