package com.lycanitesmobs.core.worldgen;

import com.lycanitesmobs.core.spawner.SpawnerEventListener;
import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class ChunkSpawnFeature extends Feature<NoFeatureConfig> {
	public ChunkSpawnFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	public boolean func_212245_a(IWorld worldWriter, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		SpawnerEventListener.getInstance().onChunkGenerate(worldWriter.getWorld(), new ChunkPos(pos));
		return false;
	}

	@Override
	public List<Biome.SpawnListEntry> getSpawnList() {
		return Collections.emptyList();
	}

	@Override
	public List<Biome.SpawnListEntry> getCreatureSpawnList() {
		return Collections.emptyList();
	}
}
