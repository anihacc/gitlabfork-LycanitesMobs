package com.lycanitesmobs.core.worldgen;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class AlwaysPlacement extends Placement<NoPlacementConfig> {

	public AlwaysPlacement(Function<Dynamic<?>, ? extends NoPlacementConfig> configFactory) {
		super(configFactory);
	}

	@Override
	@Nonnull
	public Stream<BlockPos> getPositions(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGenerator, Random random, NoPlacementConfig frequencyConfig, BlockPos blockPos) {
		return Stream.of(blockPos);
	}
}
