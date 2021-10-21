package com.lycanitesmobs.core.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.stream.Stream;

public class AlwaysPlacement extends FeatureDecorator<NoneDecoratorConfiguration> {

	public AlwaysPlacement(Codec<NoneDecoratorConfiguration> codec) {
		super(codec);
	}

	@Override
	public Stream<BlockPos> getPositions(DecorationContext helper, Random rand, NoneDecoratorConfiguration config, BlockPos blockPos) {
		return Stream.of(new BlockPos(blockPos));
	}
}
