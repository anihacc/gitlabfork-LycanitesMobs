package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EntityEttin extends AgeableCreatureEntity implements IMob {
	public boolean griefing = true;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEttin(EntityType<? extends EntityEttin> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        
        this.solidCollision = true;
        this.setupMob();
        
        // Stats:
        this.attackPhaseMax = 2;
    }

    @Override
    protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));

		if(this.getNavigator() instanceof GroundPathNavigator) {
			GroundPathNavigator pathNavigateGround = (GroundPathNavigator)this.getNavigator();
			pathNavigateGround.setBreakDoors(true);
		}
    }

	@Override
	public void loadCreatureFlags() {
		this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
	}

    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.BagSize; }
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
    	// Destroy Blocks:
		if(!this.getEntityWorld().isRemote)
	        if(this.getAttackTarget() != null && this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING) && this.griefing) {
		    	float distance = this.getAttackTarget().getDistance(this);
		    		if(distance <= this.getSize(Pose.STANDING).width + 4.0F)
		    			this.destroyArea((int)this.getPositionVec().getX(), (int)this.getPositionVec().getY(), (int)this.getPositionVec().getZ(), 0.5F, true);
	        }
        
        super.livingTick();
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	boolean success = super.attackMelee(target, damageScale);
    	if(success)
    		this.nextAttackPhase();
    	return success;
    }
}
