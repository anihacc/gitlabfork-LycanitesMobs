package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.LivingEntity;

public class EntityAITargetingFuse extends TargetingGoal {
	// Targets:
    private Class<IFusable> targetClass = IFusable.class;

    // Properties:
    private int targetChance = 0;
    protected boolean tameTargeting = false;

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetingFuse(EntityCreatureBase setHost) {
        super(setHost);
        this.setMutexBits(8);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetingFuse setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }

    public EntityAITargetingFuse setTargetClass(Class<IFusable> setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }

    public EntityAITargetingFuse setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }

    public EntityAITargetingFuse setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }

    public EntityAITargetingFuse setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }

    public EntityAITargetingFuse setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }

    public EntityAITargetingFuse setHelpCall(boolean setHelp) {
        this.callForHelp = setHelp;
        return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected LivingEntity getTarget() {
    	if(this.host instanceof IFusable) {
			return (LivingEntity) ((IFusable) this.host).getFusionTarget();
		}
    	return null;
    }
    @Override
    protected void setTarget(LivingEntity newTarget) {
		if(this.host instanceof IFusable && newTarget instanceof IFusable) {
			((IFusable)this.host).setFusionTarget((IFusable)newTarget);
		}
    }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(LivingEntity target) {
        // Target Class Check:
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass()))
            return false;

        // Own Class Check:
    	if(target.getClass() == this.host.getClass())
            return false;

    	// Fusable Check:
		if(!(this.host instanceof IFusable) || ((IFusable)this.host).getFusionClass((IFusable)target) == null)
			return false;

		// Owner Check:
		if(this.host instanceof EntityCreatureTameable && target instanceof EntityCreatureTameable) {
			if(((EntityCreatureTameable)this.host).getPlayerOwner() != ((EntityCreatureTameable)target).getPlayerOwner()) {
				return false;
			}
		}

    	return true;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
		if(!CreatureManager.getInstance().config.elementalFusion) {
			return false;
		}

		if (this.host.updateTick % 20 != 0) {
			return false;
		}
		if(this.host.isPetType("familiar")) {
			return false;
		}
		if(this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0) {
			return false;
		}

        // Check for other fusion target:
        LivingEntity fuseTarget = this.getTarget();
        if(fuseTarget != null && !this.isValidTarget(fuseTarget)) {
            return false;
        }

    	this.target = null;
        
        double distance = this.getTargetDistance();
        double heightDistance = 4.0D;
        if(this.host.useDirectNavigator())
            heightDistance = distance;
        this.target = this.getNewTarget(distance, heightDistance, distance);
        if(this.callForHelp)
            this.callNearbyForHelp();
        return this.target != null;
    }
}
