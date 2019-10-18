package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class CopyMasterAttackTargetGoal extends EntityAIBase {
	// Targets:
	private BaseCreatureEntity host;
	
	// Properties:
    private boolean tameTargeting = false;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public CopyMasterAttackTargetGoal(BaseCreatureEntity setHost) {
        host = setHost;
		this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public CopyMasterAttackTargetGoal setTameTargetting(boolean setTargetting) {
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
    	if(this.host.getMasterAttackTarget() == null)
    		return false;
    	return true;
    }

    
    // ==================================================
  	//                       Update
  	// ==================================================
    @Override
    public void updateTask() {
    	if(this.host.getAttackTarget() == null) {
    		EntityLivingBase target = this.host.getMasterAttackTarget();
    		if(isTargetValid(target))
    			this.host.setAttackTarget(target);
    	}
    }

    
    // ==================================================
  	//                    Valid Target
  	// ==================================================
	private boolean isTargetValid(EntityLivingBase target) {
    	if(target == null) return false;
    	if(!target.isEntityAlive()) return false;
		if(target == this.host) return false;
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
