package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkSpawnTrigger extends SpawnTrigger {
	/** Has a random chance of triggering after so many player ticks. **/


	/** Constructor **/
	public ChunkSpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);
	}


	/** Called every time a new chunk is generated. **/
	public boolean onChunkPopulate(World world, ChunkPos chunkPos) {
		if(true) return false; // TODO Disabled until stable.

		// Chance:
		if(this.chance < 1 && world.rand.nextDouble() > this.chance) {
			return false;
		}

		return this.trigger(world, null, new BlockPos(chunkPos.getXStart() + 8, world.getSeaLevel(), chunkPos.getZStart() + 8), 0, 0);
	}
}
