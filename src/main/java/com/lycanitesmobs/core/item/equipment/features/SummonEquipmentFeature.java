package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.spawner.MobSpawn;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.world.World;

public class SummonEquipmentFeature extends EquipmentFeature {
	/** The entity class to spawn. **/
	public Class entityClass;

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
		CreatureInfo creatureInfo = CreatureManager.getInstance().getCreatureFromId(this.summonMobId);
		if(creatureInfo != null) {
			this.entityClass = creatureInfo.entityClass;
		}
		else {
			Class entityClass = null;
			net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.getValue(new ResourceLocation(this.summonMobId));
			if(entry != null) {
				entityClass = entry.getEntityClass();
			}
			this.entityClass = entityClass;
		}

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
		String description = LanguageManager.translate("equipment.feature." + this.featureType) + " " + LanguageManager.translate("entity." + this.summonMobId + ".name");
		description += "\n" + LanguageManager.translate("equipment.feature.summon.chance") + " " + Math.round(this.summonChance * 100) + "%";
		if(this.summonDuration > 0) {
			description += "\n" + LanguageManager.translate("equipment.feature.effect.duration") + " " + ((float)this.summonDuration / 20);
		}
		if(this.summonCountMin != this.summonCountMax) {
			description += "\n" + LanguageManager.translate("equipment.feature.summon.count") + " " + this.summonCountMin + " - " + this.summonCountMax;
		}
		else {
			description += "\n" + LanguageManager.translate("equipment.feature.summon.count") + " " + this.summonCountMax;
		}
		return description;
	}

	/**
	 * Called when an entity is hit by equipment with this feature.
	 * @param itemStack The ItemStack being hit with.
	 * @param target The target entity being hit.
	 * @param attacker The entity using this item to hit.
	 */
	public void onHitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase attacker) {
		if(target == null || attacker == null || attacker.getEntityWorld().isRemote) {
			return;
		}

		// Summon:
		if(attacker.getRNG().nextDouble() <= this.summonChance) {
			try {
				EntityLiving entity = (EntityLiving)this.entityClass.getConstructor(World.class).newInstance(new Object[]{attacker.getEntityWorld()});
				if(entity instanceof EntityCreatureBase) {
					EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
					entityCreature.setMinion(true);
					entityCreature.setTemporary(this.summonDuration * 20);
					entityCreature.setSizeScale(this.sizeScale);

					if(attacker instanceof PlayerEntity && entityCreature instanceof EntityCreatureTameable) {
						EntityCreatureTameable entityTameable = (EntityCreatureTameable)entityCreature;
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
					/*if(!entity.getEntityWorld().isSideSolid(spawnPos, EnumFacing.UP)) {
						randomAngle = -randomAngle;
					}*/
					entity.setLocationAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), attacker.rotationYaw, 0.0F);
					attacker.getEntityWorld().spawnEntity(entity);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
