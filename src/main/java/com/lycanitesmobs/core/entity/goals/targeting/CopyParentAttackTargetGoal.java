package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class CopyParentAttackTargetGoal extends EntityAIBase {
	// Targets:
	private BaseCreatureEntity host;
	
	// Properties:
    private boolean tameTargeting = true;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public CopyParentAttackTargetGoal(BaseCreatureEntity setHost) {
        host = setHost;
		this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public CopyParentAttackTargetGoal setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }

    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	if(this.host.getAttackTarget() != null) {
    		if(!this.host.getAttackTarget().isEntityAlive())
    			return false;
    	}
    	if(this.host.getParentAttackTarget() == null)
    		return false;
    	return true;
    }

    
    // ==================================================
  	//                       Update
  	// ==================================================
    @Override
    public void updateTask() {
    	if(this.host.getAttackTarget() == null) {
			EntityLivingBase target = this.host.getParentAttackTarget();
    		if(this.isTargetValid(target))
    			this.host.setAttackTarget(target);
    	}
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
 	//                       Reset
 	// ==================================================
    @Override
    public void resetTask() {
        this.host.setAttackTarget(null);
    }
}
