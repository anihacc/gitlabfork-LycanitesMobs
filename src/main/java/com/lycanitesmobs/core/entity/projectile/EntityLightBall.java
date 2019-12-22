package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.ModelProjectileEntity;
import com.lycanitesmobs.core.entity.creature.EntityWisp;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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

public class EntityLightBall extends ModelProjectileEntity {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityLightBall(EntityType<? extends BaseProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public EntityLightBall(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity entityLivingBase) {
        super(entityType, world, entityLivingBase);
        this.shootingEntity = entityLivingBase;
    }

    public EntityLightBall(EntityType<? extends BaseProjectileEntity> entityType, World world, double x, double y, double z) {
        super(entityType, world, x, y, z);
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
    	if(this.getPositionVec().getY() > this.getEntityWorld().getActualHeight() + 20)
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
			this.remove();
		}
	}

    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
		if(this.getEntityWorld().isRemote && !CreatureManager.getInstance().config.disableBlockParticles) {
			for (int i = 0; i < 8; ++i) {
				this.getEntityWorld().addParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.GLOWSTONE.getDefaultState()),
						this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
						this.getPositionVec().getY() + this.rand.nextDouble() * (double) this.getSize(Pose.STANDING).height,
						this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
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
