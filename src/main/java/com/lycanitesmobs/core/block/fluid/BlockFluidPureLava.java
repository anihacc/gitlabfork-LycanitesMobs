package com.lycanitesmobs.core.block.fluid;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.BlockFluidBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockFluidPureLava extends BlockFluidBase {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockFluidPureLava(FlowingFluid fluid, Block.Properties properties) {
		super(fluid, properties, LycanitesMobs.modInfo, "purelava");

        //this.setLightOpacity(1);
        //this.setLightLevel(1.0F);
	}
    
    
	// ==================================================
	//                       Fluid
	// ==================================================
	/*@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		
		// Renewable Fluid:
		if(blockState.getBlock() == this) {
			if(blockState.getBlock().getMetaFromState(blockState) != 0) {
				byte otherSourceBlocks = 0;
				ArrayList<BlockPos> adjBlockPositions = new ArrayList<BlockPos>();
				adjBlockPositions.add(pos.add(-1, 0, 0));
				adjBlockPositions.add(pos.add(1, 0, 0));
				adjBlockPositions.add(pos.add(0, 1, 0));
				adjBlockPositions.add(pos.add(0, 0, -1));
				adjBlockPositions.add(pos.add(0, 0, 1));
				for(BlockPos adjBlockPos : adjBlockPositions) {
                    BlockState adjBlockState = world.getBlockState(adjBlockPos);
                    Block adjBlock = adjBlockState.getBlock();
					int adjMetadata = adjBlock.getMetaFromState(adjBlockState);
					if(adjBlock == this && adjMetadata == 0)
						otherSourceBlocks++;
					if(otherSourceBlocks > 1)
						break;
				}
				
				if(otherSourceBlocks > 1) {
					if(world instanceof World) {
						((World)world).setBlockState(pos, this.getDefaultState());
					}
				}
			}
			return false;
		}

        // Water Cobblestone:
        if(blockState == Blocks.WATER) {
            if(world instanceof World) {
                ((World)world).setBlockState(pos, Blocks.STONE.getDefaultState());
            }
            return false;
        }
		
		if(blockState.getMaterial().isLiquid()) return false;
		return super.canDisplace(world, pos);
	}
	
	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		if(world.getBlockState(pos).getMaterial().isLiquid()) return this.canDisplace(world, pos);
		return super.displaceIfPossible(world, pos);
	}*/
    
    
	// ==================================================
	//                      Collision
	// ==================================================
	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos pos, Entity entity) {
		if(entity instanceof ItemEntity)
			entity.attackEntityFrom(DamageSource.LAVA, 10F);
		super.onEntityCollision(blockState, world, pos, entity);
	}
    
    
	// ==================================================
	//                      Particles
	// ==================================================
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
		float f;
		float f1;
		float f2;

		if (random.nextInt(100) == 0) {
			f = (float)pos.getX() + random.nextFloat();
			f1 = (float)pos.getY() + random.nextFloat() * 0.5F;
			f2 = (float)pos.getZ() + random.nextFloat();
			world.addParticle(ParticleTypes.LAVA, (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
		}
		super.animateTick(state, world, pos, random);
    }
}
