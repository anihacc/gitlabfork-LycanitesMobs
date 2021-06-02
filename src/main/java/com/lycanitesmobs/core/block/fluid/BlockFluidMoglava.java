package com.lycanitesmobs.core.block.fluid;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.BlockFluidBase;

import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockFluidMoglava extends BlockFluidBase {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockFluidMoglava(Fluid fluid) {
		super(fluid, Material.LAVA, LycanitesMobs.modInfo, "moglava");

        this.setLightOpacity(1);
        this.setLightLevel(1.0F);
	}


	// ==================================================
	//                      Info
	// ==================================================
	@Override
	public String getLocalizedName() {
		return LanguageManager.translate(this.getUnlocalizedName() + ".name");
	}

	@Override
	public void addInformation(ItemStack stack,  World world, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.add(this.getDescription(stack, world));
	}

	public String getDescription(ItemStack itemStack,  World world) {
		return LanguageManager.translate(this.getUnlocalizedName() + ".description");
	}
    
    
	// ==================================================
	//                       Fluid
	// ==================================================
	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		
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
                    IBlockState adjBlockState = world.getBlockState(adjBlockPos);
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
	}
    
    
	// ==================================================
	//                      Collision
	// ==================================================
	@Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if(entity instanceof EntityItem)
			entity.attackEntityFrom(DamageSource.LAVA, 10F);
        super.onEntityCollidedWithBlock(world, pos, state, entity);
	}
    
    
	// ==================================================
	//                      Particles
	// ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState blockState, World world, BlockPos pos, Random random) {
        float f; 
        float f1;
        float f2;
        
        if (random.nextInt(100) == 0) {
	        f = (float)pos.getX() + random.nextFloat();
	        f1 = (float)pos.getY() + random.nextFloat() * 0.5F;
	        f2 = (float)pos.getZ() + random.nextFloat();
	        world.spawnParticle(EnumParticleTypes.LAVA, (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
        }
        super.randomDisplayTick(blockState, world, pos, random);
    }
}
