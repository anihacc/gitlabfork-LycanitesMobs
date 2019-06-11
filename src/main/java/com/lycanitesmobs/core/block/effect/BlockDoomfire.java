package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockFireBase;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockDoomfire extends BlockFireBase {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockDoomfire() {
		super(Material.FIRE, LycanitesMobs.modInfo, "doomfire");

        // Stats:
        this.tickRate = 30;
        this.dieInRain = false;
        this.triggerTNT = false;
        this.agingRate = 8;
        this.spreadChance = 3;
        this.removeOnTick = !ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Doomfire", true);
        this.removeOnNoFireTick = ConfigBase.getConfig(this.group, "general").getBool("Features", "Remove Doomfire on No Fire Tick", false);

        this.setLightOpacity(1);
        this.setLightLevel(0.8F);
	}


    // ==================================================
    //                       Break
    // ==================================================
    @Override
    public Item getItemDropped(BlockState state, Random random, int zero) {
        return ObjectManager.getItem("doomfirecharge");
    }


    // ==================================================
    //                Collision Effects
    // ==================================================
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onEntityCollidedWithBlock(world, pos, state, entity);

        if(entity instanceof EntityLivingBase) {
			Potion decay = ObjectManager.getPotionEffect("decay");
            if(decay != null) {
                PotionEffect effect = new PotionEffect(decay, 5 * 20, 0);
                EntityLivingBase entityLiving = (EntityLivingBase) entity;
                if (entityLiving.isPotionApplicable(effect))
                    entityLiving.addPotionEffect(effect);
            }
        }

        if(entity instanceof EntityItem)
            if(((EntityItem)entity).getItem().getItem() == ObjectManager.getItem("hellfirecharge"))
                return;

        if(entity.isImmuneToFire())
            return;
        entity.attackEntityFrom(DamageSource.IN_FIRE, 2);
        entity.setFire(5);
    }


    // ==================================================
    //                      Particles
    // ==================================================
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if(random.nextInt(24) == 0)
            world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("doomfire"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

        for(int particleCount = 0; particleCount < 12; ++particleCount) {
            float particleX = (float)x + random.nextFloat();
            float particleY = (float)y + random.nextFloat() * 0.5F;
            float particleZ = (float)z + random.nextFloat();
            world.spawnParticle(EnumParticleTypes.REDSTONE, (double)particleX, (double)particleY, (double)particleZ, 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }
}