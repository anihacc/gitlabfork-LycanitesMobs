package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public class CopyOwnerAttackTargetGoal extends TargetingGoal {
	// Targets:
	private TameableCreatureEntity host;
	
	// Properties:
	private int lastAttackTime;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public CopyOwnerAttackTargetGoal(TameableCreatureEntity setHost) {
    	super(setHost);
        this.host = setHost;
        this.checkSight = false;
    }

    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	if(!this.host.isTamed())
    		return false;
    	if(this.host.isSitting())
    		return false;
		if(!this.host.isAssisting())
			return false;
    	if(this.host.getOwner() == null)
    		return false;

        if (!(this.host.getOwner() instanceof EntityLiving))
            return false;
        EntityLiving owner = (EntityLiving)this.host.getOwner();
    	this.target = owner.getLastAttackedEntity();
    	if(this.target == null) {
    		return false;
    	}
    	if(lastAttackTime == owner.getLastAttackedEntityTime())
    		return false;
    	return true;
    }

    
    // ==================================================
  	//                       Start
  	// ==================================================
    @Override
    public void startExecuting() {
    	if(this.isTargetValid(target)) {
			lastAttackTime = ((EntityLiving)this.host.getOwner()).getLastAttackedEntityTime();
			super.startExecuting();
		}
    }
    
    
    // ==================================================
 	//                  Continue Executing
 	// ==================================================
    @Override
    public boolean shouldContinueExecuting() {
        if(this.host.isSitting())
            return false;
        return super.shouldContinueExecuting();
    }
    
    
    // ==================================================
  	//                    Valid Target
  	// ==================================================
    private boolean isTargetValid(EntityLivingBase target) {
    	if(target == null)
            return false;
    	if(!target.isEntityAlive())
            return false;
		if(target == this.host)
            return false;
		if(!this.host.canAttackClass(target.getClass()))
            return false;
		if(!this.host.canAttackEntity(target))
            return false;
    	return true;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected EntityLivingBase getTarget() { return this.host.getAttackTarget(); }
    @Override
    protected void setTarget(EntityLivingBase newTarget) { this.host.setAttackTarget(newTarget); }
}
