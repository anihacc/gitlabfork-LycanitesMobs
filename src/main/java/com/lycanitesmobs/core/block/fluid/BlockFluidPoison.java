package com.lycanitesmobs.core.block.fluid;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.block.BlockFluidBase;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import java.util.List;
import java.util.Random;

public class BlockFluidPoison extends BlockFluidBase {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockFluidPoison(Fluid fluid, String name) {
        super(fluid, Material.WATER, LycanitesMobs.modInfo, name);

        this.setLightOpacity(0);
        this.setLightLevel(0.25F);
        this.destroyItems = false;
	}


    // ==================================================
    //                      Info
    // ==================================================
    @Override
    public String getLocalizedName() {
        return LanguageManager.translate(this.getUnlocalizedName() + ".name");
    }

    @Override
    public void addInformation(ItemStack stack,  World world, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(this.getDescription(stack, world));
    }

    public String getDescription(ItemStack itemStack,  World world) {
        return LanguageManager.translate(this.getUnlocalizedName() + ".description");
    }


    // ==================================================
    //                       Fluid
    // ==================================================
    @Override
    public boolean canDisplace(IBlockAccess world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos);
        if(blockState.getBlock() == this) {
            return false;
        }

        // Gravel Water:
        if(blockState.getMaterial() == Material.WATER) {
            if(world instanceof World) {
                ((World)world).setBlockState(pos, Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE));
            }
            return false;
        }

        // Obsidian Lava:
        if(blockState.getMaterial() == Material.LAVA) {
            if(world instanceof World) {
                ((World)world).setBlockState(pos, Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE));
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
    }
    
    
	// ==================================================
	//                      Collision
	// ==================================================
	@Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if(entity != null) {
            // Effects:
            Potion effect = ObjectManager.getEffect("plague");
            if(effect != null && entity instanceof EntityLivingBase) {
                ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(effect, 10 * 20, 1));
            }
        }
		super.onEntityCollidedWithBlock(world, pos, state, entity);
	}
    
    
	// ==================================================
	//                      Particles
	// ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState blockState, World world, BlockPos pos, Random random) {
        float f; 
        float f1;
        float f2;
        
        if (random.nextInt(100) == 0) {
            f = (float)pos.getX() + random.nextFloat();
            f1 = (float)pos.getY() + random.nextFloat() * 0.5F;
            f2 = (float)pos.getZ() + random.nextFloat();
	        world.spawnParticle(EnumParticleTypes.PORTAL, f, f1, f2, 0.0D, 0.0D, 0.0D);
        }
        super.randomDisplayTick(blockState, world, pos, random);
    }
}
