package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockFireBase;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockPrimefire extends BlockFireBase {

    public BlockPrimefire(Properties properties) {
        this(properties, "primefire");
    }

    public BlockPrimefire(Properties properties, String name) {
        super(properties, LycanitesMobs.modInfo, name);

        // Stats:
        this.tickRate = 30;
        this.dieInRain = false;
        this.triggerTNT = true;
        this.agingRate = 2;
        this.spreadChance = 6;
        this.removeOnTick = false;
        this.removeOnNoFireTick = false;
    }

    /*@Override
    public Item getItemDropped(BlockState state, Random random, int zero) {
        return ObjectManager.getItem("primeembercharge");
    }*/

    @Override
    public void entityInside(BlockState blockState, Level world, BlockPos pos, Entity entity) {
        super.entityInside(blockState, world, pos, entity);

        if(entity instanceof LivingEntity) {
            MobEffect effect = ObjectManager.getEffect("smouldering");
            if(effect != null) {
                MobEffectInstance effectInstance = new MobEffectInstance(effect, 10 * 20, 0);
                LivingEntity entityLiving = (LivingEntity)entity;
                if(entityLiving.canBeAffected(effectInstance))
                    entityLiving.addEffect(effectInstance);
            }
        }

        if(entity instanceof ItemEntity)
            if(((ItemEntity)entity).getItem().getItem() == ObjectManager.getItem("primeembercharge"))
                return;

        if(entity.isInvulnerableTo(DamageSource.IN_FIRE))
            return;

        entity.hurt(DamageSource.IN_FIRE, 2);
        entity.setSecondsOnFire(5);
    }

    @Override
    public void burnBlockReplace(Level world, BlockPos pos, int newFireAge) {
        BlockState replaceFireBlock = this.defaultBlockState().setValue(AGE, newFireAge).setValue(PERMANENT, false);
        if (world.getRandom().nextFloat() <= 0.25F) {
            replaceFireBlock = Blocks.FIRE.defaultBlockState().setValue(FireBlock.AGE, newFireAge);
        }
        world.setBlock(pos, replaceFireBlock, 3);
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
            world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
            world.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        }
        super.animateTick(state, world, pos, random);
    }
}