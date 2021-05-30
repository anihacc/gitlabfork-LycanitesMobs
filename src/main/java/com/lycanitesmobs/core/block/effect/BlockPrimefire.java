package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockFireBase;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockPrimefire extends BlockFireBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public BlockPrimefire(Properties properties) {
        this(properties, "Primefire");
    }

    public BlockPrimefire(Properties properties, String name) {
        super(properties, LycanitesMobs.modInfo, name);

        // Stats:
        this.tickRate = 30;
        this.dieInRain = false;
        this.triggerTNT = true;
        this.agingRate = 8;
        this.spreadChance = 6;
        this.removeOnTick = false;
        this.removeOnNoFireTick = false;
    }


    // ==================================================
    //                       Break
    // ==================================================
    /*@Override
    public Item getItemDropped(BlockState state, Random random, int zero) {
        return ObjectManager.getItem("scorchfirecharge");
    }*/


    // ==================================================
    //                Collision Effects
    // ==================================================
    @Override
    public void entityInside(BlockState blockState, World world, BlockPos pos, Entity entity) {
        super.entityInside(blockState, world, pos, entity);

        if(entity instanceof LivingEntity) {
            Effect smoldering  = ObjectManager.getEffect("smoldering ");
            if(smoldering  != null) {
                EffectInstance effect = new EffectInstance(smoldering , 3 * 20, 0);
                LivingEntity entityLiving = (LivingEntity)entity;
                if(entityLiving.canBeAffected(effect))
                    entityLiving.addEffect(effect);
            }
        }

        if(entity instanceof ItemEntity)
            if(((ItemEntity)entity).getItem().getItem() == ObjectManager.getItem("primecharge"))
                return;

        if(entity.isInvulnerableTo(DamageSource.IN_FIRE))
            return;

        entity.hurt(DamageSource.IN_FIRE, 2);
        entity.setSecondsOnFire(5);
    }


    // ==================================================
    //                      Particles
    // ==================================================
    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        if(random.nextInt(24) == 0)
            world.playLocalSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), ObjectManager.getSound("primefire"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

        if (random.nextInt(100) == 0) {
            x = pos.getX() + random.nextFloat();
            z = pos.getZ() + random.nextFloat();
            world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
            world.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        }
        super.animateTick(state, world, pos, random);
    }
}