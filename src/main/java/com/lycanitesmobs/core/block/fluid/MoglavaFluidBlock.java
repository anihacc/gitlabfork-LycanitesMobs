package com.lycanitesmobs.core.block.fluid;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

public class BlockFluidMoglava extends BaseFluidBlock {
	public BlockFluidMoglava(Supplier<? extends FlowingFluid> fluidSupplier, Block.Properties properties, String name) {
		super(fluidSupplier, properties, name);
	}

	@Override
	public boolean shouldSpreadLiquid(World world, BlockPos pos, BlockState blockState) {
		BlockState neighborBlockState = world.getBlockState(pos);

        // Water Cobblestone:
		if (blockState.getMaterial() == Material.WATER) {
			world.setBlock(pos, Blocks.STONE.defaultBlockState(), 2);
			return false;
        }

		return super.shouldSpreadLiquid(world, pos, blockState);
	}

	@Override
	public void entityInside(BlockState blockState, World world, BlockPos pos, Entity entity) {
		if(entity instanceof ItemEntity)
			entity.hurt(DamageSource.LAVA, 10F);
		super.entityInside(blockState, world, pos, entity);
	}

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
