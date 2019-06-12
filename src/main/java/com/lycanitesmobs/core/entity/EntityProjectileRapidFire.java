package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;

public class EntityProjectileRapidFire extends EntityProjectileBase {
	// Properties:
	public LivingEntity shootingEntity;
	private float projectileWidth = 0.2f;
	private float projectileHeight = 0.2f;
	
	// Rapid Fire:
	private Class projectileClass;
	private ProjectileInfo projectileInfo;
	private int rapidTime = 100;
	private int rapidDelay = 5;
	
	// Offsets:
	public double offsetX = 0;
	public double offsetY = 0;
	public double offsetZ = 0;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityProjectileRapidFire(Class entityClass, World world, int setTime, int setDelay) {
        super(world);
        this.setSize(projectileWidth, projectileHeight);
		this.projectileClass = entityClass;
        this.rapidTime = setTime;
        this.rapidDelay = setDelay;
        this.noClip = true;
    }

    public EntityProjectileRapidFire(Class entityClass, World world, double par2, double par4, double par6, int setTime, int setDelay) {
        super(world, par2, par4, par6);
        this.setSize(projectileWidth, projectileHeight);
		this.projectileClass = entityClass;
        this.rapidTime = setTime;
        this.rapidDelay = setDelay;
        this.noClip = true;
    }

    public EntityProjectileRapidFire(Class entityClass, World world, LivingEntity entityLivingBase, int setTime, int setDelay) {
        super(world, entityLivingBase);
        this.setSize(projectileWidth, projectileHeight);
        this.projectileClass = entityClass;
        this.shootingEntity = entityLivingBase;
        this.offsetX = this.posX - entityLivingBase.posX;
        this.offsetY = this.posY - entityLivingBase.posY;
        this.offsetZ = this.posZ - entityLivingBase.posZ;
        this.rapidTime = setTime;
        this.rapidDelay = setDelay;
        this.noClip = true;
    }

	public EntityProjectileRapidFire(ProjectileInfo projectileInfo, World world, double par2, double par4, double par6, int setTime, int setDelay) {
		super(world, par2, par4, par6);
		this.setSize(projectileWidth, projectileHeight);
		this.projectileInfo = projectileInfo;
		this.rapidTime = setTime;
		this.rapidDelay = setDelay;
		this.noClip = true;
	}

	public EntityProjectileRapidFire(ProjectileInfo projectileInfo, World world, LivingEntity entityLivingBase, int setTime, int setDelay) {
		super(world, entityLivingBase);
		this.setSize(projectileWidth, projectileHeight);
		this.projectileInfo = projectileInfo;
		this.shootingEntity = entityLivingBase;
		this.offsetX = this.posX - entityLivingBase.posX;
		this.offsetY = this.posY - entityLivingBase.posY;
		this.offsetZ = this.posZ - entityLivingBase.posZ;
		this.rapidTime = setTime;
		this.rapidDelay = setDelay;
		this.noClip = true;
	}
	
    
    // ==================================================
 	//                   Update
 	// ==================================================
    @Override
    public void onUpdate() {
    	if(this.shootingEntity != null) {
    		this.posX = shootingEntity.posX + this.offsetX;
    		this.posY = shootingEntity.posY + this.offsetY;
    		this.posZ = shootingEntity.posZ + this.offsetZ;
    	}
    	if(rapidTime > 0) {
	    	if(projectileClass == null && this.projectileInfo == null) {
	    		rapidTime = 0;
	    		return;
	    	}
	    	
	    	if(rapidTime % rapidDelay == 0)
	    		fireProjectile();
	    	
	    	rapidTime--;
    	}
    	else if(!this.isDead) {
    		this.remove();
    	}
    }
	
    
    // ==================================================
 	//                    Add Time
 	// ==================================================
	public void addTime(int addTime) {
		this.rapidTime += addTime;
	}
	
    
    // ==================================================
 	//                 Fire Projectile
 	// ==================================================
    public void fireProjectile() {
    	World world = this.getEntityWorld();
    	if(world.isRemote)
    		return;
    	
		try {
	        IProjectile projectile;

	        if(this.shootingEntity == null) {
				if(this.projectileInfo != null) {
					projectile = this.projectileInfo.createProjectile(this.getEntityWorld(), this.posX, this.posY, this.posZ);
					projectile.shoot(this.motionX, this.motionY, this.motionZ, (float)this.projectileInfo.velocity, 0);
				}
				else {
					Constructor constructor = projectileClass.getDeclaredConstructor(new Class[]{World.class, double.class, double.class, double.class});
					constructor.setAccessible(true);
					projectile = (IProjectile) constructor.newInstance(new Object[]{world, this.posX, this.posY, this.posZ});
					projectile.shoot(this.motionX, this.motionY, this.motionZ, 1, 1);
				}
	        }
	        else {
	        	if(this.projectileInfo != null) {
					projectile = this.projectileInfo.createProjectile(this.getEntityWorld(), this.shootingEntity);
					projectile.shoot(this.motionX, this.motionY, this.motionZ, (float)this.projectileInfo.velocity, 0);
				}
	        	else {
					Constructor constructor = projectileClass.getDeclaredConstructor(new Class[]{World.class, LivingEntity.class});
					constructor.setAccessible(true);
					projectile = (IProjectile) constructor.newInstance(new Object[]{world, this.shootingEntity});
					projectile.shoot(this.motionX, this.motionY, this.motionZ, 1, 1);
				}
                if(projectile instanceof EntityThrowable) {
                    EntityThrowable entityThrowable = (EntityThrowable)projectile;
                    entityThrowable.setPosition(this.shootingEntity.posX + this.offsetX, this.shootingEntity.posY + this.offsetY, this.shootingEntity.posZ + this.offsetZ);
                }
	        }
	        
	        if(projectile instanceof EntityProjectileBase) {
                ((EntityProjectileBase) projectile).setProjectileScale(this.projectileScale);
            }
	        
	        world.spawnEntity((Entity)projectile);
		}
		catch (Exception e) {
			System.out.println("[WARNING] [LycanitesMobs] EntityRapidFire was unable to instantiate the given projectile class.");
			e.printStackTrace();
		}
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    // ========== Set Position ==========
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
        if(this.shootingEntity != null) {
            this.offsetX = x - this.shootingEntity.posX;
            this.offsetY = y - this.shootingEntity.posY;
            this.offsetZ = z - this.shootingEntity.posZ;
        }
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    @Override
    protected void onImpact(RayTraceResult rayTraceResult) {
    	return;
    }
    
    
    // ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getTexture() {
    	return null;
    }
}
