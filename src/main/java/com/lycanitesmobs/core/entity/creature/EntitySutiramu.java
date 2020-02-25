package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntitySutiramu extends TameableCreatureEntity implements IMob {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySutiramu(World world) {
        super(world);

        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;

        this.canGrow = false;
        this.babySpawnChance = 0.01D;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(EntityPlayer.class).setLongMemory(false));
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
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
