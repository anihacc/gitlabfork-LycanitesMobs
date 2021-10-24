package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;

public class Lycosa extends TameableCreatureEntity implements Enemy {
    
    public Lycosa(EntityType<? extends Lycosa> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.ARTHROPOD;
        this.hasAttackSound = true;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
    }

	@Override
    public void aiStep() {
        super.aiStep();
        
        if(this.onGround && !this.getCommandSenderWorld().isClientSide) {
        	if(this.hasAttackTarget()) {
        		if(this.random.nextInt(10) == 0)
        			this.leap(6.0F, 0.6D, this.getTarget());
        	}
        	else {
        		if(this.random.nextInt(50) == 0 && this.isMoving())
        			this.leap(1.0D, 1.0D);
        	}
        }

        if(!this.getCommandSenderWorld().isClientSide && (this.tickCount % 10 == 0 || this.isMoving() && this.tickCount % 5 == 0)) {
            int trailHeight = 1;
            for(int y = 0; y < trailHeight; y++) {
                Block block = this.getCommandSenderWorld().getBlockState(this.blockPosition().offset(0, y, 0)).getBlock();
                if(block == Blocks.AIR || block == Blocks.SNOW || block == ObjectManager.getBlock("quickweb"))
                    this.getCommandSenderWorld().setBlockAndUpdate(this.blockPosition().offset(0, y, 0), ObjectManager.getBlock("quickweb").defaultBlockState());
            }
        }
    }

    @Override
    public boolean canClimb() { return true; }

    public boolean petControlsEnabled() { return true; }


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    
    

    @Override
    public float getFallResistance() {
    	return 100;
    }
    
    @Override
    public boolean webProof() { return true; }
}
