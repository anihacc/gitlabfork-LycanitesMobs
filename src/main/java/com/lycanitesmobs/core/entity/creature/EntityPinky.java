package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.List;

public class EntityPinky extends EntityCreatureRideable implements IAnimals, IGroupAnimal, IGroupAlpha, IGroupPredator, IGroupHunter, IGroupDemon {
	
	EntityAIPlayerControl playerControlAI;
	
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
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        this.field_70714_bg.addTask(1, new EntityAIMate(this));
        this.field_70714_bg.addTask(4, new EntityAITempt(this).setTemptDistanceMin(4.0D));
        this.field_70714_bg.addTask(5, new EntityAIAttackMelee(this).setTargetClass(EntityPigZombie.class).setSpeed(1.5D).setDamage(8.0D).setRange(2.5D));
        this.field_70714_bg.addTask(6, new EntityAIAttackMelee(this).setSpeed(1.5D));
        this.field_70714_bg.addTask(7, this.aiSit);
        this.field_70714_bg.addTask(8, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.playerControlAI = new EntityAIPlayerControl(this);
        this.field_70714_bg.addTask(9, playerControlAI);
        this.field_70714_bg.addTask(10, new EntityAIWander(this).setSpeed(1.0D));
        this.field_70714_bg.addTask(11, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(12, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetRiderRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetRiderAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(3, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(4, new EntityAITargetOwnerThreats(this));
        this.field_70715_bh.addTask(5, new EntityAITargetRevenge(this).setHelpCall(true));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(6, new EntityAITargetAttack(this).setTargetClass(EntityCow.class).setTameTargetting(true));
            this.field_70715_bh.addTask(6, new EntityAITargetAttack(this).setTargetClass(EntityPig.class).setTameTargetting(true));
            this.field_70715_bh.addTask(6, new EntityAITargetAttack(this).setTargetClass(EntitySheep.class).setTameTargetting(true));
        }
        this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(6, new EntityAITargetAttack(this).setTargetClass(EntityPigZombie.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(6, new EntityAITargetAttack(this).setTargetClass(IGroupAlpha.class));
            this.field_70715_bh.addTask(6, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class));
            this.field_70715_bh.addTask(6, new EntityAITargetAttack(this).setTargetClass(AnimalEntity.class));
        }
        this.field_70715_bh.addTask(7, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Become a farmed animal if removed from the Nether to another dimension, prevents natural despawning.
        if(this.getEntityWorld().provider.getDimension() != -1)
        	this.setFarmed();
    }
    
    public void riderEffects(LivingEntity rider) {
    	if(rider.isPotionActive(MobEffects.WITHER))
    		rider.removeEffectInstance(MobEffects.WITHER);
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
        return (double)this.height * 1.0D;
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
    	if(target instanceof EntityCow || target instanceof EntityPig || target instanceof EntitySheep || target instanceof EntityHorse || target instanceof EntityLlama)
    		this.breed();
    	
        return true;
    }

    // ========== Special Attack ==========
    public void specialAttack() {
        // Withering Roar:
        double distance = 5.0D;
        List<LivingEntity> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, this.getEntityBoundingBox().grow(distance, distance, distance), new Predicate<LivingEntity>() {
            @Override
            public boolean apply(LivingEntity possibleTarget) {
                if(!possibleTarget.isAlive()
                        || possibleTarget == EntityPinky.this
                        || EntityPinky.this.isRidingOrBeingRiddenBy(possibleTarget)
                        || EntityPinky.this.isOnSameTeam(possibleTarget)
                        || !EntityPinky.this.canAttackClass(possibleTarget.getClass())
                        || !EntityPinky.this.canAttackEntity(possibleTarget))
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
                    possibleTarget.addPotionEffect(new EffectInstance(MobEffects.WITHER, 10 * 20, 0));
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
