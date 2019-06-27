package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class EntityGeken extends TameableCreatureEntity implements IMob {
	
	private AttackMeleeGoal meleeAttackAI;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGeken(EntityType<? extends EntityGeken> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        this.setupMob();
        
        // Stats:
        this.attackPhaseMax = 3;
        this.setAttackCooldownMax(10);
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        meleeAttackAI = new AttackMeleeGoal(this);
        this.goalSelector.addGoal(3, meleeAttackAI);
        this.goalSelector.addGoal(4, this.aiSit);
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(6, new WanderGoal(this).setPauseRate(30));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RevengeOwnerGoal(this));
        this.targetSelector.addGoal(1, new CopyOwnerAttackTargetGoal(this));
        this.targetSelector.addGoal(2, new RevengeGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(3, new FindAttackTargetGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(6, new DefendOwnerGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Random Leaping:
        if(this.onGround && !this.getEntityWorld().isRemote) {
        	if(this.hasAttackTarget()) {
        		if(this.rand.nextInt(10) == 0)
        			this.leap(6.0F, 0.6D, this.getAttackTarget());
        	}
        	else {
        		if(this.isMoving() && this.rand.nextInt(50) == 0)
        			this.leap(1.0D, 1.0D);
        	}
        }
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
	@Override
	public int getMeleeCooldown() {
		if(this.getAttackPhase() == 2)
			return super.getMeleeCooldown() * 3;
		return super.getMeleeCooldown();
	}

	@Override
	public int getRangedCooldown() {
		if(this.getAttackPhase() == 2)
			return super.getRangedCooldown() * 3;
		return super.getRangedCooldown();
	}


	// ==================================================
	//                     Abilities
	// ==================================================
	@Override
	public boolean canClimb() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
    	return 100;
    }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 5; }
	

    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
