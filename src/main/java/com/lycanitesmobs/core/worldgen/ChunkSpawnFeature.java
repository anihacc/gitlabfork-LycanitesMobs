package com.lycanitesmobs.core.worldgen;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.spawner.SpawnerEventListener;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.World;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Random;
import java.util.function.Function;

public class ChunkSpawnFeature extends Feature<NoneFeatureConfiguration> {
	public ChunkSpawnFeature(Codec<NoneFeatureConfiguration> configFactory) {
		super(configFactory);
	}

	@Override
	protected void setBlock(LevelWriter worldWriter, BlockPos pos, BlockState state) {
//		if(worldWriter instanceof World) {
//			SpawnerEventListener.getInstance().onChunkGenerate(((World) worldWriter).getDimensionKey().getLocation().toString(), new ChunkPos(pos));
//		}
	}

	@Override
	public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, NoneFeatureConfiguration config) {
		if (reader instanceof WorldGenRegion) {
			SpawnerEventListener.getInstance().onChunkGenerate(((WorldGenRegion)reader).getLevel().dimension().location().toString(), new ChunkPos(pos));
		}
		return true;
	}
}
