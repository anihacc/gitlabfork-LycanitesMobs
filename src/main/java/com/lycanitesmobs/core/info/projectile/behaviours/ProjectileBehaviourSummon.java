package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.beastiary.SummoningBeastiaryScreen;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.network.MessageGUIRequest;
import com.lycanitesmobs.core.network.PacketHandler;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
		Class entityClass = null;

		// Summon Minion:
		SummonSet summonSet = null;
		if(this.summonMinion) {
			if(!(projectile.getThrower() instanceof EntityPlayer)) {
				return;
			}
			EntityPlayer player = (EntityPlayer)projectile.getThrower();
			ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
			if(extendedPlayer == null) {
				return;
			}
			summonSet = extendedPlayer.getSelectedSummonSet();
			if(summonSet == null || summonSet.getCreatureClass() == null) {
				if(player instanceof EntityPlayerMP) {
					MessageGUIRequest messageGUIRequest = new MessageGUIRequest(GuiHandler.GuiType.BEASTIARY.id);
					LycanitesMobs.packetHandler.sendToPlayer(messageGUIRequest, (EntityPlayerMP) player);
				}
				return;
			}
			entityClass = summonSet.getCreatureClass();
		}

		// Summon From ID:
		if(entityClass == null && this.summonMobId != null) {
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
		}
		if (entityClass == null) {
			return;
		}

		int summonCount = this.summonCountMin;
		if(this.summonCountMax > this.summonCountMin) {
			summonCount = this.summonCountMin + projectile.getEntityWorld().rand.nextInt(this.summonCountMax - this.summonCountMin);
		}

		for(int i = 0; i < summonCount; i++) {
			if (projectile.getEntityWorld().rand.nextDouble() <= this.summonChance) {
				try {
					EntityLiving entity = (EntityLiving) entityClass.getConstructor(World.class).newInstance(new Object[]{projectile.getEntityWorld()});
					if (entity instanceof BaseCreatureEntity) {
						BaseCreatureEntity entityCreature = (BaseCreatureEntity) entity;
						entityCreature.setMinion(true);
						entityCreature.setTemporary(this.summonDuration);
						entityCreature.setSizeScale(this.sizeScale);

						if (projectile.getThrower() instanceof EntityPlayer && entityCreature instanceof TameableCreatureEntity) {
							TameableCreatureEntity entityTameable = (TameableCreatureEntity) entityCreature;
							entityTameable.setPlayerOwner((EntityPlayer) projectile.getThrower());
							entityTameable.setSitting(false);
							entityTameable.setFollowing(true);
							entityTameable.setPassive(false);
							entityTameable.setAssist(true);
							entityTameable.setAggressive(true);
							if(summonSet != null) {
								summonSet.applyBehaviour(entityTameable);
								entityTameable.applySubspecies(summonSet.subspecies);
							}
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
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
