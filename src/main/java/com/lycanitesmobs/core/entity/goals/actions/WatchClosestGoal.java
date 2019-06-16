package com.lycanitesmobs.core.entity.goals.actions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class WatchClosestGoal extends Goal {
    // Targets:
    private MobEntity host;
    protected Entity closestEntity;
    private final EntityPredicate searchPredicate = (new EntityPredicate()).func_221013_a(64.0D);

    // Properties
    private Class watchedClass = LivingEntity.class;
    private float maxDistance = 4.0F;
    private int lookTime;
    private int lookTimeMin = 40;
    private int lookTimeRange = 40;
    private float lookChance = 0.02F;
    
    // ==================================================
   	//                     Constructor
   	// ==================================================
    public WatchClosestGoal(MobEntity setHost) {
    	this.host = setHost;
        this.setMutexFlags(EnumSet.of(Flag.LOOK));
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
    public boolean shouldExecute() {
        if(this.host.getRNG().nextFloat() >= this.lookChance)
            return false;
        else {
            if(this.host.getAttackTarget() != null)
                this.closestEntity = this.host.getAttackTarget();
            if(this.watchedClass == PlayerEntity.class)
                this.closestEntity = this.host.getEntityWorld().getClosestPlayer(this.host.getPositionVec().getX(), this.host.getPositionVec().getY(), this.host.getPositionVec().getZ(), this.maxDistance, entity -> true);
            else
                this.host.world.func_217374_a(LivingEntity.class, this.searchPredicate, this.host, this.host.getBoundingBox().grow((double)this.maxDistance));

            return this.closestEntity != null;
        }
    }
    
    
    // ==================================================
   	//                 Continue Executing
   	// ==================================================
    public boolean shouldContinueExecuting() {
    	if(!this.closestEntity.isAlive())
    		return false;
    	if(this.host.getDistance(this.closestEntity) > (double)(this.maxDistance * this.maxDistance))
    		return false;
        return this.lookTime > 0;
    }
    
    
    // ==================================================
   	//                  Start Executing
   	// ==================================================
    public void startExecuting() {
        this.lookTime = lookTimeMin + this.host.getRNG().nextInt(lookTimeRange);
    }
    
    
    // ==================================================
   	//                      Reset
   	// ==================================================
    public void resetTask() {
        this.closestEntity = null;
    }
    
    
    // ==================================================
   	//                      Update
   	// ==================================================
    public void updateTask() {
        this.host.getLookHelper().setLookPosition(this.closestEntity.posX, this.closestEntity.posY + (double)this.closestEntity.getEyeHeight(), this.closestEntity.posZ, 10.0F, (float)this.host.getVerticalFaceSpeed());
        this.lookTime--;
    }
}