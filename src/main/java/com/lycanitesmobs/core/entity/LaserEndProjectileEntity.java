package com.lycanitesmobs.core.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class LaserEndProjectileEntity extends BaseProjectileEntity {
	// Laser End:
	private double targetX;
	private double targetY;
	private double targetZ;
	
	// Properties:
	public LivingEntity shootingEntity;
	public BaseProjectileEntity laserEntity;
	private double projectileSpeed;

    // Datawatcher:
    protected static final DataParameter<Float> POS_X = EntityDataManager.createKey(LaserEndProjectileEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> POS_Y = EntityDataManager.createKey(LaserEndProjectileEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> POS_Z = EntityDataManager.createKey(LaserEndProjectileEntity.class, DataSerializers.FLOAT);
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public LaserEndProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setStats();
    }

    public LaserEndProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6, LaserProjectileEntity laser) {
        super(entityType, world, par2, par4, par6);
        this.laserEntity = laser;
        this.setStats();
    }
    
    public LaserEndProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity shooter, LaserProjectileEntity laser) {
        super(entityType, world, shooter);
        this.shootingEntity = shooter;
        this.laserEntity = laser;
        this.setStats();
    }
    
    public void setStats() {
        this.setSpeed(1.0D);
        //this.setSize(projectileWidth, projectileHeight);
        if(laserEntity != null) {
	        this.targetX = this.laserEntity.getPositionVec().getX();
	        this.targetY = this.laserEntity.getPositionVec().getY();
	        this.targetZ = this.laserEntity.getPositionVec().getZ();
        }
        this.dataManager.register(POS_X, (float) this.getPositionVec().getX());
        this.dataManager.register(POS_Y, (float) this.getPositionVec().getY());
        this.dataManager.register(POS_Z, (float)this.getPositionVec().getZ());
        this.noClip = true;
    }
    
    
    // ==================================================
 	//                     Updates
 	// ==================================================
    // ========== Main Update ==========
    @Override
    public void tick() {
    	if(this.getEntityWorld().isRemote) {
    		this.setPosition(this.dataManager.get(POS_X), this.dataManager.get(POS_Y), this.dataManager.get(POS_Z));
    		return;
    	}
    	
    	if((this.laserEntity == null || !this.laserEntity.isAlive()) && this.isAlive())
    		this.remove();
    	
    	if(this.isAlive())
    		this.moveToTarget();
    	
    	this.dataManager.set(POS_X, (float) this.getPositionVec().getX());
    	this.dataManager.set(POS_Y, (float) this.getPositionVec().getY());
    	this.dataManager.set(POS_Z, (float) this.getPositionVec().getZ());
    }
    
    // ========== End Update ==========
	public void onUpdateEnd(double newTargetX, double newTargetY, double newTargetZ) {
		if(this.getEntityWorld().isRemote)
			return;
		
		this.targetX = newTargetX;
		this.targetY = newTargetY;
		this.targetZ = newTargetZ;
		
		if(this.getLaunchSound() != null)
			this.playSound(this.getLaunchSound(), 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
	}
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
    
    // ========== Move to Target ==========
    public void moveToTarget() {
    	this.setPosition(this.targetX, this.targetY, this.targetZ);
    	/*this.setPosition(
    			this.moveCoordToTarget(this.getPositionVec().getX(), this.targetX, this.laserEntity.getPositionVec().getX()),
    			this.moveCoordToTarget(this.getPositionVec().getY(), this.targetY, this.laserEntity.getPositionVec().getY()),
    			this.moveCoordToTarget(this.getPositionVec().getZ(), this.targetZ, this.laserEntity.getPositionVec().getZ())
		);*/
    }
    
    // ========== Move Coord ==========
    public double moveCoordToTarget(double coord, double targetCoord, double originCoord) {
    	double distance = targetCoord - coord;
    	double moveSpeed = this.projectileSpeed;
    	if(distance > 0) {
    		if(distance < moveSpeed + 1)
    			moveSpeed = distance;
    		if((targetCoord - originCoord) > (coord - originCoord))
    			return coord + moveSpeed;
    		else
    			return targetCoord;
    	}
    	else if(distance < 0) {
    		if(distance > -moveSpeed - 1)
    			moveSpeed = -distance;
    		if((targetCoord - originCoord) < (coord - originCoord))
    			return coord - moveSpeed;
    		else
    			return targetCoord;
    	}
    	return targetCoord;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    @Override
    protected void onImpact(RayTraceResult rayTraceResult) {}
    
    
    // ==================================================
 	//                      Speed
 	// ==================================================
    public void setSpeed(double speed) {
    	this.projectileSpeed = speed;
    }
    
    
    // ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getTexture() {
    	return null;
    }
}
