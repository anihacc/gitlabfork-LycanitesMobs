package com.lycanitesmobs.core.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class CreatureBuildTask {
	public BlockState blockState;
	public BlockPos pos;

	public CreatureBuildTask(BlockState blockState, BlockPos pos) {
		this.blockState = blockState;
		this.pos = pos;
	}
}
