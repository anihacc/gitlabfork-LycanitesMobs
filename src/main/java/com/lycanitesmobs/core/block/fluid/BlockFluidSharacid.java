package com.lycanitesmobs.core.block.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;

public class BlockFluidSharacid extends BlockFluidAcid {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockFluidSharacid(Fluid fluid, String name) {
        super(fluid, name);
	}


	// ==================================================
	//                       Fluid
	// ==================================================
	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);

		// Renewable Fluid:
		if (blockState.getBlock() == this) {
			if (blockState.getBlock().getMetaFromState(blockState) != 0) {
				byte otherSourceBlocks = 0;
				ArrayList<BlockPos> adjBlockPositions = new ArrayList<BlockPos>();
				adjBlockPositions.add(pos.add(-1, 0, 0));
				adjBlockPositions.add(pos.add(1, 0, 0));
				adjBlockPositions.add(pos.add(0, 1, 0));
				adjBlockPositions.add(pos.add(0, 0, -1));
				adjBlockPositions.add(pos.add(0, 0, 1));
				for (BlockPos adjBlockPos : adjBlockPositions) {
					IBlockState adjBlockState = world.getBlockState(adjBlockPos);
					Block adjBlock = adjBlockState.getBlock();
					int adjMetadata = adjBlock.getMetaFromState(adjBlockState);
					if (adjBlock == this && adjMetadata == 0)
						otherSourceBlocks++;
					if (otherSourceBlocks > 1)
						break;
				}

				if (otherSourceBlocks > 1) {
					if (world instanceof World) {
						((World) world).setBlockState(pos, this.getDefaultState());
					}
				}
			}
			return false;
		}

		return super.canDisplace(world, pos);
	}
}
