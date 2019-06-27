package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.LivingEntity;

public class CopyRiderAttackTargetGoal extends TargetingGoal {
	// Targets:
	private TameableCreatureEntity host;
	
	// Properties:
	private int lastAttackTime;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public CopyRiderAttackTargetGoal(TameableCreatureEntity setHost) {
    	super(setHost);
        this.host = setHost;
        this.checkSight = false;
    }

    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	if(!this.host.hasRiderTarget())
    		return false;
    	if(this.host.isSitting())
    		return false;
    	if(this.host.getRider() == null)
    		return false;
    	
    	this.target = this.host.getRider().getLastAttackedEntity();
    	if(this.target == null) {
    		return false;
    	}
    	if(lastAttackTime == this.host.getRider().getLastAttackedEntityTime())
    		return false;
    	return true;
    }

    
    // ==================================================
  	//                       Start
  	// ==================================================
    @Override
    public void startExecuting() {
    	if(isTargetValid(target)) {
			lastAttackTime = this.host.getRider().getLastAttackedEntityTime();
			super.startExecuting();
		}
    }
    
    
    // ==================================================
 	//                  Continue Executing
 	// ==================================================
    @Override
    public boolean shouldContinueExecuting() {
    	if(!this.host.hasRiderTarget())
    		return false;
        if(this.host.isSitting())
            return false;
        return super.shouldContinueExecuting();
    }
    
    
    // ==================================================
  	//                    Valid Target
  	// ==================================================
    private boolean isTargetValid(LivingEntity target) {
    	if(target == null) return false;
    	if(!target.isAlive()) return false;
		if(target == this.host) return false;
		if(!this.host.canAttack(target.getType())) return false;
    	return true;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected LivingEntity getTarget() { return this.host.getAttackTarget(); }
    @Override
    protected void setTarget(LivingEntity newTarget) { this.host.setAttackTarget(newTarget); }
}
