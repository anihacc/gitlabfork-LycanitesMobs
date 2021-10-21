package com.lycanitesmobs.core.worldgen;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.config.ConfigDungeons;
import com.lycanitesmobs.core.dungeon.instance.DungeonInstance;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DungeonFeature extends Feature<NoneFeatureConfiguration> {
	public DungeonFeature(Codec<NoneFeatureConfiguration> configFactory) {
		super(configFactory);
	}

	@Override
	public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, NoneFeatureConfiguration config) {
		boolean enabled = ConfigDungeons.INSTANCE.dungeonsEnabled.get();
		int dungeonDistance = ConfigDungeons.INSTANCE.dungeonDistance.get();

		Level world = reader.getLevel();
		ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(world);
		if(!enabled || extendedWorld == null) {
			return false;
		}
		try {
			int dungeonSizeMax = dungeonDistance;
			ChunkPos chunkPos = new ChunkPos(pos);
			List<DungeonInstance> nearbyDungeons = extendedWorld.getNearbyDungeonInstances(chunkPos, dungeonSizeMax * 2);

			// Create New Instances:
			if (nearbyDungeons.isEmpty()) {
				for (int x = -1; x <= 1; x++) {
					for (int z = -1; z <= 1; z++) {
						if (x == 0 && z == 0) {
							continue;
						}
						if (x != 0 && z != 0) {
							continue;
						}
						LycanitesMobs.logDebug("Dungeon", "Creating A New Dungeon At Chunk: X" + (chunkPos.x + (dungeonSizeMax * x)) + " Z" + (chunkPos.z + (dungeonSizeMax * z)));
						DungeonInstance dungeonInstance = new DungeonInstance();
						int yPos = reader.getSeaLevel();
						if(yPos < 64) {
							yPos = 64;
						}
						BlockPos dungeonPos = new ChunkPos(chunkPos.x + (dungeonSizeMax * x), chunkPos.z + (dungeonSizeMax * z)).getWorldPosition().offset(7, yPos, 7);
						dungeonInstance.setOrigin(dungeonPos);
						if(dungeonInstance.init(world)) {
							extendedWorld.addDungeonInstance(dungeonInstance, new UUID(rand.nextLong(), rand.nextLong()));
							LycanitesMobs.logDebug("Dungeon", "Feature Created: " + dungeonInstance.toString());
						}
					}
				}
			}

			// Build Dungeons:
			nearbyDungeons = extendedWorld.getNearbyDungeonInstances(chunkPos, 0);
			for(DungeonInstance dungeonInstance : nearbyDungeons) {
				dungeonInstance.buildChunk(reader, world, chunkPos, rand);
			}
		}
		catch(Exception e) {
//			LycanitesMobs.logWarning("", "An exception occurred when trying to generate a dungeon.");
//			e.printStackTrace();
		}
		return true;
	}
}
