package com.lycanitesmobs.core.block.fluid;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockFluidBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockFluidOoze extends BlockFluidBase {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockFluidOoze(FlowingFluid fluid, Block.Properties properties) {
        super(fluid, properties, LycanitesMobs.modInfo, "ooze");

        //this.setLightOpacity(0);
        //this.setLightLevel(0.25F);
	}


    // ==================================================
    //                       Fluid
    // ==================================================
    /*@Override
    public boolean canDisplace(IBlockAccess world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if(blockState == null || blockState.getBlock() == this) {
            return false;
        }

        // Freeze Water:
        if(blockState.getMaterial() == Material.WATER) {
            if(world instanceof World) {
                ((World)world).setBlockState(pos, Blocks.PACKED_ICE.getDefaultState());
            }
            return false;
        }

        // Freeze Lava:
        if(blockState.getMaterial() == Material.LAVA) {
            if(world instanceof World) {
                ((World)world).setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
            }
            return false;
        }

        if(blockState.getMaterial().isLiquid()) {
            return false;
        }

        return super.canDisplace(world, pos);
    }

    @Override
    public boolean displaceIfPossible(World world, BlockPos pos) {
        if(world.getBlockState(pos).getMaterial().isLiquid()) return this.canDisplace(world, pos);
        return super.displaceIfPossible(world, pos);
    }*/
    
    
	// ==================================================
	//                      Collision
	// ==================================================
    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos pos, Entity entity) {
        // Damage:
        if (!(entity instanceof ItemEntity) && !(entity instanceof ExperienceOrbEntity)) {
            entity.attackEntityFrom(ObjectManager.getDamageSource("ooze"), 1F);
        }

        // Extinguish:
        if(entity.isBurning())
            entity.extinguish();

        // Effects:
        if(entity instanceof LivingEntity) {
            ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.field_76421_d, 5 * 20, 0));
        }
        super.onEntityCollision(blockState, world, pos, entity);
    }
    
    
	// ==================================================
	//                      Particles
	// ==================================================
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