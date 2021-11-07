package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockBase;
import com.lycanitesmobs.core.info.ItemManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockPoisonCloud extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockPoisonCloud(Block.Properties properties) {
		super(properties);

		this.group = LycanitesMobs.modInfo;
		this.blockName = "poisoncloud";
		
		// Stats:
		this.tickRate = 200;
		this.removeOnTick = true;
		this.loopTicks = true;
		this.canBeCrushed = true;

		this.noBreakCollision = true;

		this.setRegistryName(this.group.modid, this.blockName.toLowerCase());
		this.registerDefaultState(this.getStateDefinition().any().setValue(AGE, 0));

		ItemManager.getInstance().cutoutBlocks.add(this);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}
    
    
	// ==================================================
	//                Collision Effects
	// ==================================================
	@Override
	public void entityInside(BlockState blockState, Level world, BlockPos pos, Entity entity) {
		super.entityInside(blockState, world, pos, entity);

		if(entity instanceof LivingEntity) {
			LivingEntity entityLiving = (LivingEntity)entity;
			entityLiving.addEffect(new MobEffectInstance(MobEffects.POISON, 3 * 20, 0)); // Poison
		}
	}


	// ==================================================
	//                     Ticking
	// ==================================================
	// ========== Can Remove ==========
	/** Returns true if the block should be removed naturally (remove on tick). **/
	@Override
	public boolean canRemove(Level world, BlockPos pos, BlockState state, Random rand) {
		if(world.getBlockState(pos.below()).getBlock() == Blocks.MYCELIUM)
			return false;
		return super.canRemove(world, pos, state, rand);
	}
    
    
	// ==================================================
	//                      Particles
	// ==================================================
    @Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		if(random.nextInt(24) == 0)
			world.playLocalSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), ObjectManager.getSound("poisoncloud"), SoundSource.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

		if (random.nextInt(100) == 0) {
			x += random.nextFloat();
			z += random.nextFloat();
			world.addParticle(ParticleTypes.PORTAL, x, y, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
		}
		super.animateTick(state, world, pos, random);
	}


	// ==================================================
	//                      Rendering
	// ==================================================
    /*@Override Redundant?
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }*/
}
