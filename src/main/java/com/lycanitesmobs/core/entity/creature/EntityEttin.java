package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.World;

public class EntityEttin extends AgeableCreatureEntity implements IMob {
	public boolean griefing = true;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEttin(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        
        this.solidCollision = true;
        this.setupMob();
        
        // Stats:
        this.attackPhaseMax = 2;
    }

    @Override
    protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.addTask(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));

		if(this.getNavigator() instanceof PathNavigateGround) {
			PathNavigateGround pathNavigateGround = (PathNavigateGround)this.getNavigator();
			pathNavigateGround.setBreakDoors(true);
		}
    }

	@Override
	public void loadCreatureFlags() {
		this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
    	// Destroy Blocks:
		if(!this.getEntityWorld().isRemote)
	        if(this.getAttackTarget() != null && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.griefing) {
		    	float distance = this.getAttackTarget().getDistance(this);
		    		if(distance <= this.width + 4.0F)
		    			this.destroyArea((int)this.posX, (int)this.posY, (int)this.posZ, 0.5F, true);
	        }
        
        super.onLivingUpdate();
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
