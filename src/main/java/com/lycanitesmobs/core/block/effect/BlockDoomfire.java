package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.config.ConfigBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockDoomfire extends BlockFireBase {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockDoomfire(Block.Properties properties) {
		super(properties, LycanitesMobs.modInfo, "doomfire");

        // Stats:
        this.tickRate = 30;
        this.dieInRain = false;
        this.triggerTNT = false;
        this.agingRate = 8;
        this.spreadChance = 3;
        this.removeOnTick = !ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Doomfire", true);
        this.removeOnNoFireTick = ConfigBase.getConfig(this.group, "general").getBool("Features", "Remove Doomfire on No Fire Tick", false);

        //this.setLightOpacity(1);
        //this.setLightLevel(0.8F);
	}


    // ==================================================
    //                       Break
    // ==================================================
    /*@Override
    public Item getItemDropped(BlockState state, Random random, int zero) {
        return ObjectManager.getItem("doomfirecharge");
    }*/


    // ==================================================
    //                Collision Effects
    // ==================================================
    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(blockState, world, pos, entity);

        if(entity instanceof LivingEntity) {
            Effect decay = ObjectManager.getEffect("decay");
            if(decay != null) {
                EffectInstance effect = new EffectInstance(decay, 5 * 20, 0);
                LivingEntity entityLiving = (LivingEntity)entity;
                if(entityLiving.isPotionApplicable(effect))
                    entityLiving.addPotionEffect(effect);
            }
        }

        if(entity instanceof ItemEntity)
            if(((ItemEntity)entity).getItem().getItem() == ObjectManager.getItem("hellfirecharge"))
                return;

        if(entity.isImmuneToFire())
            return;

        entity.attackEntityFrom(DamageSource.IN_FIRE, 1);
        entity.setFire(5);
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
            world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("doomfire"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

        if (random.nextInt(100) == 0) {
            x = pos.getX() + random.nextFloat();
            z = pos.getZ() + random.nextFloat();
            world.addParticle(RedstoneParticleData.REDSTONE_DUST, x, y, z, 0.0D, 0.0D, 0.0D);
        }
        super.animateTick(state, world, pos, random);
    }
}