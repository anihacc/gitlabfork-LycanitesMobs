package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.dungeon.definition.DungeonTheme;
import com.lycanitesmobs.core.dungeon.definition.ThemeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.IFluidBlock;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatureStructure {
	protected BaseCreatureEntity owner;
	protected DungeonTheme dungeonTheme;
	protected Map<Integer, List<CreatureBuildTask>> buildTasks = new HashMap<>();
	protected BlockPos origin;

	protected BlockPos startPos;

	public CreatureStructure(BaseCreatureEntity creatureEntity, DungeonTheme dungeonTheme) {
		this.owner = creatureEntity;
		this.dungeonTheme = dungeonTheme;
		this.origin = creatureEntity.getPosition();
		if (this.dungeonTheme == null) {
			throw new RuntimeException("Unable to find the vespid_hive dungeon theme!");
		}
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
		this.startPos = blockPos.add(0, 8, 0);
	}

	/**
	 * Updates structure maintenance, checking for any parts that need to be build, etc.
	 */
	public void refreshBuildTasks() {
		this.buildTasks.clear();
		int radius = 8;

		// Check if started:
		if (!this.isPhaseComplete(0)) {
			this.createBuildTask(
				this.dungeonTheme.getCeiling(null, '1', this.owner.getRNG()),
				this.startPos,
				0
			);
			return;
		}

		// A simple box for now. TODO Try to use Dungeon Sector?
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {

					// Skip Corners:
					if (Math.abs(x) == radius && Math.abs(z) == radius) {
						continue;
					}

					BlockPos blockPos = this.origin.add(x, y, z);

					// Check Placement:
					if (!this.shouldBuildAt(blockPos)) {
						continue;
					}

					IBlockState blockState = null;

					// Entrance Spaces:
					if (Math.abs(y) <= 1 && (Math.abs(x) <= 1 || Math.abs(z) <= 1)) {
						blockState = Blocks.AIR.getDefaultState();
					}

					// Inside Air:
					else if (Math.abs(x) < radius && Math.abs(y) < radius && Math.abs(z) < radius) {
						if (Math.abs(x) < (radius / 4) + 1 && y < -(radius / 2) + 1 && Math.abs(z) < (radius / 4) + 1) {
							continue;
						}
						IBlockState targetState = this.owner.getEntityWorld().getBlockState(blockPos);
						if (targetState.getBlock() != Blocks.AIR && !(targetState.getBlock() instanceof IFluidBlock)) {
							blockState = Blocks.AIR.getDefaultState();
						}
					}

					// Ceiling:
					else if (y == radius) {
						blockState = this.dungeonTheme.getCeiling(null, '1', this.owner.getRNG());
					}

					// Floor:
					else if (y == -radius) {
						blockState = this.dungeonTheme.getFloor(null, '1', this.owner.getRNG());
					}

					// Wall:
					else {
						blockState = this.dungeonTheme.getWall(null, '1', this.owner.getRNG());
					}

					// Create Build Task If Different:
					if (blockState != null && this.owner.getEntityWorld().getBlockState(blockPos).getBlock() != blockState.getBlock()) {
						this.createBuildTask(blockState, blockPos, 1);
					}
				}
			}
		}

		// First Pit Blocks:
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					if (Math.abs(x) < (radius / 4) + 1 && Math.abs(z) < (radius / 4) + 1) {
						continue;
					}

					BlockPos blockPos = this.origin.add(x, y, z);

					// Check Placement:
					if (!this.shouldBuildAt(blockPos)) {
						continue;
					}

					for (int pitLayer = 1; pitLayer <= (radius / 2); pitLayer++) {
						if (y == radius - pitLayer && Math.abs(x) < (radius - pitLayer) && Math.abs(z) < (radius - pitLayer)) {
							IBlockState blockState = this.dungeonTheme.getPit('1', this.owner.getRNG());
							if (this.owner.getEntityWorld().getBlockState(blockPos).getBlock() != blockState.getBlock()) {
								this.createBuildTask(blockState, blockPos, 1);
							}
						}
					}
					for (int pitLayer = 1; pitLayer <= (radius / 2); pitLayer++) {
						if (y == -radius + pitLayer && Math.abs(x) < (radius - pitLayer) && Math.abs(z) < (radius - pitLayer)) {
							IBlockState blockState = this.dungeonTheme.getPit('1', this.owner.getRNG());
							if (this.owner.getEntityWorld().getBlockState(blockPos).getBlock() != blockState.getBlock()) {
								this.createBuildTask(blockState, blockPos, 1);
							}
						}
					}
				}
			}
		}

		// Second Pit Blocks:
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					if (Math.abs(x) < (radius / 4) + 1 && y <= -(radius / 2) && y > -radius && Math.abs(z) < (radius / 4) + 1) {
						BlockPos blockPos = this.origin.add(x, y, z);

						// Check Placement:
						if (!this.shouldBuildAt(blockPos)) {
							continue;
						}

						IBlockState blockState = this.dungeonTheme.getPit('2', this.owner.getRNG());
						if (this.owner.getEntityWorld().getBlockState(blockPos).getBlock() != blockState.getBlock()) {
							this.createBuildTask(blockState, blockPos, 2);
						}
					}
				}
			}
		}
	}

	/**
	 * Creates and adds a new CreatureBuildTask to this CreatureStructure.
	 * Note that build tasks are constantly emptied when the structure is rescanned.
	 * Existing tasks using the same position will also be removed.
	 * @param blockState The block state to place.
	 * @param blockPos The position to place at.
	 */
	public void createBuildTask(IBlockState blockState, BlockPos blockPos, int phase) {
		if (!this.buildTasks.containsKey(phase)) {
			this.buildTasks.put(phase, new ArrayList<>());
		}

		// Remove Existing Tasks Using Same Block Position:
		for (int checkPhase = 0; checkPhase < this.buildTasks.size(); checkPhase++) {
			if (!this.buildTasks.containsKey(checkPhase)) {
				continue;
			}
			List<CreatureBuildTask> removeTasks = new ArrayList<>();
			for (CreatureBuildTask creatureBuildTask : this.buildTasks.get(checkPhase)) {
				if (creatureBuildTask.pos.equals(blockPos)) {
					removeTasks.add(creatureBuildTask);
				}
			}
			for (CreatureBuildTask creatureBuildTask : removeTasks) {
				this.buildTasks.get(checkPhase).remove(creatureBuildTask);
			}
		}

		this.buildTasks.get(phase).add(new CreatureBuildTask(blockState, blockPos, phase));
	}

	/**
	 * Checks if the provided position is valid for building, checks for existing structure blocks.
	 * @param pos The position to check.
	 * @return True if a Build Task should be created for the position, false if not.
	 */
	protected boolean shouldBuildAt(BlockPos pos) {
		IBlockState targetState = this.owner.getEntityWorld().getBlockState(pos);
		Block targetBlock = targetState.getBlock();
		if (targetBlock == Blocks.AIR) {
			return true;
		}
		if (this.owner.getEntityWorld().getTileEntity(pos) != null) {
			return false;
		}
		if (targetState.getMaterial() == Material.WATER || targetState.getMaterial() == Material.LAVA) {
			return true;
		}
		if (targetState.getMaterial() == Material.PLANTS
				|| targetState.getMaterial() == Material.GROUND
				|| targetState.getMaterial() == Material.GRASS
				|| targetState.getMaterial() == Material.LEAVES
				|| targetState.getMaterial() == Material.WOOD
		) {
			return true;
		}
		return false;
	}

	@Nullable
	public CreatureBuildTask takeBuildTask(EntityLivingBase builder) {
		if (this.buildTasks.isEmpty()) {
			return null;
		}
		for (int phase = 0; phase < this.buildTasks.size(); phase++) {
			if (!this.buildTasks.containsKey(phase) || this.buildTasks.get(phase).isEmpty()) {
				continue;
			}
			CreatureBuildTask buildTask = null;
			if (buildTasks.get(phase).size() == 1) {
				buildTask = buildTasks.get(phase).get(0);
			}
			else {
				buildTask = this.buildTasks.get(phase).get(builder.getRNG().nextInt(buildTasks.get(phase).size()));
			}
			return buildTask;
		}
		return null;
	}

	/**
	 * Completes the provided build task, removing it from the build tasks list.
	 * @param buildTask The build task to complete.
	 */
	public void completeBuildTask(CreatureBuildTask buildTask) {
		for (int phase = 0; phase < this.buildTasks.size(); phase++) {
			if (!this.buildTasks.containsKey(phase) || this.buildTasks.get(phase).isEmpty()) {
				continue;
			}
			this.buildTasks.get(phase).remove(buildTask);
			this.owner.onBuildTaskComplete(buildTask);
		}
	}

	/**
	 * Performs a check to see if the provided structure build phase is complete.
	 * @param phase The phase to check.
	 * @return True if in a hive, false if not.
	 */
	public boolean isPhaseComplete(int phase) {
		return this.getBuildTaskSize(phase) == 0;
	}

	/**
	 * Returns how many Build Tasks that are left to finish building the structure.
	 * @param phase The build task phase.
	 * @return How many tasks are left.
	 */
	public int getBuildTaskSize(int phase) {
		if (!this.buildTasks.containsKey(phase)) {
			return 0;
		}
		return this.buildTasks.get(phase).size();
	}

	/**
	 * Returns how many Build Tasks that are left to finish building the structure for the final phase.
	 * @return How many tasks are left.
	 */
	public int getFinalPhaseBuildTaskSize() {
		return this.getBuildTaskSize(this.buildTasks.size() -1);
	}

	/**
	 * Checks if the block at the provided blockpos is a block belonging to this structure.
	 * @param blockPos The position to check.
	 * @return True if the block is part of this structure's theme.
	 */
	protected boolean isStructureBlock(BlockPos blockPos) {
		IBlockState targetState = this.owner.getEntityWorld().getBlockState(blockPos);
		Block targetBlock = targetState.getBlock();
		if (targetBlock == Blocks.AIR) {
			return false;
		}
		for (ThemeBlock themeBlock : this.dungeonTheme.ceilingBlocks) {
			if (targetBlock == themeBlock.getBlockState().getBlock()) {
				return true;
			}
		}
		for (ThemeBlock themeBlock : this.dungeonTheme.floorBlocks) {
			if (targetBlock == themeBlock.getBlockState().getBlock()) {
				return true;
			}
		}
		for (ThemeBlock themeBlock : this.dungeonTheme.wallBlocks) {
			if (targetBlock == themeBlock.getBlockState().getBlock()) {
				return true;
			}
		}
		return false;
	}
}
