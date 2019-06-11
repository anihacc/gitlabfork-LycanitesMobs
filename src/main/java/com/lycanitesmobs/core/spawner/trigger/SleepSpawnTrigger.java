package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class SleepSpawnTrigger extends BlockSpawnTrigger {
	/** Constructor **/
	public SleepSpawnTrigger(Spawner spawner) {
		super(spawner);
	}



	/** Called every time a player attempts to use a bed. **/
	public boolean onSleep(World world, PlayerEntity player, BlockPos spawnPos, BlockState blockState) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);

		// Chance:
		if(this.chance < 1 && player.getRNG().nextDouble() > this.chance) {
			return false;
		}

		// Check Block:
		if(!this.isTriggerBlock(blockState, world, spawnPos, 0)) {
			return false;
		}

		// Trigger:
		boolean success = this.trigger(world, player, spawnPos, 0, 0);
		if(this.cooldown > -1 && playerExt != null) {
			this.playerUsedTicks.put(player, playerExt.timePlayed);
		}
		return success;
	}
}
