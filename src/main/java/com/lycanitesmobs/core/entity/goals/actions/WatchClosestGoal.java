package com.lycanitesmobs.core.entity.goals.actions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class WatchClosestGoal extends EntityAIBase {
    // Targets:
    private EntityLiving host;
    protected Entity closestEntity;

    // Properties
    private Class watchedClass = EntityLivingBase.class;
    private float maxDistance = 4.0F;
    private int lookTime;
    private int lookTimeMin = 40;
    private int lookTimeRange = 40;
    private float lookChance = 0.02F;
    
    // ==================================================
   	//                     Constructor
   	// ==================================================
    public WatchClosestGoal(EntityLiving setHost) {
    	this.host = setHost;
        this.setMutexBits(2);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public WatchClosestGoal setTargetClass(Class setTarget) {
    	this.watchedClass = setTarget;
    	return this;
    }
    public WatchClosestGoal setMaxDistance(float setDist) {
    	this.maxDistance = setDist;
    	return this;
    }
    public WatchClosestGoal setlookChance(float setChance) {
    	this.lookChance = setChance;
    	return this;
    }
    
    
    // ==================================================
   	//                   Should Execute
   	// ==================================================
	@Override
    public boolean shouldExecute() {
		if(this.host.getRNG().nextFloat() >= this.lookChance)
			return false;
		else {
			if(this.host.getAttackTarget() != null)
				this.closestEntity = this.host.getAttackTarget();
			if(this.watchedClass == EntityPlayer.class)
				this.closestEntity = this.host.getEntityWorld().getClosestPlayerToEntity(this.host, (double)this.maxDistance);
			else
				this.closestEntity = this.host.getEntityWorld().findNearestEntityWithinAABB(this.watchedClass, this.host.getEntityBoundingBox().grow((double)this.maxDistance, 3.0D, (double)this.maxDistance), this.host);

			return this.closestEntity != null;
		}
    }
    
    
    // ==================================================
   	//                 Continue Executing
   	// ==================================================
	@Override
    public boolean shouldContinueExecuting() {
    	if(!this.closestEntity.isEntityAlive())
    		return false;
    	if(this.host.getDistance(this.closestEntity) > (double)(this.maxDistance * this.maxDistance))
    		return false;
        return this.lookTime > 0;
    }
    
    
    // ==================================================
   	//                  Start Executing
   	// ==================================================
	@Override
    public void startExecuting() {
        this.lookTime = lookTimeMin + this.host.getRNG().nextInt(lookTimeRange);
    }
    
    
    // ==================================================
   	//                      Reset
   	// ==================================================
	@Override
    public void resetTask() {
        this.closestEntity = null;
    }
    
    
    // ==================================================
   	//                      Update
   	// ==================================================
	@Override
    public void updateTask() {
        this.host.getLookHelper().setLookPosition(this.closestEntity.posX, this.closestEntity.posY + (double)this.closestEntity.getEyeHeight(), this.closestEntity.posZ, 10.0F, (float)this.host.getVerticalFaceSpeed());
        this.lookTime--;
    }
}