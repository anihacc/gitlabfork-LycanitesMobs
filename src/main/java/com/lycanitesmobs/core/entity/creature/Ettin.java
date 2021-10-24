package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class Ettin extends AgeableCreatureEntity implements Enemy {
	public boolean griefing = true;
    
    public Ettin(EntityType<? extends Ettin> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        
        this.solidCollision = true;
        this.setupMob();

        this.attackPhaseMax = 2;
    }

    @Override
    protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));

		if(this.getNavigation() instanceof GroundPathNavigation) {
			GroundPathNavigation pathNavigateGround = (GroundPathNavigation)this.getNavigation();
			pathNavigateGround.setCanOpenDoors(true);
		}
    }

	@Override
	public void loadCreatureFlags() {
		this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
	}

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

	@Override
    public void aiStep() {
		if(!this.getCommandSenderWorld().isClientSide)
	        if(this.getTarget() != null && this.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.griefing) {
		    	float distance = this.getTarget().distanceTo(this);
		    		if(distance <= this.getDimensions(Pose.STANDING).width + 4.0F)
		    			this.destroyArea((int)this.position().x(), (int)this.position().y(), (int)this.position().z(), 0.5F, true);
	        }
        
        super.aiStep();
    }

    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	boolean success = super.attackMelee(target, damageScale);
    	if(success)
    		this.nextAttackPhase();
    	return success;
    }
}
