package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.ElementManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockShadowfire extends BlockFireBase {
    public boolean blindness;

	public BlockShadowfire(Block.Properties properties) {
		super(properties, LycanitesMobs.modInfo, "shadowfire");
		
		// Stats:
		this.tickRate = 30;
        this.dieInRain = false;
        this.triggerTNT = false;
        this.agingRate = 3;
        this.spreadChance = 0;
        this.removeOnTick = false;
        this.removeOnNoFireTick = false;
		this.blindness = true;
	}

    protected boolean canNeighborCatchFire(Level worldIn, BlockPos pos) {
        return false;
    }

    protected int getNeighborEncouragement(Level worldIn, BlockPos pos) {
        return 0;
    }

	public boolean canCatchFire(BlockGetter world, BlockPos pos, Direction face) {
        return false;
    }

	@Override
	public boolean isBlockFireSource(BlockState state, LevelReader world, BlockPos pos, Direction side) {
		if(state.getBlock() == Blocks.OBSIDIAN || state.getBlock() == Blocks.CRYING_OBSIDIAN)
			return true;
		return true; // TODO Figure out why the PERMANENT property isn't working consistently.
	}

    protected boolean canDie(Level world, BlockPos pos) {
        return false;
    }

    @Override
	public void entityInside(BlockState blockState, Level world, BlockPos pos, Entity entity) {
		super.entityInside(blockState, world, pos, entity);

		if(entity instanceof ItemEntity)
			return;

		if(entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)entity;

			MobEffect decay = ObjectManager.getEffect("decay");
			if(decay != null) {
				MobEffectInstance effect = new MobEffectInstance(decay, 5 * 20, 0);
				if(livingEntity.canBeAffected(effect))
					livingEntity.addEffect(effect);
			}

			MobEffectInstance blindness = new MobEffectInstance(MobEffects.BLINDNESS, 5 * 20, 0);
			if(this.blindness && livingEntity.canBeAffected(blindness)) {
				livingEntity.addEffect(blindness);
			}
		}

		if(entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).hasElement(ElementManager.getInstance().getElement("shadow")))
			return;

		entity.hurt(DamageSource.WITHER, 1);
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
			world.addParticle(ParticleTypes.WITCH, x, y, z, 0.0D, 0.0D, 0.0D);
		}
		super.animateTick(state, world, pos, random);
	}
}