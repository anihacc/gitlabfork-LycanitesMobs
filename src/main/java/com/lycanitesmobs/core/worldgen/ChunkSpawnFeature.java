package com.lycanitesmobs.core.worldgen;

import com.lycanitesmobs.core.spawner.SpawnerEventListener;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;
import java.util.function.Function;

public class ChunkSpawnFeature extends Feature<NoFeatureConfig> {
	public ChunkSpawnFeature(Codec<NoFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	protected void setBlockState(IWorldWriter worldWriter, BlockPos pos, BlockState state) {
		if(worldWriter instanceof World) {
			SpawnerEventListener.getInstance().onChunkGenerate(((World) worldWriter).getDimensionKey().getLocation().toString(), new ChunkPos(pos)); // TODO Probably doesn't work, need a proper chunk gen event again for custom mob spawning!
		}
	}

	@Override
	public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		return false;
	}
}
