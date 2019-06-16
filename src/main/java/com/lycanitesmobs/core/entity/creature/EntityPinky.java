package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.List;

public class EntityPinky extends EntityCreatureRideable implements IGroupAnimal, IGroupAlpha, IGroupPredator, IGroupHunter, IGroupDemon {
	
	PlayerControlGoal playerControlAI;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityPinky(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.hasAttackSound = true;
        this.spreadFire = true;
        this.setupMob();
        
        this.attackCooldownMax = 10;
        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new SwimmingGoal(this));
        this.field_70714_bg.addTask(1, new MateGoal(this));
        this.field_70714_bg.addTask(4, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.field_70714_bg.addTask(5, new AttackMeleeGoal(this).setTargetClass(ZombiePigmanEntity.class).setSpeed(1.5D).setDamage(8.0D).setRange(2.5D));
        this.field_70714_bg.addTask(6, new AttackMeleeGoal(this).setSpeed(1.5D));
        this.field_70714_bg.addTask(7, this.aiSit);
        this.field_70714_bg.addTask(8, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.playerControlAI = new PlayerControlGoal(this);
        this.field_70714_bg.addTask(9, playerControlAI);
        this.field_70714_bg.addTask(10, new WanderGoal(this).setSpeed(1.0D));
        this.field_70714_bg.addTask(11, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(12, new LookIdleGoal(this));

        this.field_70715_bh.addTask(0, new RiderRevengeTargetingGoal(this));
        this.field_70715_bh.addTask(1, new RiderAttackTargetingGoal(this));
        this.field_70715_bh.addTask(2, new OwnerRevengeTargetingGoal(this));
        this.field_70715_bh.addTask(3, new OwnerAttackTargetingGoal(this));
        this.field_70715_bh.addTask(4, new OwnerDefenseTargetingGoal(this));
        this.field_70715_bh.addTask(5, new RevengeTargetingGoal(this).setHelpCall(true));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(6, new AttackTargetingGoal(this).setTargetClass(CowEntity.class).setTameTargetting(true));
            this.field_70715_bh.addTask(6, new AttackTargetingGoal(this).setTargetClass(PigEntity.class).setTameTargetting(true));
            this.field_70715_bh.addTask(6, new AttackTargetingGoal(this).setTargetClass(SheepEntity.class).setTameTargetting(true));
        }
        this.field_70715_bh.addTask(5, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(5, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(6, new AttackTargetingGoal(this).setTargetClass(ZombiePigmanEntity.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(6, new AttackTargetingGoal(this).setTargetClass(IGroupAlpha.class));
            this.field_70715_bh.addTask(6, new AttackTargetingGoal(this).setTargetClass(IGroupAnimal.class));
            this.field_70715_bh.addTask(6, new AttackTargetingGoal(this).setTargetClass(AnimalEntity.class));
        }
        this.field_70715_bh.addTask(7, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Become a farmed animal if removed from the Nether to another dimension, prevents natural despawning.
        if(this.getEntityWorld().getDimension().getType().getId() != -1)
        	this.setFarmed();
    }
    
    public void riderEffects(LivingEntity rider) {
    	if(rider.isPotionActive(Effects.field_82731_v))
    		rider.removePotionEffect(Effects.field_82731_v);
        if(rider.isBurning())
            rider.setFire(0);
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    @Override
    public void mountAbility(Entity rider) {
    	if(this.getEntityWorld().isRemote)
    		return;

    	if(this.abilityToggled)
    		return;
    	if(this.getStamina() < this.getStaminaCost())
    		return;
        this.specialAttack();
    	this.applyStaminaCost();
    }
    
    public float getStaminaCost() {
    	return 20;
    }
    
    public int getStaminaRecoveryWarmup() {
    	return 5 * 20;
    }
    
    public float getStaminaRecoveryMax() {
    	return 1.0F;
    }
	
	
    // ==================================================
    //                     Movement
    // ==================================================
    @Override
    public double getMountedYOffset() {
        return (double)this.getSize(Pose.STANDING).height * 1.0D;
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
        if((target instanceof AnimalEntity || target instanceof IGroupAnimal) && target.getSize(Pose.STANDING).height < 1F)
    		this.breed();
    	
        return true;
    }

    // ========== Special Attack ==========
    public void specialAttack() {
        // Withering Roar:
        double distance = 5.0D;
        List<LivingEntity> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(distance, distance, distance), new Predicate<LivingEntity>() {
            @Override
            public boolean apply(LivingEntity possibleTarget) {
                if(!possibleTarget.isAlive()
                        || possibleTarget == EntityPinky.this
                        || EntityPinky.this.isRidingOrBeingRiddenBy(possibleTarget)
                        || EntityPinky.this.isOnSameTeam(possibleTarget)
                        || !EntityPinky.this.canAttack(possibleTarget.getType())
                        || !EntityPinky.this.canAttack(possibleTarget))
                    return false;
                return true;
            }
        });
        if(!possibleTargets.isEmpty()) {
            for(LivingEntity possibleTarget : possibleTargets) {
                boolean doDamage = true;
                if(this.getRider() instanceof PlayerEntity) {
                    if(MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((PlayerEntity)this.getRider(), possibleTarget))) {
                        doDamage = false;
                    }
                }
                if(doDamage) {
                    possibleTarget.addPotionEffect(new EffectInstance(Effects.field_82731_v, 10 * 20, 0));
                }
            }
        }
        this.playAttackSound();
        this.triggerAttackCooldown();
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
    public int getBagSize() { return 10; }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean canBurn() {
        return false;
    }
    
    @Override
    public float getFallResistance() {
    	return 10;
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityPinky(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
        if(!CreatureManager.getInstance().config.predatorsAttackAnimals)
            return ObjectLists.inItemList("rawmeat", itemStack) || ObjectLists.inItemList("cookedmeat", itemStack);
        return false; // Breeding is triggered by attacking specific mobs instead!
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("CookedMeat", testStack);
    }
}
