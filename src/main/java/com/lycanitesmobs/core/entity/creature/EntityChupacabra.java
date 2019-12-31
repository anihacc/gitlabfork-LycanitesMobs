package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityChupacabra extends TameableCreatureEntity {

	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityChupacabra(EntityType<? extends EntityChupacabra> entityType, World world) {
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
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setSpeed(1.5D));
    }

    @Override
    public boolean shouldCreatureGroupFlee(LivingEntity target) {
        return false;
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
        if(!super.attackMelee(target, damageScale))
        	return false;
        
    	// Breed:
        if((target instanceof AnimalEntity || (target instanceof BaseCreatureEntity && ((BaseCreatureEntity)target).creatureInfo.isFarmable())) && target.getSize(Pose.STANDING).height >= 1F)
    		this.breed();

        // Leech:
        float leeching = Math.max(1, this.getAttackDamage(damageScale) / 2);
        this.heal(leeching);
    	
        return true;
    }
    
    
    // ==================================================
   	//                     Abilities
   	// ==================================================
    public boolean canBeTempted() {
    	return this.isChild();
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
    //                     Breeding
    // ==================================================
    // ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
        return false; // Breeding is triggered by attacking specific mobs instead!
    }
}
