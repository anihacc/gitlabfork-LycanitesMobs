package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.dungeon.definition.DungeonTheme;
import com.lycanitesmobs.core.dungeon.definition.ThemeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CreatureStructure {
	protected BaseCreatureEntity owner;
	protected DungeonTheme dungeonTheme;
	protected List<CreatureBuildTask> buildTasks = new ArrayList<>();
	protected BlockPos origin;

	protected BlockPos startPos;

	public CreatureStructure(BaseCreatureEntity creatureEntity, DungeonTheme dungeonTheme) {
		this.owner = creatureEntity;
		this.dungeonTheme = dungeonTheme;
		this.origin = creatureEntity.blockPosition();
	}

	/**
	 * Gets the origin position of this structure.
	 * @return blockPos The position that acts as the structure origin.
	 */
	public BlockPos getOrigin() {
		return this.origin;
	}

	/**
	 * Sets the origin position of this structure.
	 * @param blockPos The position to act as the structure origin.
	 */
	public void setOrigin(BlockPos blockPos) {
		this.origin = blockPos;
		this.startPos = blockPos.offset(0, 8, 0);
	}

	/**
	 * Updates structure maintenance, checking for any parts that need to be build, etc.
	 */
	public void refreshBuildTasks() {
		this.buildTasks.clear();

		// Check if started:
		if (!this.isStarted()) {
			this.buildTasks.add(new CreatureBuildTask(
					this.dungeonTheme.getCeiling(null, '1', this.owner.getRandom()),
					this.startPos
				)
			);
			return;
		}

		// A simple box for now. TODO Try to use Dungeon Sector?
		int radius = 8;
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {

					// Skip Corners:
					if (Math.abs(x) == radius && Math.abs(z) == radius) {
						continue;
					}

					BlockPos blockPos = this.origin.offset(x, y, z);

					// Check Placement:
					if (!this.shouldBuildAt(blockPos)) {
						continue;
					}

					BlockState blockState = Blocks.AIR.defaultBlockState();

					// Entrance Spaces:
					if (Math.abs(y) <= 1 && (Math.abs(x) <= 1 || Math.abs(z) <= 1)) {
						blockState = Blocks.AIR.defaultBlockState();
					}

					// Inside Air:
					else if (Math.abs(x) < radius && Math.abs(y) < radius && Math.abs(z) < radius) {
						if (this.owner.getCommandSenderWorld().getBlockState(blockPos).getBlock() != Blocks.AIR) {
							blockState = Blocks.AIR.defaultBlockState();
						}
					}

					// Ceiling:
					else if (y == radius) {
						blockState = this.dungeonTheme.getCeiling(null, '1', this.owner.getRandom());
					}

					// Floor:
					else if (y == -radius) {
						blockState = this.dungeonTheme.getFloor(null, '1', this.owner.getRandom());
					}

					// Wall:
					else {
						blockState = this.dungeonTheme.getWall(null, '1', this.owner.getRandom());
					}

					// Create Build Task If Different:
					if (this.owner.getCommandSenderWorld().getBlockState(blockPos).getBlock() != blockState.getBlock()) {
						this.buildTasks.add(new CreatureBuildTask(blockState, blockPos));
					}
				}
			}
		}

		// Pit Blocks:
		if (this.getBuildTaskSize() <= 10) {
			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						for (int pitLayer = 1; pitLayer <= (radius / 2); pitLayer++) {
							if (y == radius - pitLayer && Math.abs(x) < (radius - pitLayer) && Math.abs(z) < (radius - pitLayer)) {
								BlockPos blockPos = this.origin.offset(x, y, z);
								BlockState blockState = this.dungeonTheme.getPit('1', this.owner.getRandom());
								this.buildTasks.add(new CreatureBuildTask(blockState, blockPos));
							}
						}
						for (int pitLayer = 1; pitLayer <= (radius / 2); pitLayer++) {
							if (y == -radius + pitLayer && Math.abs(x) < (radius - pitLayer) && Math.abs(z) < (radius - pitLayer)) {
								BlockPos blockPos = this.origin.offset(x, y, z);
								BlockState blockState = this.dungeonTheme.getPit('1', this.owner.getRandom());
								this.buildTasks.add(new CreatureBuildTask(blockState, blockPos));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Checks if the provided position is valid for building, checks for existing structure blocks.
	 * @param pos The position to check.
	 * @return True if a Build Task should be created for the position, false if not.
	 */
	protected boolean shouldBuildAt(BlockPos pos) {
		BlockState targetState = this.owner.getCommandSenderWorld().getBlockState(pos);
		Block targetBlock = targetState.getBlock();
		if (targetBlock == Blocks.AIR) {
			return true;
		}
		if (targetState.getMaterial() == Material.WATER || targetState.getMaterial() == Material.LAVA) {
			return true;
		}
		if (targetState.getMaterial() == Material.PLANT
				|| targetState.getMaterial() == Material.DIRT
				|| targetState.getMaterial() == Material.GRASS
				|| targetState.getMaterial() == Material.LEAVES
				|| targetState.getMaterial() == Material.VEGETABLE
				|| targetState.getMaterial() == Material.REPLACEABLE_PLANT
				|| targetState.getMaterial() == Material.WOOD
		) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the block at the provided blockpos is a block belonging to this structure.
	 * @param blockPos The position to check.
	 * @return True if the block is part of this structure's theme.
	 */
	protected boolean isStructureBlock(BlockPos blockPos) {
		BlockState targetState = this.owner.getCommandSenderWorld().getBlockState(blockPos);
		Block targetBlock = targetState.getBlock();
		if (targetBlock == Blocks.AIR) {
			return false;
		}
		for (ThemeBlock themeBlock : this.dungeonTheme.ceilingBlocks) {
			if (targetBlock == themeBlock.getBlock()) {
				return true;
			}
		}
		for (ThemeBlock themeBlock : this.dungeonTheme.floorBlocks) {
			if (targetBlock == themeBlock.getBlock()) {
				return true;
			}
		}
		for (ThemeBlock themeBlock : this.dungeonTheme.wallBlocks) {
			if (targetBlock == themeBlock.getBlock()) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	public CreatureBuildTask takeBuildTask(LivingEntity builder) {
		if (this.buildTasks.isEmpty()) {
			return null;
		}
		if (buildTasks.size() == 1) {
			return buildTasks.get(0);
		}
		CreatureBuildTask buildTask = this.buildTasks.get(builder.getRandom().nextInt(buildTasks.size()));
		this.buildTasks.remove(buildTask);
		return buildTask;
	}

	/**
	 * Performs a cheap check for initial Hive blocks.
	 * @return True if in a hive, false if not.
	 */
	public boolean isStarted() {
		if (this.isStructureBlock(this.startPos)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns how many Build Tasks that are left to finish building the structure..
	 * @return A percentage from 0.0 - 1.0 of how complete th
	 */
	public int getBuildTaskSize() {
		return this.buildTasks.size();
	}
}
