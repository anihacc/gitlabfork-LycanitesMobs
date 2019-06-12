package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class EntityAIFollowMaster extends EntityAIFollow {
	// Targets:
	EntityCreatureBase host;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIFollowMaster(EntityCreatureBase setHost) {
    	super(setHost);
        this.setMutexBits(1);
        this.host = setHost;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIFollowMaster setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIFollowMaster setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAIFollowMaster setStrayDistance(double setDist) {
    	this.strayDistance = setDist;
    	return this;
    }
    public EntityAIFollowMaster setLostDistance(double setDist) {
    	this.lostDistance = setDist;
    	return this;
    }
    
	
	// ==================================================
 	//                    Get Target
 	// ==================================================
    @Override
    public Entity getTarget() {
    	return this.host.getMasterTarget();
    }

	@Override
	public void setTarget(Entity entity) {
		if(entity instanceof LivingEntity)
			this.host.setMasterTarget((LivingEntity) entity);
	}
}
