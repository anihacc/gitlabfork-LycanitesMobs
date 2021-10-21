package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

public class EntityCrusk extends TameableCreatureEntity implements IGroupHeavy {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCrusk(EntityType<? extends EntityCrusk> entityType, Level world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = MobType.ARTHROPOD;
        this.hasAttackSound = true;
        this.babySpawnChance = 0.25D;
        this.growthTime = -120000;
        this.setupMob();
        this.hitAreaWidthScale = 1.5F;

        // Stats:
        this.maxUpStep = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextPriorityGoalIndex++, new StealthGoal(this).setStealthTime(60));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(Player.class).setLongMemory(false));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }

    @Override
	public boolean rollWanderChance() {
		return this.getRandom().nextDouble() <= 0.001D;
	}
    
    
    // ==================================================
   	//                      Stealth
   	// ==================================================
    @Override
    public boolean canStealth() {
    	if(this.isTamed() && this.isSitting())
    		return false;
        BlockState blockState = this.getCommandSenderWorld().getBlockState(this.blockPosition().offset(0, -1, 0));
		if(blockState.getBlock() != Blocks.AIR) {
        	if(blockState.getMaterial() == Material.DIRT) return true;
        	if(blockState.getMaterial() == Material.GRASS) return true;
        	if(blockState.getMaterial() == Material.LEAVES) return true;
        	if(blockState.getMaterial() == Material.SAND) return true;
        	if(blockState.getMaterial() == Material.CLAY) return true;
        	if(blockState.getMaterial() == Material.TOP_SNOW) return true;
        	if(blockState.getMaterial() == Material.SNOW) return true;
        }
        if(blockState.getBlock() == Blocks.NETHERRACK)
            return true;
    	return false;
    }
    
    
    // ==================================================
   	//                     Abilities
   	// ==================================================
    public boolean canBeTempted() {
    	return this.isBaby();
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	if(type.equals("inWall")) return false;
    	return super.isVulnerableTo(type, source, damage);
    }
}
