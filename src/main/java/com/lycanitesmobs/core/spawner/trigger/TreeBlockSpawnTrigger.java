package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.LogBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TreeBlockSpawnTrigger extends BlockSpawnTrigger {

	/** Constructor **/
	public TreeBlockSpawnTrigger(Spawner spawner) {
		super(spawner);
	}

	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);
	}


	@Override
	public boolean isTriggerBlock(BlockState blockState, World world, BlockPos blockPos, int fortune, @Nullable LivingEntity entity) {
		return this.isTreeLogBlock(blockState.getBlock(), world, blockPos) || this.isTreeLeavesBlock(blockState.getBlock(), world, blockPos);
	}

	public boolean isTreeLogBlock(Block block, World world, BlockPos pos) {
		if(this.isLog(world.getBlockState(pos))) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			for(int searchX = x - 1; searchX <= x + 1; searchX++) {
				for(int searchZ = z - 1; searchZ <= z + 1; searchZ++) {
					for(int searchY = y; searchY <= Math.min(world.getActualHeight(), y + 32); searchY++) {
						if(this.isLeaves(world.getBlockState(new BlockPos(searchX, searchY, searchZ))))
							return true;
						if(!world.isAirBlock(new BlockPos(x, searchY, z)))
							break;
					}
				}
			}
		}
		String blockName = block.getRegistryName().getPath();
		if(blockName.contains("tree") && blockName.contains("branch")) {
			return true;
		}
		return false;
	}

	public boolean isTreeLeavesBlock(Block block, World world, BlockPos pos) {
		if(this.isLeaves(world.getBlockState(pos))) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			for(int searchX = x - 1; searchX <= x + 1; searchX++) {
				for(int searchZ = z - 1; searchZ <= z + 1; searchZ++) {
					for(int searchY = y; searchY >= Math.max(0, y - 32); searchY--) {
						if(this.isLog(world.getBlockState(new BlockPos(searchX, searchY, searchZ))))
							return true;
						if(!world.isAirBlock(new BlockPos(x, searchY, z)))
							break;
					}
				}
			}
		}
		String blockName = block.getRegistryName().getPath();
		if(blockName.contains("tree") && blockName.contains("leaves")) {
			return true;
		}
		return false;
	}

	public boolean isLog(BlockState blockState) {
		Block block = blockState.getBlock();
		if(block instanceof LogBlock || block.isIn(BlockTags.LOGS)) {
			return true;
		}
		return false;
	}

	public boolean isLeaves(BlockState blockState) {
		Block block = blockState.getBlock();
		if(block instanceof LeavesBlock || block.isIn(BlockTags.LEAVES)) {
			return true;
		}
		return false;
	}
}
