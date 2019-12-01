package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.LaserProjectileEntity;
import com.lycanitesmobs.core.entity.creature.EntityBeholder;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class EntityArcaneLaserStorm extends BaseProjectileEntity {

	// Properties:
	public Entity shootingEntity;
	public int expireTime = 15;
    public int laserMax = 7;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityArcaneLaserStorm(EntityType<? extends BaseProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public EntityArcaneLaserStorm(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity entityLiving) {
        super(entityType, world, entityLiving);
    }

    public EntityArcaneLaserStorm(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6) {
        super(entityType, world, par2, par4, par6);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "arcanelaserstorm";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(4);
    	this.setProjectileScale(4F);
        this.ripper = true;
    }
	
    
    // ==================================================
 	//                   Update
 	// ==================================================
    @Override
    public void tick() {
    	super.tick();
    	if(!this.getEntityWorld().isRemote) {
	    	updateLasers();
    	}
    	
    	if(this.posY > this.getEntityWorld().getActualHeight() + 20)
    		this.remove();
    	
    	if(this.ticksExisted >= this.expireTime * 20)
    		this.remove();
    }
	
    
    // ==================================================
 	//                 Fire Projectile
 	// ==================================================
    List<LaserProjectileEntity> lasers = new ArrayList<LaserProjectileEntity>();
    int laserTick = 0;
    public void updateLasers() {
    	World world = this.getEntityWorld();

        while(this.lasers.size() < this.laserMax) {
            LaserProjectileEntity laser;
            if(this.getThrower() != null) {
                laser = new EntityArcaneLaser(ProjectileManager.getInstance().oldProjectileTypes.get(EntityArcaneLaser.class), world, this.getThrower(), 20, 10, this);
                laser.posX = this.posX;
                laser.posY = this.posY;
                laser.posZ = this.posZ;
            }
            else
                laser = new EntityArcaneLaser(ProjectileManager.getInstance().oldProjectileTypes.get(EntityArcaneLaser.class), world, this.posX, this.posY, this.posZ, 20, 10, this);
            laser.useEntityAttackTarget = false;
            this.lasers.add(laser);
            world.addEntity(laser);
        }

        int laserCount = 0;
        for(LaserProjectileEntity laser : this.lasers) {
            laser.setTime(20);
            double[] target = new double[]{this.posX, this.posY, this.posZ};

            if(laserCount == 0)
                target = this.getFacingPosition(this, laser.laserLength, 135);
            if(laserCount == 1)
                target = this.getFacingPosition(this, laser.laserLength, 90);
            if(laserCount == 2)
                target = this.getFacingPosition(this, laser.laserLength, 45);
            if(laserCount == 3)
                target = this.getFacingPosition(this, laser.laserLength, 0);
            if(laserCount == 4)
                target = this.getFacingPosition(this, laser.laserLength, -45);
            if(laserCount == 5)
                target = this.getFacingPosition(this, laser.laserLength, -90);
            if(laserCount == 6)
                target = this.getFacingPosition(this, laser.laserLength, -135);

            if(laserCount == 0 || laserCount == 2 || laserCount == 4 || laserCount == 6)
                target[1] -= laser.laserLength / 2;
            else
                target[1] += laser.laserLength / 2;

            target[0] += (MathHelper.cos(this.laserTick * 0.25F) * 1.0F) - 0.5F;
            target[1] += (MathHelper.cos(this.laserTick * 0.25F) * 1.0F) - 0.5F;
            target[2] += (MathHelper.cos(this.laserTick * 0.25F) * 1.0F) - 0.5F;

            laser.setTarget(target[0], target[1], target[2]);
            laserCount++;
        }

        this.laserTick++;
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
    //========== On Impact Splash/Ricochet ==========
    @Override
	public void onEntityCollision(Entity entity) {
		if(this.getThrower() != null && this.getThrower().getRidingEntity() == entity) {
			return;
		}
		if(this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
			int explosionRadius = 2;
			if (this.getThrower() != null && this.getThrower() instanceof BaseCreatureEntity) {
				BaseCreatureEntity baseCreatureEntity = (BaseCreatureEntity) this.getThrower();
				if(baseCreatureEntity instanceof EntityBeholder && !((EntityBeholder) baseCreatureEntity).greifing) {
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
					explosionRadius += 2;
				}
			}
			this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, explosionRadius, Explosion.Mode.BREAK);
		}
    	super.onImpactComplete(this.getPosition());
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.getEntityWorld().addParticle(ParticleTypes.WITCH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return ObjectManager.getSound(this.entityName);
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    @Override
    public float getBrightness() {
        return 1.0F;
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public int getBrightnessForRender() {
        return 15728880;
    }
}
