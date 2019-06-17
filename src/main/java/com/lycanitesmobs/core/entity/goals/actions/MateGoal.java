package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class MateGoal extends Goal {
	// Targets:
    private EntityCreatureAgeable host;
    private EntityCreatureAgeable partner;
    
    // Properties:
    private double speed = 1.0D;
    private Class targetClass;
    private int mateTime;
    private int mateTimeMax = 60;
    private double mateDistance = 9.0D;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public MateGoal(EntityCreatureAgeable setHost) {
        this.host = setHost;
        this.targetClass = this.host.getClass();
		this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public MateGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public MateGoal setMateDistance(double setDouble) {
    	this.mateDistance = setDouble * setDouble;
    	return this;
    }
    public MateGoal setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public MateGoal setMateTime(int setTime) {
    	this.mateTimeMax = setTime;
    	return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
	@Override
    public boolean shouldExecute() {
        if(!this.host.isInLove())
            return false;
        this.partner = this.getPartner();
        return this.partner != null;
    }
    
    
    // ==================================================
  	//                Continue Executing
  	// ==================================================
	@Override
    public boolean shouldContinueExecuting() {
        return this.partner != null && this.partner.isAlive() && this.partner.isInLove() && this.mateTime < mateTimeMax;
    }
    
    
    // ==================================================
  	//                      Reset
  	// ==================================================
	@Override
    public void resetTask() {
        this.partner = null;
        this.mateTime = 0;
    }
    
    
    // ==================================================
  	//                      Update
  	// ==================================================
	@Override
    public void tick() {
        this.host.getLookHelper().setLookPositionWithEntity(this.partner, 10.0F, (float)this.host.getVerticalFaceSpeed());
        if(!this.host.useDirectNavigator())
        	this.host.getNavigator().tryMoveToEntityLiving(this.partner, this.speed);
        else
        	this.host.directNavigator.setTargetPosition(new BlockPos((int)this.partner.posX, (int)this.partner.posY, (int)this.partner.posZ), speed);
        if(this.host.getDistance(this.partner) < this.mateDistance)
	        ++this.mateTime;
	        if(this.mateTime >= mateTimeMax)
	            this.host.procreate(this.partner);
    }
    
    
    // ==================================================
  	//                    Get Partner
  	// ==================================================
    private EntityCreatureAgeable getPartner() {
        float distance = 8.0F;
        List possibleMates = this.host.getEntityWorld().getEntitiesWithinAABB(this.targetClass, this.host.getBoundingBox().grow((double)distance, (double)distance, (double)distance));
        double closestDistance = Double.MAX_VALUE;
        EntityCreatureAgeable newMate = null;
        Iterator possibleMate = possibleMates.iterator();
        
        while(possibleMate.hasNext())  {
        	LivingEntity nextEntity = (LivingEntity)possibleMate.next();
        	if(nextEntity instanceof EntityCreatureAgeable) {
	        	EntityCreatureAgeable testMate = (EntityCreatureAgeable)nextEntity;
	            if(this.host.canBreedWith(testMate) && this.host.getDistance(testMate) < closestDistance) {
	            	newMate = testMate;
	            	closestDistance = this.host.getDistance(testMate);
	            }
        	}
        }
        return newMate;
    }
}
