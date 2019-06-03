package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.PotionBase;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.config.ConfigBase;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockHellfire extends BlockFireBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockHellfire() {
		super(Material.FIRE, LycanitesMobs.modInfo, "hellfire");

        // Stats:
        this.tickRate = 30;
        this.dieInRain = false;
        this.triggerTNT = false;
        this.agingRate = 1;
        this.spreadChance = 1;
        this.removeOnTick = !ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Hellfire", true);
        this.removeOnNoFireTick = ConfigBase.getConfig(this.group, "general").getBool("Features", "Remove Hellfire On No Fire Tick", false);

        this.setLightOpacity(1);
        this.setLightLevel(0.8F);
	}


    // ==================================================
    //                       Break
    // ==================================================
    @Override
    public Item getItemDropped(IBlockState state, Random random, int zero) {
        return ObjectManager.getItem("hellfirecharge");
    }


    // ==================================================
    //                Collision Effects
    // ==================================================
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        super.onEntityCollidedWithBlock(world, pos, state, entity);

        if(entity instanceof EntityLivingBase) {
			PotionBase decay = ObjectManager.getPotionEffect("decay");
			if(decay != null) {
				PotionEffect effect = new PotionEffect(decay, 5 * 20, 0);
				EntityLivingBase entityLiving = (EntityLivingBase) entity;
				if (entityLiving.isPotionApplicable(effect)) {
					entityLiving.addPotionEffect(effect);
				}
			}
        }

        if(entity instanceof EntityItem && ((EntityItem)entity).getItem() != null)
            if(((EntityItem)entity).getItem().getItem() == ObjectManager.getItem("hellfirecharge"))
                return;
        if(entity.isImmuneToFire())
            return;
        entity.attackEntityFrom(DamageSource.IN_FIRE, 2);
        entity.setFire(5);
    }


    // ==================================================
    //                        Fire
    // ==================================================
    @Override
    public boolean isBlockFireSource(Block block, World world, BlockPos pos, EnumFacing side) {
        if(block == Blocks.OBSIDIAN)
            return true;
        return false;
    }


    // ==================================================
    //                      Particles
    // ==================================================
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if(random.nextInt(24) == 0)
            world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("hellfire"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

        for(int particleCount = 0; particleCount < 12; ++particleCount) {
            float particleX = (float)x + random.nextFloat();
            float particleY = (float)y + random.nextFloat() * 0.5F;
            float particleZ = (float)z + random.nextFloat();
            world.spawnParticle(EnumParticleTypes.REDSTONE, (double)particleX, (double)particleY, (double)particleZ, 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }
}