package com.lycanitesmobs.core.block.effect;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.StateContainer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockFrostCloud extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockFrostCloud(Block.Properties properties) {
		super(properties);

		this.group = LycanitesMobs.modInfo;
		this.blockName = "frostcloud";
		
		// Stats:
		this.tickRate = 200;
		this.removeOnTick = true;
		this.loopTicks = false;
		this.canBeCrushed = true;

		this.noBreakCollision = true;

		this.setRegistryName(this.group.modid, this.blockName.toLowerCase());
		this.setDefaultState(this.getStateContainer().getBaseState().with(AGE, 0));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}


	// ==================================================
	//                     Break
	// ==================================================
    /*@Override
    public Item getItemDropped(BlockState blockState, Random random, int fortune) {
        return ObjectManager.getItem("frostyfur");
    }

    @Override
    public int damageDropped(BlockState blockState) {
        return 0;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }*/
    
    
	// ==================================================
	//                Collision Effects
	// ==================================================
    @Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(blockState, world, pos, entity);

		if(entity instanceof LivingEntity) {
			LivingEntity entityLiving = (LivingEntity)entity;
			entityLiving.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 3 * 20, 0)); // Slowness
			entityLiving.addPotionEffect(new EffectInstance(Effects.HUNGER, 3 * 20, 0)); // Hunger
		}
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
			world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), ObjectManager.getSound("frostcloud"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

		if (random.nextInt(100) == 0) {
			x += random.nextFloat();
			z += random.nextFloat();
			world.addParticle(ParticleTypes.ITEM_SNOWBALL, x, y, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.CLOUD, x, y, z, 0.0D, 0.0D, 0.0D);
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
