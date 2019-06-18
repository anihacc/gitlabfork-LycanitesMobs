package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileModel;
import com.lycanitesmobs.core.entity.creature.EntityWisp;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityLightBall extends EntityProjectileModel {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityLightBall(World world) {
        super(world);
    }

    public EntityLightBall(World world, LivingEntity entityLivingBase) {
        super(world, entityLivingBase);
        this.shootingEntity = entityLivingBase;
    }

    public EntityLightBall(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "lightball";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(2);
    	this.setProjectileScale(1F);
        this.projectileLife = 100;
        this.knockbackChance = 0;
    	
    	this.waterProof = true;
    }
	
    
    // ==================================================
 	//                   Update
 	// ==================================================
    @Override
    public void tick() {
    	super.tick();
    	if(this.posY > this.getEntityWorld().getActualHeight() + 20)
    		this.remove();
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0.001F;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
	//========== Entity Living Damage ==========
	@Override
	public boolean onEntityLivingDamage(LivingEntity entityLiving) {
    	entityLiving.addPotionEffect(new EffectInstance(Effects.GLOWING, this.getEffectDuration(20), 0));
		return true;
	}

	//========== Entity Collision ==========
	@Override
	public void onEntityCollision(Entity entity) {
		if(entity instanceof EntityWisp) {
			((EntityWisp)entity).resetAttackCooldown();
		}
	}

    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
		if(this.getEntityWorld().isRemote) {
			for (int i = 0; i < 8; ++i) {
				this.getEntityWorld().addParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.GLOWSTONE.getDefaultState()),
						this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
						this.posY + this.rand.nextDouble() * (double) this.getSize(Pose.STANDING).height,
						this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
						0.0D, 0.0D, 0.0D);
			}
		}
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return ObjectManager.getSound("lightball");
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness() {
        return 1.0F;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
}
