package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockPoisonCloud extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockPoisonCloud(Block.Properties properties) {
		super(properties);
		this.setDefaultState(this.getStateContainer().getBaseState().with(AGE, 0));

        // Properties:
		this.group = LycanitesMobs.modInfo;
		this.blockName = "poisoncloud";
		this.setup();
		
		// Stats:
		this.tickRate = 200;
		this.removeOnTick = true;
		this.loopTicks = true;
		this.canBeCrushed = true;
		
		//this.noEntityCollision = true;
		this.noBreakCollision = true;
		this.isOpaque = false;
		
		//this.setBlockUnbreakable();
		//this.setLightOpacity(1);
	}


	// ==================================================
	//                     Break
	// ==================================================
	/*@Override
	public Item getItemDropped(BlockState blockState, Random random, int fortune) {
		return ObjectManager.getItem("poisongland");
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
			entityLiving.addPotionEffect(new EffectInstance(Effects.field_76436_u, 3 * 20, 0)); // Poison
		}
	}


	// ==================================================
	//                     Ticking
	// ==================================================
	// ========== Can Remove ==========
	/** Returns true if the block should be removed naturally (remove on tick). **/
	@Override
	public boolean canRemove(World world, BlockPos pos, BlockState state, Random rand) {
		if(world.getBlockState(pos.down()).getBlock() == Blocks.MYCELIUM)
			return false;
		return super.canRemove(world, pos, state, rand);
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
			world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("poisoncloud"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

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
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
