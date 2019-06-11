package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.util.math.BlockPos;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class CropBlockSpawnTrigger extends BlockSpawnTrigger {

	/** Constructor **/
	public CropBlockSpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);
	}


	@Override
	public boolean isTriggerBlock(BlockState blockState, World world, BlockPos blockPos, int fortune) {
		Block block = blockState.getBlock();
		return block instanceof IPlantable || block instanceof BlockVine;
	}

	@Override
	public int getBlockLevel(BlockState blockState, World world, BlockPos blockPos) {
		return 0;
	}
}
