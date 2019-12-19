package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
		int summonCount = this.summonCountMin;
		if(this.summonCountMax > this.summonCountMin) {
			summonCount = this.summonCountMin + projectile.getEntityWorld().rand.nextInt(this.summonCountMax - this.summonCountMin);
		}
		for(int i = 0; i < summonCount; i++) {
			if (projectile.getEntityWorld().rand.nextDouble() <= this.summonChance) {
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
						EntityLiving entity = (EntityLiving) entityClass.getConstructor(World.class).newInstance(new Object[]{projectile.getEntityWorld()});
						if (entity instanceof BaseCreatureEntity) {
							BaseCreatureEntity entityCreature = (BaseCreatureEntity) entity;
							entityCreature.setMinion(true);
							entityCreature.setTemporary(this.summonDuration * 20);
							entityCreature.setSizeScale(this.sizeScale);

							if (projectile.getThrower() instanceof EntityPlayer && entityCreature instanceof TameableCreatureEntity) {
								TameableCreatureEntity entityTameable = (TameableCreatureEntity) entityCreature;
								entityTameable.setPlayerOwner((EntityPlayer) projectile.getThrower());
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
							projectile.getEntityWorld().spawnEntity(entity);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
