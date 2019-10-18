package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.EntityLivingBase;

public class FindFuseTargetGoal extends TargetingGoal {
	// Targets:
    private Class<IFusable> targetClass = IFusable.class;

    // Properties:
    private int targetChance = 0;
    protected boolean tameTargeting = false;

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public FindFuseTargetGoal(BaseCreatureEntity setHost) {
        super(setHost);
		this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public FindFuseTargetGoal setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }

    public FindFuseTargetGoal setTargetClass(Class<IFusable> setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }

    public FindFuseTargetGoal setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }

    public FindFuseTargetGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }

    public FindFuseTargetGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }

    public FindFuseTargetGoal setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }

    public FindFuseTargetGoal setHelpCall(boolean setHelp) {
        this.callForHelp = setHelp;
        return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected EntityLivingBase getTarget() {
    	if(this.host instanceof IFusable) {
			return (EntityLivingBase) ((IFusable) this.host).getFusionTarget();
		}
    	return null;
    }
    @Override
    protected void setTarget(EntityLivingBase newTarget) {
		if(this.host instanceof IFusable && newTarget instanceof IFusable) {
			((IFusable)this.host).setFusionTarget((IFusable)newTarget);
		}
    }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(EntityLivingBase target) {
        // Target Class Check:
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass()))
            return false;

        // Own Class Check:
    	if(target.getClass() == this.host.getClass())
            return false;

    	// Fusable Check:
		if(!(this.host instanceof IFusable) || ((IFusable)this.host).getFusionClass((IFusable)target) == null)
			return false;

		// Subspecies Check:
		if(this.host.isRareSubspecies()) {
			if(!(target instanceof BaseCreatureEntity)) {
				return false;
			}
			if(!((BaseCreatureEntity)target).isRareSubspecies()) {
				return false;
			}
		}

		// Owner Check:
		if(this.host instanceof TameableCreatureEntity && target instanceof TameableCreatureEntity) {
			if(((TameableCreatureEntity)this.host).getPlayerOwner() != ((TameableCreatureEntity)target).getPlayerOwner()) {
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
		EntityLivingBase fuseTarget = this.getTarget();
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
