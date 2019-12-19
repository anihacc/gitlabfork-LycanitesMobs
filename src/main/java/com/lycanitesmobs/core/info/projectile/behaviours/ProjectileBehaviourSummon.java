package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ProjectileBehaviourSummon extends ProjectileBehaviour {
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
	public void onProjectileImpact(BaseProjectileEntity projectile, World world, BlockPos pos) {
		if(projectile == null || projectile.getEntityWorld().isRemote) {
			return;
		}

		// Summon:
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

		int summonCount = this.summonCountMin;
		if(this.summonCountMax > this.summonCountMin) {
			summonCount = this.summonCountMin + projectile.getEntityWorld().rand.nextInt(this.summonCountMax - this.summonCountMin);
		}
		for(int i = 0; i < summonCount; i++) {
			if (projectile.getEntityWorld().rand.nextDouble() <= this.summonChance) {
				try {
					Entity entity = entityType.create(projectile.getEntityWorld());
					entity.setLocationAndAngles(projectile.getPosition().getX(), projectile.getPosition().getY(), projectile.getPosition().getZ(), projectile.rotationYaw, 0.0F);
					if (entity instanceof BaseCreatureEntity) {
						BaseCreatureEntity entityCreature = (BaseCreatureEntity) entity;
						entityCreature.setMinion(true);
						entityCreature.setTemporary(this.summonDuration * 20);
						entityCreature.setSizeScale(this.sizeScale);

						if (projectile.getThrower() instanceof PlayerEntity && entityCreature instanceof TameableCreatureEntity) {
							TameableCreatureEntity entityTameable = (TameableCreatureEntity) entityCreature;
							entityTameable.setPlayerOwner((PlayerEntity) projectile.getThrower());
							entityTameable.setSitting(false);
							entityTameable.setFollowing(true);
							entityTameable.setPassive(false);
							entityTameable.setAssist(true);
							entityTameable.setAggressive(true);
						}

						float randomAngle = 45F + (45F * projectile.getEntityWorld().rand.nextFloat());
						if (projectile.getEntityWorld().rand.nextBoolean()) {
							randomAngle = -randomAngle;
						}
						BlockPos spawnPos = entityCreature.getFacingPosition(projectile, -1, randomAngle);
						entity.setLocationAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), projectile.rotationYaw, 0.0F);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}