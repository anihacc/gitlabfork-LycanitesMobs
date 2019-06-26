package com.lycanitesmobs.core.entity.goals.targeting;

import com.google.common.base.Predicate;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.Targeting;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;

import java.util.*;

public class AttackTargetingGoal extends TargetingGoal {
	// Targets:
    public Class targetClass = LivingEntity.class;
    private List<Class> targetClasses = new ArrayList<>();
    
    // Properties:
    private int targetChance = 0;
    protected boolean tameTargeting = false;
    private int allySize = 0;
    private int enemySize = 0;

    // Temp Groups:
	private static List<Class> VILLAGER_CLASSES = Arrays.asList(VillagerEntity.class, PillagerEntity.class);
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public AttackTargetingGoal(BaseCreatureEntity setHost) {
        super(setHost);
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public AttackTargetingGoal setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }
    
    public AttackTargetingGoal setCheckSight(boolean bool) {
    	this.checkSight = bool;
    	return this;
    }
    
    public AttackTargetingGoal setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	if(setTargetClass == VillagerEntity.class) {
    		this.setTargetClasses(VILLAGER_CLASSES);
		}
    	return this;
    }
    
    public AttackTargetingGoal setTargetClasses(List<Class> classList) {
    	this.targetClasses = classList;
    	return this;
    }
    
    public AttackTargetingGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }

    public AttackTargetingGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }

    public AttackTargetingGoal setRange(double range) {
        this.targetingRange = range;
        return this;
    }

    public AttackTargetingGoal setHelpCall(boolean setHelp) {
        this.callForHelp = setHelp;
        return this;
    }
    
    public AttackTargetingGoal setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }
    
    /** If both values are above 0 then this mob will consider the size of the enemy pack and it's pack before attacking.
     * setAllySize How many of this mob vs the enemy pack.
     * setEnemySize How many of the enemy vs this mobs size.
     * For example allySize of this mob will attack up to enemySize of the enemy at once.
     * Setting either value at or below 0 will disable this functionality.
    **/
    public AttackTargetingGoal setPackHuntingScale(int setAllySize, int setEnemySize) {
    	this.allySize = setAllySize;
    	this.enemySize = setEnemySize;
    	return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected LivingEntity getTarget() { return this.host.getAttackTarget(); }
    @Override
    protected void setTarget(LivingEntity newTarget) { this.host.setAttackTarget(newTarget); }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(LivingEntity target) {
    	// Target Class List:
		if(!this.targetClasses.isEmpty()) {
			boolean foundTarget = false;
			for(Class targetClass : this.targetClasses) {
				if(targetClass.isAssignableFrom(target.getClass())) {
					foundTarget = true;
					break;
				}
			}
			if(!foundTarget)
				return false;
		}

        // Target Class Check:
        else if(this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass()))
            return false;

    	// Own Class Check:
    	if(this.targetClass != this.host.getClass() && target.getClass() == this.host.getClass())
            return false;

		// Peaceful Difficulty Check:
		if(this.host.getEntityWorld().getDifficulty() == Difficulty.PEACEFUL && target instanceof PlayerEntity)
			return false;

		// Tamed Targeting Check:
		if(!this.tameTargeting && this.host.isTamed())
			return false;

        // Predator Animal/Alpha Check:
        if(this.targetClass == IGroupAnimal.class || this.targetClass == IGroupAlpha.class) {
            if(target instanceof IGroupPredator)
                return false; // Do not attack predators when targeting animals or alphas (some predators could also count as alphas, etc).
        }
    	
    	// Type Check:
    	if(!this.host.canAttack(target.getType()))
            return false;

        // Entity Check:
		if(!this.host.canAttack(target)) {
			return false;
		}

		// Mod Interaction Check:
		if(!Targeting.isValidTarget(this.host, target)) {
			return false;
		}
        
        // Pack Size Check:
        if(this.allySize > 0 && this.enemySize > 0) {
            try {
                double hostPackRange = 32D;
                double hostPackSize = this.host.getEntityWorld().getEntitiesWithinAABB(this.host.getClass(), this.host.getBoundingBox().grow(hostPackRange, hostPackRange, hostPackRange)).size();
                double hostPackScale = hostPackSize / this.allySize;

                double targetPackRange = 64D;
                double targetPackSize = target.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, target.getBoundingBox().grow(targetPackRange, targetPackRange, targetPackRange), (Predicate<LivingEntity>) entity -> entity.getClass().isAssignableFrom(AttackTargetingGoal.this.targetClass)).size();
                double targetPackScale = targetPackSize / this.enemySize;

                if (hostPackScale < targetPackScale)
                    return false;
            }
            catch (Exception e) {
                LycanitesMobs.logWarning("", "An exception occurred when assessing pack sizes, this has been skipped to prevent a crash.");
                e.printStackTrace();
            }
        }
        
    	return true;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
		if(!this.host.isAggressive() || this.host.hasFixateTarget()) {
			return false;
		}

    	if(this.targetClass == PlayerEntity.class) {
			if (this.host.updateTick % 10 != 0) {
				return false;
			}
		}
		else {
			if (this.host.updateTick % 40 != 0) {
				return false;
			}
		}
		if(this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0) {
			return false;
		}


		this.target = null;
        
        double distance = this.getTargetDistance();
        double heightDistance = 4.0D + this.host.getHeight();
        if(this.host.useDirectNavigator())
            heightDistance = distance;
        this.target = this.getNewTarget(distance, heightDistance, distance);
        if(this.callForHelp)
            this.callNearbyForHelp();

        return this.target != null;
    }


	// ==================================================
	//                  Get New Target
	// ==================================================
	@Override
	public LivingEntity getNewTarget(double rangeX, double rangeY, double rangeZ) {
		// Faster Player Targeting:
		if(this.targetClass == PlayerEntity.class) {
			LivingEntity newTarget = null;
			try {
				List<? extends PlayerEntity> players = this.host.getEntityWorld().getPlayers();
				if (players.isEmpty())
					return null;
				List<PlayerEntity> possibleTargets = new ArrayList<>();
				for(PlayerEntity player : players) {
					if(this.isValidTarget(player))
						possibleTargets.add(player);
				}
				if (possibleTargets.isEmpty())
					return null;
				Collections.sort(possibleTargets, this.nearestSorter);
				newTarget = possibleTargets.get(0);
			}
			catch (Exception e) {
				LycanitesMobs.logWarning("", "An exception occurred when player target selecting, this has been skipped to prevent a crash.");
				e.printStackTrace();
			}
			return newTarget;
		}

		return super.getNewTarget(rangeX, rangeY, rangeZ);
	}
}
