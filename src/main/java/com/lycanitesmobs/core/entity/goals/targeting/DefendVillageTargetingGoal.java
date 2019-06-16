package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Iterator;
import java.util.List;

public class DefendVillageTargetingGoal extends AttackTargetingGoal {
	private final EntityPredicate villageSearchPredicate = (new EntityPredicate()).func_221013_a(64.0D);

	public DefendVillageTargetingGoal(EntityCreatureBase setHost) {
		super(setHost);
	}

	/**
	 * Returns whether the Goal should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		if(this.host.getOwner() != null) {
			return false;
		}

		AxisAlignedBB villageSearchArea = this.host.getBoundingBox().grow(10.0D, 8.0D, 10.0D);
		List<LivingEntity> villagers = this.host.world.func_217374_a(VillagerEntity.class, this.villageSearchPredicate, this.host, villageSearchArea);
		List<PlayerEntity> players = this.host.world.func_217373_a(this.villageSearchPredicate, this.host, villageSearchArea);
		Iterator villagerIter = villagers.iterator();

		while(villagerIter.hasNext()) {
			LivingEntity livingEntity = (LivingEntity)villagerIter.next();
			VillagerEntity villagerEntity = (VillagerEntity)livingEntity;
			Iterator playerIter = players.iterator();

			while(playerIter.hasNext()) {
				PlayerEntity playerEntity = (PlayerEntity)playerIter.next();
				int reputation = villagerEntity.func_223107_f(playerEntity);
				if (reputation <= -100) {
					this.target = playerEntity;
				}
			}
		}

		return this.isEntityTargetable(this.target, false);
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		super.startExecuting();
	}

}