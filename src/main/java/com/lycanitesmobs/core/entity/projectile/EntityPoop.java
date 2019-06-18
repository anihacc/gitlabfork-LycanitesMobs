package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.block.Blocks;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.potion.Effects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityPoop extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityPoop(World world) {
        super(world);
    }

    public EntityPoop(World world, LivingEntity entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityPoop(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "poop";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(2);
    	this.setProjectileScale(1.0F);
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean onEntityLivingDamage(LivingEntity entityLiving) {
    	entityLiving.addPotionEffect(new EffectInstance(Effects.SLOWNESS, this.getEffectDuration(3), 0));
    	entityLiving.addPotionEffect(new EffectInstance(Effects.NAUSEA, this.getEffectDuration(5), 0));
    	return true;
    }

    //========== Can Destroy Block ==========
    @Override
    public boolean canDestroyBlock(BlockPos pos) {
        Block block = this.getEntityWorld().getBlockState(pos).getBlock();
        if(ObjectManager.getBlock("PoisonCloud") != null && block == ObjectManager.getBlock("PoisonCloud"))
            return true;
        if(ObjectManager.getBlock("PoopCloud") != null && block == ObjectManager.getBlock("PoopCloud"))
            return true;
        if(ObjectManager.getBlock("FrostCloud") != null && block == ObjectManager.getBlock("FrostCloud"))
            return true;
        return super.canDestroyBlock(pos);
    }
    
    //========== Place Block ==========
    @Override
    public void placeBlock(World world, BlockPos pos) {
        world.setBlockState(pos, ObjectManager.getBlock("poopcloud").getDefaultState());
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
            this.getEntityWorld().addParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.PODZOL.getDefaultState()),
                    this.posX, this.posY, this.posZ,
                    0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return ObjectManager.getSound("poop");
    }
}
