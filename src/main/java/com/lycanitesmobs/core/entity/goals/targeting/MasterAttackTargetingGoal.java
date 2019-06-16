package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

public class EntityAITargetMasterAttack extends Goal {
	// Targets:
	private EntityCreatureBase host;
	
	// Properties:
    private boolean tameTargeting = false;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetMasterAttack(EntityCreatureBase setHost) {
        host = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetMasterAttack setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }

    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	if(this.host.getAttackTarget() != null) {
    		if(!this.host.getAttackTarget().isAlive())
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
    		LivingEntity target = this.host.getMasterAttackTarget();
    		if(isTargetValid(target))
    			this.host.setAttackTarget(target);
    	}
    }

    
    // ==================================================
  	//                    Valid Target
  	// ==================================================
    private boolean isTargetValid(LivingEntity target) {
    	if(target == null) return false;
    	if(!target.isAlive()) return false;
		if(target == this.host) return false;
		if(!this.host.canAttackClass(target.getClass()))
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
