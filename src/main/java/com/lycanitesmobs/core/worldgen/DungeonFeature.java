package com.lycanitesmobs.core.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class DungeonFeature extends Feature<NoFeatureConfig> {
	public DungeonFeature(Codec<NoFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		return false;
	}

//	@Override
//	public boolean setBlockState(IWorld worldWriter, ChunkGenerator<? extends GenerationSettings> generator, Random random, BlockPos pos, NoFeatureConfig config) {
//		boolean enabled = ConfigDungeons.INSTANCE.dungeonsEnabled.get();
//		int dungeonDistance = ConfigDungeons.INSTANCE.dungeonDistance.get();
//
//		World world = worldWriter.getWorld();
//		ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(world);
//		if(!enabled || extendedWorld == null) {
//			return false;
//		}
//		try {
//			int dungeonSizeMax = dungeonDistance;
//			ChunkPos chunkPos = new ChunkPos(pos);
//			List<DungeonInstance> nearbyDungeons = extendedWorld.getNearbyDungeonInstances(chunkPos, dungeonSizeMax * 2);
//
//			// Create New Instances:
//			if (nearbyDungeons.isEmpty()) {
//				for (int x = -1; x <= 1; x++) {
//					for (int z = -1; z <= 1; z++) {
//						if (x == 0 && z == 0) {
//							continue;
//						}
//						if (x != 0 && z != 0) {
//							continue;
//						}
//						LycanitesMobs.logDebug("Dungeon", "Creating A New Dungeon At Chunk: X" + (chunkPos.x + (dungeonSizeMax * x)) + " Z" + (chunkPos.z + (dungeonSizeMax * z)));
//						DungeonInstance dungeonInstance = new DungeonInstance();
//						int yPos = worldWriter.getSeaLevel();
//						if(yPos < 64) {
//							yPos = 64;
//						}
//						BlockPos dungeonPos = new ChunkPos(chunkPos.x + (dungeonSizeMax * x), chunkPos.z + (dungeonSizeMax * z)).getBlock(7, yPos, 7);
//						dungeonInstance.setOrigin(dungeonPos);
//						if(dungeonInstance.init(world)) {
//							extendedWorld.addDungeonInstance(dungeonInstance, new UUID(random.nextLong(), random.nextLong()));
//							LycanitesMobs.logDebug("", "Dungeon (Feature) Created: " + dungeonInstance.toString());
//						}
//					}
//				}
//			}
//
//			// Build Dungeons:
//			nearbyDungeons = extendedWorld.getNearbyDungeonInstances(chunkPos, 0);
//			for(DungeonInstance dungeonInstance : nearbyDungeons) {
//				//LycanitesMobs.logDebug("", "Building At: " + pos);
//				dungeonInstance.buildChunk(worldWriter, world, chunkPos, random);
//			}
//		}
//		catch(Exception e) {
//			LycanitesMobs.logWarning("", "An exception occurred when trying to generate a dungeon.");
//			e.printStackTrace();
//		}
//		return true;
//	}
}
