package com.lycanitesmobs.core.worldgen;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Placement;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AlwaysPlacement extends Placement<FrequencyConfig> {

	public AlwaysPlacement(Function<Dynamic<?>, ? extends FrequencyConfig> configFactory) {
		super(configFactory);
	}

	@Override
	@Nonnull
	public Stream<BlockPos> func_212848_a_(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGenerator, Random random, FrequencyConfig frequencyConfig, BlockPos blockPos) {
		return IntStream.range(0, frequencyConfig.count).mapToObj((p_215050_3_) -> blockPos);
	}
}
