package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.CopyOwnerAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeOwnerGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class EntitySutiramu extends TameableCreatureEntity implements IMob, IGroupPredator {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySutiramu(EntityType<? extends EntitySutiramu> entityType, World world) {
        super(entityType, world);

        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;

        this.canGrow = false;
        this.babySpawnChance = 0.01D;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PaddleGoal(this));
        this.goalSelector.addGoal(2, new AttackMeleeGoal(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.goalSelector.addGoal(3, new AttackMeleeGoal(this));
        this.goalSelector.addGoal(5, this.stayGoal);
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(7, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RevengeOwnerGoal(this));
        this.targetSelector.addGoal(1, new CopyOwnerAttackTargetGoal(this));
        this.targetSelector.addGoal(2, new RevengeGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(3, new FindAttackTargetGoal(this).addTargets(EntityType.PLAYER));
        this.targetSelector.addGoal(3, new FindAttackTargetGoal(this).addTargets(EntityType.VILLAGER));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).setTargetClass(IGroupPrey.class));
        this.targetSelector.addGoal(5, new FindAttackTargetGoal(this).setTargetClass(IGroupAlpha.class).setPackHuntingScale(2, 1));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetSelector.addGoal(5, new FindAttackTargetGoal(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(2, 1));
            this.targetSelector.addGoal(5, new FindAttackTargetGoal(this).setTargetClass(AnimalEntity.class).setPackHuntingScale(2, 1));
        }
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean canBurn() { return false; }
	

    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
