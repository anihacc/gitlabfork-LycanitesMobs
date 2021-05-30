package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.GoalConditions;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FireProjectilesGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityChupacabra extends TameableCreatureEntity {

	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityChupacabra(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setSpeed(1.5D));
        this.tasks.addTask(this.nextIdleGoalIndex, new FireProjectilesGoal(this).setProjectile("chaosorb").setFireRate(40).setVelocity(1.0F).setScale(0.5F)
                .setConditions(new GoalConditions().setRareVariantOnly(true)));
    }

    @Override
    public boolean shouldCreatureGroupFlee(EntityLivingBase target) {
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
        if((target instanceof EntityAnimal || (target instanceof BaseCreatureEntity && ((BaseCreatureEntity)target).creatureInfo.isFarmable())) && target.height >= 1F)
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
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.BagSize; }



    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
        return false; // Breeding is triggered by attacking specific mobs instead!
    }
}
