package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeGoal;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EntityEttin extends AgeableCreatureEntity implements IMob {
	public boolean ettinGreifing = true; // TODO Creature flags.
    
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

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        if(this.getNavigator() instanceof GroundPathNavigator) {
            GroundPathNavigator pathNavigateGround = (GroundPathNavigator)this.getNavigator();
            pathNavigateGround.setBreakDoors(true);
        }
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(1, new BreakDoorGoal(this));
        this.goalSelector.addGoal(3, new AttackMeleeGoal(this).setLongMemory(false));
        this.goalSelector.addGoal(6, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RevengeGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(2, new FindAttackTargetGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(2, new FindAttackTargetGoal(this).setTargetClass(VillagerEntity.class));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
    	// Destroy Blocks:
		if(!this.getEntityWorld().isRemote)
	        if(this.getAttackTarget() != null && this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING) && this.ettinGreifing) {
		    	float distance = this.getAttackTarget().getDistance(this);
		    		if(distance <= this.getSize(Pose.STANDING).width + 4.0F)
		    			this.destroyArea((int)this.posX, (int)this.posY, (int)this.posZ, 10, true);
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
