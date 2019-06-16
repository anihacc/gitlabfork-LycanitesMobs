package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.Entity;

public class FollowFuseGoal extends FollowGoal {
	// Targets:
	EntityCreatureBase host;

	// Fusion:
	double fuseRange = 2;

	// ==================================================
 	//                    Constructor
 	// ==================================================
    public FollowFuseGoal(EntityCreatureBase setHost) {
    	super(setHost);
        this.host = setHost;
        this.strayDistance = 0;
    }


	// ==================================================
	//                  Should Execute
	// ==================================================
	@Override
	public boolean shouldExecute() {
    	/*if(this.host instanceof EntityCreatureTameable && ((EntityCreatureTameable)this.host).isTamed()) {
    		return false;
		}*/
    	if(this.host.isBoss()) {
    		return false;
		}
		return super.shouldExecute();
	}
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public FollowFuseGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public FollowFuseGoal setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public FollowFuseGoal setStrayDistance(double setDist) {
    	this.strayDistance = setDist;
    	return this;
    }
    public FollowFuseGoal setLostDistance(double setDist) {
    	this.lostDistance = setDist;
    	return this;
    }
	public FollowFuseGoal setFuseRange(double setDist) {
		this.fuseRange = setDist;
		return this;
	}
    
	
	// ==================================================
 	//                       Target
 	// ==================================================
    @Override
    public Entity getTarget() {
    	if(this.host instanceof IFusable) {
    		return (Entity)((IFusable)this.host).getFusionTarget();
		}
    	return null;
    }

	@Override
	public void setTarget(Entity entity) {
		if(this.host instanceof IFusable && entity instanceof IFusable) {
			((IFusable)this.host).setFusionTarget((IFusable)entity);
		}
	}

	@Override
	public void onTargetDistance(double distance, Entity followTarget) {
		if(distance > this.fuseRange)
			return;

		// Do Fusion:
		if(this.host instanceof IFusable && followTarget instanceof IFusable) {
			Class fusionClass = ((IFusable)this.host).getFusionClass((IFusable)followTarget);
			if(fusionClass == null) {
				return;
			}
			this.host.transform(fusionClass, followTarget, true);
		}
	}
}