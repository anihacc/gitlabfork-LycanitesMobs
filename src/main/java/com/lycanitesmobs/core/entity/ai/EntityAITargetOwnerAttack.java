package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import net.minecraft.entity.LivingEntity;

public class EntityAITargetOwnerAttack extends EntityAITarget {
	// Targets:
	private EntityCreatureTameable host;
	
	// Properties:
	private int lastAttackTime;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetOwnerAttack(EntityCreatureTameable setHost) {
    	super(setHost);
        this.host = setHost;
        this.checkSight = false;
        this.setMutexBits(1);
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

        if (!(this.host.getOwner() instanceof LivingEntity))
            return false;
        LivingEntity owner = (LivingEntity)this.host.getOwner();
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
			lastAttackTime = ((LivingEntity)this.host.getOwner()).getLastAttackedEntityTime();
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
    private boolean isTargetValid(LivingEntity target) {
    	if(target == null)
            return false;
    	if(!target.isAlive())
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
    protected LivingEntity getTarget() { return this.host.getAttackTarget(); }
    @Override
    protected void setTarget(LivingEntity newTarget) { this.host.setAttackTarget(newTarget); }
}
