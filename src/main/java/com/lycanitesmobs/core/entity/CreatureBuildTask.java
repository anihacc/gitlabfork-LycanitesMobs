package com.lycanitesmobs.core.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

public class CreatureBuildTask {
	public BlockState blockState;
	public BlockPos pos;
	public int phase;

	public CreatureBuildTask(BlockState blockState, BlockPos pos, int phase) {
		this.blockState = blockState;
		this.pos = pos;
		this.phase = phase;
	}
}
