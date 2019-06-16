package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MasterAttackTargetingGoal extends Goal {
	// Targets:
	private EntityCreatureBase host;
	
	// Properties:
    private boolean tameTargeting = false;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public MasterAttackTargetingGoal(EntityCreatureBase setHost) {
        host = setHost;
		this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public MasterAttackTargetingGoal setTameTargetting(boolean setTargetting) {
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
    public void tick() {
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
		if(!this.host.canAttack(target.getType()))
            return false;
		if(!this.host.canAttack(target))
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
