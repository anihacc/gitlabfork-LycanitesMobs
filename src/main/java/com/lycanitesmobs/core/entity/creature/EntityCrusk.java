package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCrusk extends EntityCreatureTameable implements IGroupPredator, IGroupHeavy {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCrusk(EntityType<? extends EntityCrusk> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.babySpawnChance = 0.25D;
        this.growthTime = -120000;
        this.setupMob();
        this.hitAreaWidthScale = 1.5F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(1, new StealthGoal(this).setStealthTime(60));
        this.goalSelector.addGoal(2, this.aiSit);
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(4, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.goalSelector.addGoal(5, new AttackMeleeGoal(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.goalSelector.addGoal(6, new AttackMeleeGoal(this));
        this.goalSelector.addGoal(7, new WanderGoal(this));
        this.goalSelector.addGoal(9, new BegGoal(this));

        this.targetSelector.addGoal(0, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new OwnerAttackTargetingGoal(this));
        this.targetSelector.addGoal(2, new RevengeTargetingGoal(this));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(IGroupAlpha.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(IGroupAnimal.class));
            this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(AnimalEntity.class));
        }
        this.targetSelector.addGoal(6, new OwnerDefenseTargetingGoal(this));
    }
    
    
    // ==================================================
   	//                      Stealth
   	// ==================================================
    @Override
    public boolean canStealth() {
    	if(this.isTamed() && this.isSitting())
    		return false;
        BlockState blockState = this.getEntityWorld().getBlockState(this.getPosition().add(0, -1, 0));
        if(blockState.getBlock() != Blocks.AIR) {
        	if(blockState.getMaterial() == Material.EARTH) return true;
        	if(blockState.getMaterial() == Material.ORGANIC) return true;
        	if(blockState.getMaterial() == Material.LEAVES) return true;
        	if(blockState.getMaterial() == Material.SAND) return true;
        	if(blockState.getMaterial() == Material.CLAY) return true;
        	if(blockState.getMaterial() == Material.SNOW) return true;
        	if(blockState.getMaterial() == Material.SNOW_BLOCK) return true;
        }
        if(blockState.getBlock() == Blocks.NETHERRACK)
            return true;
    	return false;
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
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	if(type.equals("inWall")) return false;
    	return super.isInvulnerableTo(type, source, damage);
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack par1ItemStack) {
		return false;
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("cookedmeat", testStack);
    }
}
