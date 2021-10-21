package com.lycanitesmobs.core.block.fluid;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.ElementInfo;
import net.minecraft.block.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class OozeFluidBlock extends BaseFluidBlock {
	public OozeFluidBlock(Supplier<? extends FlowingFluid> fluidSupplier, Block.Properties properties, String name, ElementInfo element, boolean destroyItems) {
        super(fluidSupplier, properties, name, element, destroyItems);
	}

    @Override
    public boolean shouldSpreadLiquid(Level world, BlockPos neighborBlockPos, BlockState blockState) {
        BlockState neighborBlockState = world.getBlockState(neighborBlockPos);

        // Freeze Water:
        if (neighborBlockState.getMaterial() == Material.WATER) {
            world.setBlock(neighborBlockPos, Blocks.PACKED_ICE.defaultBlockState(), 4);
            return false;
        }

        // Freeze Lava:
        if (neighborBlockState.getMaterial() == Material.LAVA) {
            world.setBlock(neighborBlockPos, Blocks.OBSIDIAN.defaultBlockState(), 4);
            return false;
        }

        return super.shouldSpreadLiquid(world, neighborBlockPos, blockState);
    }

    @Override
    public void entityInside(BlockState blockState, Level world, BlockPos pos, Entity entity) {
        // Damage:
        if (!(entity instanceof ItemEntity) && !(entity instanceof ExperienceOrb)) {
            entity.hurt(ObjectManager.getDamageSource("ooze"), 1F);
        }

        // Extinguish:
        if(entity.isOnFire())
            entity.clearFire();

        // Effects:
        if(entity instanceof LivingEntity) {
            ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5 * 20, 0));
        }

        super.entityInside(blockState, world, pos, entity);
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
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
