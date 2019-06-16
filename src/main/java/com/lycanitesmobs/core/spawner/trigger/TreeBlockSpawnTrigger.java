package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.LogBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
	public boolean isTriggerBlock(BlockState blockState, World world, BlockPos blockPos, int fortune) {
		return this.isTreeLogBlock(blockState.getBlock(), world, blockPos) || this.isTreeLeavesBlock(blockState.getBlock(), world, blockPos);
	}

	public boolean isTreeLogBlock(Block block, World world, BlockPos pos) {
		if(block instanceof LogBlock) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			for(int searchX = x - 1; searchX <= x + 1; searchX++) {
				for(int searchZ = z - 1; searchZ <= z + 1; searchZ++) {
					for(int searchY = y; searchY <= Math.min(world.getActualHeight(), y + 32); searchY++) {
						Block searchBlock = world.getBlockState(new BlockPos(searchX, searchY, searchZ)).getBlock();
						if(searchBlock != block) {
							if(searchBlock instanceof LeavesBlock)
								return true;
							if(!world.isAirBlock(new BlockPos(x, searchY, z)))
								break;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isTreeLeavesBlock(Block block, World world, BlockPos pos) {
		if(block instanceof LeavesBlock) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			for(int searchX = x - 1; searchX <= x + 1; searchX++) {
				for(int searchZ = z - 1; searchZ <= z + 1; searchZ++) {
					for(int searchY = y; searchY >= Math.max(0, y - 32); searchY--) {
						Block searchBlock = world.getBlockState(new BlockPos(searchX, searchY, searchZ)).getBlock();
						if(searchBlock != block) {
							if(searchBlock instanceof LogBlock) {
								return true;
							}
							if(!world.isAirBlock(new BlockPos(x, searchY, z)))
								break;
						}
					}
				}
			}
		}
		return false;
	}
}
