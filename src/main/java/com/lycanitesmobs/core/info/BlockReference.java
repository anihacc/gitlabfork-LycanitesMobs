package com.lycanitesmobs.core.info;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public record BlockReference(Level level, BlockPos pos) {

	public Level getLevel() {
		return this.level;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public BlockState getState() {
		return this.getLevel().getBlockState(this.getPos());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BlockReference that)) return false;
		return level.equals(that.level) && pos.equals(that.pos);
	}

	@Override
	public int hashCode() {
		return Objects.hash(level, pos);
	}
}
