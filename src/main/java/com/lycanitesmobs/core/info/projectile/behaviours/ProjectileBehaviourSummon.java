package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ProjectileBehaviourSummon extends ProjectileBehaviour {
	/** The id of the mob to summon. **/
	public String summonMobId;

	/** If true, the player selected minion is summoned instead of a direct entity from mobId. **/
	public boolean summonMinion = false;

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
		if(json.has("summonMobId"))
			this.summonMobId = json.get("summonMobId").getAsString();

		if(json.has("summonMinion"))
			this.summonMinion = json.get("summonMinion").getAsBoolean();

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
		EntityType entityType = null;

		// Summon Minion:
		SummonSet summonSet = null;
		if(this.summonMinion) {
			if(!(projectile.getThrower() instanceof PlayerEntity)) {
				return;
			}
			PlayerEntity player = (PlayerEntity)projectile.getThrower();
			ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
			if(extendedPlayer == null) {
				return;
			}
			summonSet = extendedPlayer.getSelectedSummonSet();
			if(summonSet == null || summonSet.getCreatureType() == null) {
				ClientManager.getInstance().displayGuiScreen("beastiary", player);
				return;
			}
			entityType = summonSet.getCreatureType();
		}

		// Summon From ID:
		if(entityType == null && this.summonMobId != null) {
			CreatureInfo creatureInfo = CreatureManager.getInstance().getCreatureFromId(this.summonMobId);
			if (creatureInfo != null) {
				entityType = creatureInfo.getEntityType();
			}
			else {
				Object entityTypeObj = GameRegistry.findRegistry(EntityType.class).getValue(new Identifier(this.summonMobId));
				if (entityTypeObj instanceof EntityType) {
					entityType = (EntityType) entityTypeObj;
				}
			}
		}
		if (entityType == null) {
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
						entityCreature.setTemporary(this.summonDuration);
						entityCreature.setSizeScale(this.sizeScale);

						if (projectile.getThrower() instanceof PlayerEntity && entityCreature instanceof TameableCreatureEntity) {
							TameableCreatureEntity entityTameable = (TameableCreatureEntity) entityCreature;
							entityTameable.setPlayerOwner((PlayerEntity) projectile.getThrower());
							entityTameable.setSitting(false);
							entityTameable.setFollowing(true);
							entityTameable.setPassive(false);
							entityTameable.setAssist(true);
							entityTameable.setAggressive(true);
							if(summonSet != null) {
								summonSet.applyBehaviour(entityTameable);
								entityTameable.setSubspecies(summonSet.subspecies);
								entityTameable.applyVariant(summonSet.variant);
							}
						}

						float randomAngle = 45F + (45F * projectile.getEntityWorld().rand.nextFloat());
						if (projectile.getEntityWorld().rand.nextBoolean()) {
							randomAngle = -randomAngle;
						}
						BlockPos spawnPos = entityCreature.getFacingPosition(projectile, -1, randomAngle);
						entity.setLocationAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), projectile.rotationYaw, 0.0F);
						entity.getEntityWorld().addEntity(entity);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
