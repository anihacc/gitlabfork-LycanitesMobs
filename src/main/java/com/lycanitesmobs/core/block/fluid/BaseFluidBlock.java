package com.lycanitesmobs.core.block.fluid;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
import java.util.function.Supplier;

public class BaseFluidBlock extends FlowingFluidBlock {
	public BaseFluidBlock(Supplier<? extends FlowingFluid> fluidSupplier, Properties properties, String name) {
        super(fluidSupplier, properties);
        this.setRegistryName(LycanitesMobs.MODID, name);
	}

	@Override
    public void neighborChanged(BlockState blockState, World world, BlockPos blockPos, Block neighborBlock, BlockPos neighborBlockPos, boolean someBoolean) {
	    super.neighborChanged(blockState, world, blockPos, neighborBlock, neighborBlockPos, someBoolean);
		if (neighborBlock == this) {
	        return;
        }
        BlockState neighborBlockState = world.getBlockState(neighborBlockPos);
		if (neighborBlockState.getBlock() == this) {
		    return;
        }
        if (this.shouldSpreadLiquid(world, neighborBlockPos, blockState)) {
            world.getLiquidTicks().scheduleTick(blockPos, blockState.getFluidState().getType(), this.getFluid().getTickDelay(world));
        }
    }

    public boolean shouldSpreadLiquid(World world, BlockPos neighborBlockPos, BlockState blockState) {
        BlockState neighborBlockState = world.getBlockState(neighborBlockPos);
        if (neighborBlockState.getMaterial().isLiquid()) {
            return false;
        }
        return true;
    }

    @Override
    public void entityInside(BlockState blockState, World world, BlockPos pos, Entity entity) {
        super.entityInside(blockState, world, pos, entity);
    }

//    @Override
//    public BlockRenderType getRenderShape(BlockState state) {
//        return BlockRenderType.MODEL;
//    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        float f; 
        float f1;
        float f2;
        
        if (random.nextInt(100) == 0) {
            f = (float)pos.getX() + random.nextFloat();
            f1 = (float)pos.getY() + random.nextFloat() * 0.5F;
            f2 = (float)pos.getZ() + random.nextFloat();
	        world.addParticle(ParticleTypes.RAIN, (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
        }
        super.animateTick(state, world, pos, random);
    }
}
