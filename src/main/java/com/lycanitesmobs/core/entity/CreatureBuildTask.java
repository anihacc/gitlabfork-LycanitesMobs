package com.lycanitesmobs.core.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class CreatureBuildTask {
	public IBlockState blockState;
	public BlockPos pos;

	public CreatureBuildTask(IBlockState blockState, BlockPos pos) {
		this.blockState = blockState;
		this.pos = pos;
	}
}
