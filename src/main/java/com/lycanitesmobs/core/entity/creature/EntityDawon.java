package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.world.World;

public class EntityDawon extends TameableCreatureEntity {

	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityDawon(EntityType<? extends EntityDawon> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
        
        this.attackCooldownMax = 15;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(ZombiePigmanEntity.class).setSpeed(1.5D).setDamageScale(8.0D).setRange(2.5D));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setSpeed(1.5D));
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
                    this.leap(16.0F, 0.2D, this.getAttackTarget());
            }
            else {
                if(this.isMoving() && this.rand.nextInt(50) == 0)
                    this.leap(2.0D, 0.5D);
            }
        }
    }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public float getFallResistance() {
        return 100;
    }
}
