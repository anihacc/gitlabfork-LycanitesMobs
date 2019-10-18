package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.creature.EntityCacodemon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    public EntityDemonicBlast(World world, EntityLivingBase entityLivingBase) {
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
    public void onUpdate() {
    	super.onUpdate();
    	if(!this.getEntityWorld().isRemote) {
	    	if(rapidTicks % 5 == 0 && !isDead) {
                for(int i = 0; i < 6; i++) {
                    fireProjectile();
                }
	    	}
	    	if(rapidTicks == Integer.MAX_VALUE)
	    		rapidTicks = -1;
	    	rapidTicks++;
    	}
    	
    	if(this.posY > this.getEntityWorld().getHeight() + 20)
    		this.setDead();
    	
    	if(this.ticksExisted >= this.expireTime * 20)
    		this.setDead();
    }
	
    
    // ==================================================
 	//                 Fire Projectile
 	// ==================================================
    public void fireProjectile() {
    	World world = this.getEntityWorld();
    	
		IProjectile projectile;
		if(this.getThrower() != null) {
			projectile = (IProjectile) new EntityDemonicSpark(world, this.getThrower());
			if(projectile instanceof Entity) {
				((Entity)projectile).posX = this.posX;
				((Entity)projectile).posY = this.posY;
				((Entity)projectile).posZ = this.posZ;
			}
		}
		else
			projectile = (IProjectile) new EntityDemonicSpark(world, this.posX, this.posY, this.posZ);
		float velocity = 1.2F;
		double motionT = this.motionX + this.motionY + this.motionZ;
		if(this.motionX < 0) motionT -= this.motionX * 2;
		if(this.motionY < 0) motionT -= this.motionY * 2;
		if(this.motionZ < 0) motionT -= this.motionZ * 2;
        projectile.shoot(this.motionX / motionT + (rand.nextGaussian() - 0.5D), this.motionY / motionT + (rand.nextGaussian() - 0.5D), this.motionZ / motionT + (rand.nextGaussian() - 0.5D), velocity, 0);
        
        if(projectile instanceof EntityProjectileBase) {
            this.playSound(((EntityProjectileBase) projectile).getLaunchSound(), 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
        }
        
        world.spawnEntity((Entity) projectile);
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
    public boolean onEntityLivingDamage(EntityLivingBase entityLiving) {
    	entityLiving.addPotionEffect(new PotionEffect(MobEffects.WITHER, this.getEffectDuration(10), 0));
		if(ObjectManager.getEffect("decay") != null) {
			entityLiving.addPotionEffect(new PotionEffect(ObjectManager.getEffect("decay"), this.getEffectDuration(60), 0));
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
			if (this.getThrower() != null && this.getThrower() instanceof BaseCreatureEntity) {
				BaseCreatureEntity baseCreatureEntity = (BaseCreatureEntity) this.getThrower();
				if(baseCreatureEntity instanceof EntityCacodemon && !((EntityCacodemon) baseCreatureEntity).cacodemonGreifing) {
					super.onImpactComplete(this.getPosition());
					return;
				}
				if(baseCreatureEntity.getOwner() == entity || baseCreatureEntity.getControllingPassenger() == entity) {
					super.onImpactComplete(this.getPosition());
					return;
				}
				if (baseCreatureEntity.getSubspeciesIndex() > 0) {
					explosionRadius += 2;
				}
				if (baseCreatureEntity.getSubspeciesIndex() > 2) {
					explosionRadius = 2;
				}
			}
			this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, explosionRadius, true);
		}
    	for(int i = 0; i < 8; ++i) {
			fireProjectile();
		}
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.getEntityWorld().spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound("DemonicBlast");
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness() {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
}
