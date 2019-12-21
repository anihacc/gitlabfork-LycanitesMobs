package com.lycanitesmobs.core.entity.goals.targeting;

import com.google.common.base.Predicate;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.TargetSorterNearest;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class TargetingGoal extends EntityAIBase {
    // Targets:
    protected BaseCreatureEntity host;
    protected EntityLivingBase target;
    
    // Targeting:
    protected Predicate<EntityLivingBase> targetSelector;
    protected Predicate<EntityLivingBase> allySelector;
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
        if(!this.getTarget().isEntityAlive())
            return false;
        if(this.shouldStopTargeting(this.getTarget())) {
            return false;
        }

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

    public boolean shouldStopTargeting(EntityLivingBase target) {
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
    protected EntityLivingBase getTarget() { return null; }
    protected void setTarget(EntityLivingBase newTarget) {}


    // ==================================================
    //                  Get New Target
    // ==================================================
    public EntityLivingBase getNewTarget(double rangeX, double rangeY, double rangeZ) {
        EntityLivingBase newTarget = null;
        try {
            List<EntityLivingBase> possibleTargets = this.getPossibleTargets(EntityLivingBase.class, rangeX, rangeY, rangeZ);

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
    public <T extends EntityLivingBase> List<T> getPossibleTargets(Class <? extends T > clazz, double rangeX, double rangeY, double rangeZ) {
        return this.host.getEntityWorld().getEntitiesWithinAABB(clazz, this.host.getEntityBoundingBox().grow(rangeX, rangeY, rangeZ), this.targetSelector);
    }
    
    
    // ==================================================
 	//                 Get Target Distance
 	// ==================================================
    protected double getTargetDistance() {
        if(this.targetingRange > 0)
            return this.targetingRange;
    	IAttributeInstance attributeInstance = this.host.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        return attributeInstance.getAttributeValue();
    }


    // ==================================================
    //              Call Nearby For Help
    // ==================================================
    public void callNearbyForHelp() {
        if(this.allySelector == null || this.target == null)
            return;
        try {
            double targetDistance = this.getTargetDistance();
            List allies = this.host.getEntityWorld().getEntitiesWithinAABB(BaseCreatureEntity.class, this.host.getEntityBoundingBox().grow(targetDistance, 4.0D, targetDistance), this.allySelector);

            Iterator possibleAllies = allies.iterator();
            while(possibleAllies.hasNext()) {
                EntityLiving possibleAlly = (EntityLiving)possibleAllies.next();
                if(!possibleAlly.isOnSameTeam(this.target) && possibleAlly.canAttackClass(this.target.getClass())) {
                    if (possibleAlly instanceof BaseCreatureEntity) {
                        BaseCreatureEntity possibleCreatureAlly = (BaseCreatureEntity) possibleAlly;
                        if (possibleCreatureAlly.getAttackTarget() == null && possibleCreatureAlly.canAttackEntity(this.target) && possibleCreatureAlly.shouldCreatureGroupRevenge(this.target))
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
    protected boolean isEntityTargetable(EntityLivingBase checkTarget, boolean targetCreative) {
        if(checkTarget == null)
            return false;
        if(checkTarget == this.host)
            return false;
        if(!checkTarget.isEntityAlive())
            return false;

        // Player Checks:
		if(checkTarget instanceof EntityPlayer) {
			if(!targetCreative && ((EntityPlayer)checkTarget).isCreative())
				return false;
			if(((EntityPlayer) checkTarget).isSpectator())
				return false;
		}
        
        // Valid Check:
        if(!this.isValidTarget(checkTarget))
            return false;
        
        // Home Check:
        if(!this.host.positionNearHome(MathHelper.floor(checkTarget.posX), MathHelper.floor(checkTarget.posY), MathHelper.floor(checkTarget.posZ)))
            return false;
        
        // Sight Check:
        if(this.shouldCheckSight() && !checkTarget.isPotionActive(MobEffects.GLOWING) && !this.host.getEntitySenses().canSee(checkTarget)) // Glowing
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
	protected boolean isValidTarget(EntityLivingBase target) {
    	return true;
    }

	/**
	 * Returns if the provided target should be considered an ally when calling for help, etc.
	 * @param checkTarget The target entity to check.
	 * @return True if the target is to be considered an ally to help.
	 */
    protected boolean isAllyTarget(EntityLivingBase checkTarget) {
        if(checkTarget == null)
            return false;
        if(checkTarget == this.host)
            return false;
        if(!checkTarget.isEntityAlive())
            return false;

		// Player Check:
		if(checkTarget instanceof EntityPlayer)
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
    private boolean isNearby(EntityLivingBase target) {
        this.targetSearchDelay = 10 + this.host.getRNG().nextInt(5);
        Path path = this.host.getNavigator().getPathToEntityLiving(target);

        if(path == null)
            return false;
        else {
            PathPoint pathpoint = path.getFinalPathPoint();
            if(pathpoint == null)
                return false;
            else {
                int i = pathpoint.x - MathHelper.floor(target.posX);
                int j = pathpoint.z - MathHelper.floor(target.posZ);
                return (double)(i * i + j * j) <= 2.25D;
            }
        }
    }
}
