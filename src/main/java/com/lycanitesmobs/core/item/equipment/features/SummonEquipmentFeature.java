package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.world.World;

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
	public String getDescription(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		String description = LanguageManager.translate("equipment.feature." + this.featureType)
				+ " " + LanguageManager.translate("entity." + this.summonMobId + ".name");

		if(this.summonCountMin != this.summonCountMax) {
			description += " x" + this.summonCountMin + " - " + this.summonCountMax;
		}
		else {
			description += " x" + this.summonCountMax;
		}

		description += " " + Math.round(this.summonChance * 100) + "%";

		if(this.summonDuration > 0) {
			description += " " + ((float)this.summonDuration / 20) + "s";
		}

		return description;
	}

	@Override
	public String getSummary(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		return LanguageManager.translate("entity." + this.summonMobId + ".name");
	}

	/**
	 * Called when an entity is hit by equipment with this feature.
	 * @param itemStack The ItemStack being hit with.
	 * @param target The target entity being hit.
	 * @param attacker The entity using this item to hit.
	 */
	public boolean onHitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase attacker) {
		if(target == null || attacker == null || attacker.getEntityWorld().isRemote) {
			return false;
		}

		// Summon:
		int summonCount = this.summonCountMin;
		if(this.summonCountMax > this.summonCountMin) {
			summonCount = this.summonCountMin + attacker.getRNG().nextInt(this.summonCountMax - this.summonCountMin);
		}
		int summonedCreatures = 0;
		for(int i = 0; i < summonCount; i++) {
			if (attacker.getRNG().nextDouble() <= this.summonChance) {
				try {
					Class entityClass = null;
					CreatureInfo creatureInfo = CreatureManager.getInstance().getCreatureFromId(this.summonMobId);
					if (creatureInfo != null) {
						entityClass = creatureInfo.entityClass;
					}
					else {
						net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.getValue(new ResourceLocation(this.summonMobId));
						if (entry != null) {
							entityClass = entry.getEntityClass();
						}
					}
					if (entityClass != null) {
						EntityLiving entity = (EntityLiving) entityClass.getConstructor(World.class).newInstance(new Object[]{attacker.getEntityWorld()});
						if (entity instanceof BaseCreatureEntity) {
							BaseCreatureEntity entityCreature = (BaseCreatureEntity) entity;
							entityCreature.setMinion(true);
							entityCreature.setTemporary(this.summonDuration * 20);
							entityCreature.setSizeScale(this.sizeScale);

							if (attacker instanceof EntityPlayer && entityCreature instanceof TameableCreatureEntity) {
								TameableCreatureEntity entityTameable = (TameableCreatureEntity) entityCreature;
								entityTameable.setPlayerOwner((EntityPlayer) attacker);
								entityTameable.setSitting(false);
								entityTameable.setFollowing(true);
								entityTameable.setPassive(false);
								entityTameable.setAssist(true);
								entityTameable.setAggressive(true);
								entityTameable.setPVP(target instanceof EntityPlayer);
							}

							float randomAngle = 45F + (45F * attacker.getRNG().nextFloat());
							if (attacker.getRNG().nextBoolean()) {
								randomAngle = -randomAngle;
							}
							BlockPos spawnPos = entityCreature.getFacingPosition(attacker, -1, randomAngle);
							entity.setLocationAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), attacker.rotationYaw, 0.0F);
							attacker.getEntityWorld().spawnEntity(entity);
							entityCreature.setAttackTarget(target);
							summonedCreatures++;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return summonedCreatures > 0;
	}
}
