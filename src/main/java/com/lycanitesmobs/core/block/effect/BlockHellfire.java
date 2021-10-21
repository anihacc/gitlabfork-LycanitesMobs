package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockFireBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockHellfire extends BlockFireBase {

	public BlockHellfire(Block.Properties properties) {
        super(properties, LycanitesMobs.modInfo, "hellfire");

        // Stats:
        this.tickRate = 30;
        this.dieInRain = false;
        this.triggerTNT = false;
        this.agingRate = 1;
        this.spreadChance = 1;
        this.removeOnTick = false;
        this.removeOnNoFireTick = false;
	}

    /*@Override
    public Item getItemDropped(BlockState state, Random random, int zero) {
        return ObjectManager.getItem("hellfirecharge");
    }*/

    @Override
    public void entityInside(BlockState blockState, Level world, BlockPos pos, Entity entity) {
        super.entityInside(blockState, world, pos, entity);

        if(entity instanceof LivingEntity) {
            MobEffect decay = ObjectManager.getEffect("decay");
            if(decay != null) {
                MobEffectInstance effect = new MobEffectInstance(decay, 5 * 20, 0);
                LivingEntity entityLiving = (LivingEntity)entity;
                if(entityLiving.canBeAffected(effect))
                    entityLiving.addEffect(effect);
            }
        }

        if(entity instanceof ItemEntity)
            if(((ItemEntity)entity).getItem().getItem() == ObjectManager.getItem("doomfirecharge"))
                return;

        if(entity.isInvulnerableTo(DamageSource.IN_FIRE))
            return;

        entity.hurt(DamageSource.IN_FIRE, 2);
        entity.setSecondsOnFire(5);
    }

    @Override
    public boolean isBlockFireSource(BlockState state, LevelReader world, BlockPos pos, Direction side) {
        if(state.getBlock() == Blocks.OBSIDIAN)
            return true;
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        if (random.nextInt(100) == 0) {
            x = pos.getX() + random.nextFloat();
            z = pos.getZ() + random.nextFloat();
            world.addParticle(DustParticleOptions.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
        }
        super.animateTick(state, world, pos, random);
    }
}