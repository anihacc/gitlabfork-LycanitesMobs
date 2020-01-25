package com.lycanitesmobs.core.entity.goals.targeting;

import com.google.common.base.Predicate;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.TargetSorterNearest;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class TargetingGoal extends Goal {
    // Targets:
    protected BaseCreatureEntity host;
    protected LivingEntity target;
    
    // Targeting:
    protected Predicate<LivingEntity> targetSelector;
    protected Predicate<LivingEntity> allySelector;
    protected TargetSorterNearest nearestSorter;

    protected boolean checkSight = true;
    protected boolean nearbyOnly = false;
    protected boolean callForHelp = false;
    private int cantSeeTime;
    protected int cantSeeTimeMax = 60;
    protected double targetingRange = 0;
    
    private int targetSearchStatus;
    private int targetSearchDelay;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public TargetingGoal(BaseCreatureEntity setHost) {
        this.host = setHost;

        this.targetSelector = entity -> {
            double targetDistance = TargetingGoal.this.getTargetDistance();
            if(this.shouldCheckSight() && !entity.isGlowing() && !this.host.canEntityBeSeen(entity)) {
                return false;
            }
            return !((double) entity.getDistance(TargetingGoal.this.host) > targetDistance) && TargetingGoal.this.isEntityTargetable(entity, false);
        };

        this.allySelector = entity -> {
            double targetDistance = TargetingGoal.this.getTargetDistance();
			if(this.shouldCheckSight() && !entity.isGlowing() && !this.host.canEntityBeSeen(entity)) {
				return false;
			}
            return !((double) entity.getDistance(TargetingGoal.this.host) > targetDistance) && TargetingGoal.this.isAllyTarget(entity);
        };

        this.nearestSorter = new TargetSorterNearest(setHost);
    }
    
    
    // ==================================================
 	//                  Start Executing
 	// ==================================================
    @Override
    public void startExecuting() {
    	this.setTarget(this.target);
        this.targetSearchStatus = 0;
        this.targetSearchDelay = 0;
        this.cantSeeTime = 0;
    }
    
    
    // ==================================================
 	//                  Continue Executing
 	// ==================================================
    @Override
    public boolean shouldContinueExecuting() {
        if(this.getTarget() == null)
            return false;
        if(!this.getTarget().isAlive())
            return false;
        if(this.shouldStopTargeting(this.getTarget()))
            return false;

        // Target Out of Range:
        double distance = this.getTargetDistance() + 2;
        if(this.host.getDistance(this.getTarget()) > distance)
            return false;
        
        if(this.shouldCheckSight())
            if(this.host.getEntitySenses().canSee(this.getTarget()))
                this.cantSeeTime = 0;
            else if(++this.cantSeeTime > this.cantSeeTimeMax)
                return false;
        
        return true;
    }

    public boolean shouldStopTargeting(LivingEntity target) {
        return false;
    }
    
    
    // ==================================================
 	//                      Reset
 	// ==================================================
    @Override
    public void resetTask() {
        this.setTarget(null);
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    protected LivingEntity getTarget() { return null; }
    protected void setTarget(LivingEntity newTarget) {}


    // ==================================================
    //                  Get New Target
    // ==================================================
    public LivingEntity getNewTarget(double rangeX, double rangeY, double rangeZ) {
        LivingEntity newTarget = null;
        try {
            List<LivingEntity> possibleTargets = this.getPossibleTargets(LivingEntity.class, rangeX, rangeY, rangeZ);

            if (possibleTargets.isEmpty())
                return null;
            Collections.sort(possibleTargets, this.nearestSorter);
            newTarget = possibleTargets.get(0);
        }
        catch (Exception e) {
            LycanitesMobs.logWarning("", "An exception occurred when target selecting, this has been skipped to prevent a crash.");
            e.printStackTrace();
        }
        return newTarget;
    }


    // ==================================================
    //               Get Possible Targets
    // ==================================================
    public <T extends LivingEntity> List<T> getPossibleTargets(Class <? extends T > clazz, double rangeX, double rangeY, double rangeZ) {
        return this.host.getEntityWorld().getEntitiesWithinAABB(clazz, this.host.getBoundingBox().grow(rangeX, rangeY, rangeZ), this.targetSelector);
    }
    
    
    // ==================================================
 	//                 Get Target Distance
 	// ==================================================
    protected double getTargetDistance() {
        if(this.targetingRange > 0)
            return this.targetingRange;
    	IAttributeInstance attributeInstance = this.host.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        return attributeInstance.getValue();
    }


    // ==================================================
    //              Call Nearby For Help
    // ==================================================
    public void callNearbyForHelp() {
        if(this.allySelector == null || this.target == null)
            return;
        try {
            double targetDistance = this.getTargetDistance();
            List allies = this.host.getEntityWorld().getEntitiesWithinAABB(BaseCreatureEntity.class, this.host.getBoundingBox().grow(targetDistance, 4.0D, targetDistance), this.allySelector);
            Iterator possibleAllies = allies.iterator();

            while(possibleAllies.hasNext()) {
                LivingEntity possibleAlly = (LivingEntity)possibleAllies.next();
                if(!possibleAlly.isOnSameTeam(this.target) && possibleAlly.canAttack(this.target.getType())) {
                    if (possibleAlly instanceof BaseCreatureEntity) {
                        BaseCreatureEntity possibleCreatureAlly = (BaseCreatureEntity) possibleAlly;
                        if (possibleCreatureAlly.getAttackTarget() == null && possibleCreatureAlly.canAttack(this.target) && possibleCreatureAlly.shouldCreatureGroupRevenge(this.target))
                            possibleCreatureAlly.setRevengeTarget(this.target);
                    }
                    else {
                        if (possibleAlly.getRevengeTarget() == null)
                            possibleAlly.setRevengeTarget(this.target);
                    }
                }
            }
        }
        catch (Exception e) {
            LycanitesMobs.logWarning("", "An exception occurred when calling for help, this has been skipped to prevent a crash.");
            e.printStackTrace();
        }
    }
    
    
    // ==================================================
 	//                  Target Checking
 	// ==================================================
    /**
     * Performs all checks to see if the entity can be targeted at all. Override isValidTarget for AI specific checks.
     * @param checkTarget The target entity to check.
     * @param targetCreative If true, creative players can pass this check.
     * @return
     */
    protected boolean isEntityTargetable(LivingEntity checkTarget, boolean targetCreative) {
        if(checkTarget == null)
            return false;
        if(checkTarget == this.host)
            return false;
        if(!checkTarget.isAlive())
            return false;

        // Player Checks:
		if(checkTarget instanceof PlayerEntity) {
			if(!targetCreative && ((PlayerEntity)checkTarget).isCreative())
				return false;
			if(checkTarget.isSpectator())
				return false;
		}
        
        // Valid Check:
        if(!this.isValidTarget(checkTarget)) {
            return false;
        }
        
        // Home Check:
        if(!this.host.positionNearHome(MathHelper.floor(checkTarget.getPositionVec().getX()), MathHelper.floor(checkTarget.getPositionVec().getY()), MathHelper.floor(checkTarget.getPositionVec().getZ())))
            return false;
        
        // Sight Check:
        if(this.shouldCheckSight() && !checkTarget.isPotionActive(Effects.GLOWING) && !this.host.getEntitySenses().canSee(checkTarget)) // Glowing
            return false;
        
        // Nearby Check:
        if(this.nearbyOnly) {
            if(--this.targetSearchDelay <= 0)
                this.targetSearchStatus = 0;
            if(this.targetSearchStatus == 0)
                this.targetSearchStatus = this.isNearby(checkTarget) ? 1 : 2;
            if(this.targetSearchStatus == 2)
                return false;
        }
        
        return true;
    }

    /**
     * Returns if this targeting goal should check for clear sight.
     * @return True if sight should be checked.
     */
    protected boolean shouldCheckSight() {
        return this.checkSight;
    }

	/**
	 * Checks if the target entity is a vlid target for this targeting AI. This should be overridden for AI specific checks..
	 * @param target The target entity to check.
	 * @return True if the entity can be targeted by this AI.
	 */
	protected boolean isValidTarget(LivingEntity target) {
    	return true;
    }

	/**
	 * Returns if the provided target should be considered an ally when calling for help, etc.
	 * @param checkTarget The target entity to check.
	 * @return True if the target is to be considered an ally to help.
	 */
    protected boolean isAllyTarget(LivingEntity checkTarget) {
        if(checkTarget == null)
            return false;
        if(checkTarget == this.host)
            return false;
        if(!checkTarget.isAlive())
            return false;

		// Player Check:
		if(checkTarget instanceof PlayerEntity)
			return false;

        // Protective:
        if(checkTarget instanceof BaseCreatureEntity) {
            if(!((BaseCreatureEntity)checkTarget).isProtective(this.host))
                return false;
        }
        else if(checkTarget.getClass() != this.host.getClass()) {
            return false;
        }

        // Sight Check:
        return !this.shouldCheckSight() || this.host.getEntitySenses().canSee(checkTarget);
    }
    
    
    // ==================================================
 	//                     Is Nearby
 	// ==================================================
    private boolean isNearby(LivingEntity target) {
        this.targetSearchDelay = 10 + this.host.getRNG().nextInt(5);
        Path path = this.host.getNavigator().getPathToEntity(target, 0);

        if(path == null)
            return false;
        else {
            PathPoint pathpoint = path.getFinalPathPoint();
            if(pathpoint == null)
                return false;
            else {
                int i = pathpoint.x - MathHelper.floor(target.getPositionVec().getX());
                int j = pathpoint.z - MathHelper.floor(target.getPositionVec().getZ());
                return (double)(i * i + j * j) <= 2.25D;
            }
        }
    }
}
