package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.creature.EntityCacodemon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IProjectile;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.Effects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityDemonicBlast extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	public int expireTime = 15;
	
	// Rapid Fire:
	private int rapidTicks = 0;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityDemonicBlast(World world) {
        super(world);
    }

    public EntityDemonicBlast(World world, LivingEntity entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityDemonicBlast(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "demoniclightning";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(10);
    	this.setProjectileScale(2.5F);
		this.ripper = true;
    }
	
    
    // ==================================================
 	//                   Update
 	// ==================================================
    @Override
    public void tick() {
    	super.tick();
    	if(!this.getEntityWorld().isRemote) {
	    	if(rapidTicks % 5 == 0 && this.isAlive()) {
                for(int i = 0; i < 6; i++) {
                    fireProjectile();
                }
	    	}
	    	if(rapidTicks == Integer.MAX_VALUE)
	    		rapidTicks = -1;
	    	rapidTicks++;
    	}
    	
    	if(this.posY > this.getEntityWorld().getActualHeight() + 20)
    		this.remove();
    	
    	if(this.ticksExisted >= this.expireTime * 20)
    		this.remove();
    }
	
    
    // ==================================================
 	//                 Fire Projectile
 	// ==================================================
    public void fireProjectile() {
    	World world = this.getEntityWorld();
    	
		EntityProjectileBase projectile;
		if(this.getThrower() != null) {
			projectile = new EntityDemonicSpark(world, this.getThrower());
			if(projectile instanceof Entity) {
				projectile.posX = this.posX;
				projectile.posY = this.posY;
				projectile.posZ = this.posZ;
			}
		}
		else {
			projectile = new EntityDemonicSpark(world, this.posX, this.posY, this.posZ);
		}

		float velocity = 1.2F;
		double motionT = this.getMotion().getX() + this.getMotion().getY() + this.getMotion().getZ();
		if(this.getMotion().getX() < 0) motionT -= this.getMotion().getX() * 2;
		if(this.getMotion().getY() < 0) motionT -= this.getMotion().getY() * 2;
		if(this.getMotion().getZ() < 0) motionT -= this.getMotion().getZ() * 2;
        projectile.shoot(this.getMotion().getX() / motionT + (rand.nextGaussian() - 0.5D), this.getMotion().getY() / motionT + (rand.nextGaussian() - 0.5D), this.getMotion().getZ() / motionT + (rand.nextGaussian() - 0.5D), velocity, 0);

		this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));

		world.addEntity(projectile);
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0.0001F;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean onEntityLivingDamage(LivingEntity entityLiving) {
    	entityLiving.addPotionEffect(new EffectInstance(Effects.WITHER, this.getEffectDuration(10), 0));
		if(ObjectManager.getEffect("decay") != null) {
			entityLiving.addPotionEffect(new EffectInstance(ObjectManager.getEffect("decay"), this.getEffectDuration(60), 0));
		}
    	return true;
    }
    
    //========== On Impact Splash/Ricochet ==========
    @Override
	public void onEntityCollision(Entity entity) {
    	if(this.getThrower() != null && this.getThrower().getRidingEntity() == entity) {
    		return;
		}
		if(this.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
			int explosionRadius = 2;
			if (this.getThrower() != null && this.getThrower() instanceof EntityCreatureBase) {
				EntityCreatureBase entityCreatureBase = (EntityCreatureBase) this.getThrower();
				if(entityCreatureBase instanceof EntityCacodemon && !((EntityCacodemon)entityCreatureBase).cacodemonGreifing) {
					super.onImpactComplete(this.getPosition());
					return;
				}
				if(entityCreatureBase.getOwner() == entity || entityCreatureBase.getControllingPassenger() == entity) {
					super.onImpactComplete(this.getPosition());
					return;
				}
				if (entityCreatureBase.getSubspeciesIndex() > 0) {
					explosionRadius += 2;
				}
				if (entityCreatureBase.getSubspeciesIndex() > 2) {
					explosionRadius = 2;
				}
			}
			this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, explosionRadius, Explosion.Mode.BREAK);
		}
    	for(int i = 0; i < 8; ++i) {
			fireProjectile();
		}
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.getEntityWorld().addParticle(RedstoneParticleData.REDSTONE_DUST, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return ObjectManager.getSound("DemonicBlast");
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
