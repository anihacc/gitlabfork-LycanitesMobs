package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.List;

public class EntityBarghest extends EntityCreatureRideable implements IGroupPredator {

    protected boolean leapedAbilityQueued = false;
    protected boolean leapedAbilityReady = false;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityBarghest(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spreadFire = false;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        this.setupMob();
        
        // Stats:
        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
		super.initEntityAI();
		this.field_70714_bg.addTask(0, new SwimmingGoal(this));
		//this.field_70714_bg.addTask(2, new EntityAIPlayerControl(this));
		this.field_70714_bg.addTask(4, new TemptGoal(this).setTemptDistanceMin(4.0D));
		this.field_70714_bg.addTask(5, new AttackMeleeGoal(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
		this.field_70714_bg.addTask(6, new AttackMeleeGoal(this));
		this.field_70714_bg.addTask(7, this.aiSit);
		this.field_70714_bg.addTask(8, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
		this.field_70714_bg.addTask(9, new FollowParentGoal(this).setSpeed(1.0D));
		this.field_70714_bg.addTask(10, new WanderGoal(this));
		this.field_70714_bg.addTask(11, new BegGoal(this));
		this.field_70714_bg.addTask(12, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
		this.field_70714_bg.addTask(13, new LookIdleGoal(this));

		this.field_70715_bh.addTask(0, new RiderRevengeTargetingGoal(this));
		this.field_70715_bh.addTask(1, new RiderAttackTargetingGoal(this));
		this.field_70715_bh.addTask(2, new OwnerRevengeTargetingGoal(this));
		this.field_70715_bh.addTask(3, new OwnerAttackTargetingGoal(this));
		this.field_70715_bh.addTask(3, new OwnerDefenseTargetingGoal(this));
		this.field_70715_bh.addTask(4, new RevengeTargetingGoal(this).setHelpCall(true));
		this.field_70715_bh.addTask(5, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
		this.field_70715_bh.addTask(5, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
		this.field_70715_bh.addTask(6, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
		this.field_70715_bh.addTask(7, new AttackTargetingGoal(this).setTargetClass(IGroupAlpha.class).setPackHuntingScale(1, 1));
		if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
			this.field_70715_bh.addTask(8, new AttackTargetingGoal(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(1, 3));
			this.field_70715_bh.addTask(8, new AttackTargetingGoal(this).setTargetClass(AnimalEntity.class).setPackHuntingScale(1, 3));
		}
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Random Leaping:
        if(!this.isTamed() && this.onGround && !this.getEntityWorld().isRemote) {
        	if(this.hasAttackTarget()) {
        		if(this.rand.nextInt(10) == 0)
        			this.leap(4.0F, 1D, this.getAttackTarget());
        	}
        }

        // Leap Landing Paralysis:
        if(this.leapedAbilityQueued && !this.onGround && !this.getEntityWorld().isRemote) {
            this.leapedAbilityQueued = false;
            this.leapedAbilityReady = true;
        }
        if(this.leapedAbilityReady && this.onGround && !this.getEntityWorld().isRemote) {
            this.leapedAbilityReady = false;
            double distance = 4.0D;
            List<LivingEntity> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(distance, distance, distance), possibleTarget -> {
				if (!possibleTarget.isAlive()
						|| possibleTarget == EntityBarghest.this
						|| EntityBarghest.this.isRidingOrBeingRiddenBy(possibleTarget)
						|| EntityBarghest.this.isOnSameTeam(possibleTarget)
						|| !EntityBarghest.this.canAttackClass(possibleTarget.getClass())
						|| !EntityBarghest.this.canAttackEntity(possibleTarget))
					return false;

				return true;
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
                        if (ObjectManager.getEffect("weight") != null)
                            possibleTarget.addPotionEffect(new EffectInstance(ObjectManager.getEffect("weight"), this.getEffectDuration(5), 1));
                        else
                            possibleTarget.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 10 * 20, 0));
                    }
                }
            }
            this.playAttackSound();
        }
    }
    
    public void riderEffects(LivingEntity rider) {
    	if(rider.isPotionActive(Effects.SLOWNESS))
    		rider.removePotionEffect(Effects.SLOWNESS);
    	if(rider.isPotionActive(ObjectManager.getEffect("weight")))
    		rider.removePotionEffect(ObjectManager.getEffect("weight"));
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(!this.onGround)
    		return 2.0F;
    	return 1.0F;
    }

    // ========== Mounted Offset ==========
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.8D;
    }

    // ========== Leap ==========
    @Override
    public void leap(double distance, double leapHeight) {
        super.leap(distance, leapHeight);
        if(!this.getEntityWorld().isRemote)
            this.leapedAbilityQueued = true;
    }

    // ========== Leap to Target ==========
    @Override
    public void leap(float range, double leapHeight, Entity target) {
        super.leap(range, leapHeight, target);
        if(!this.getEntityWorld().isRemote)
            this.leapedAbilityQueued = true;
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    public void mountAbility(Entity rider) {
    	if(this.getEntityWorld().isRemote)
    		return;

        if(!this.onGround)
            return;
    	if(this.abilityToggled)
    		return;
    	if(this.getStamina() < this.getStaminaCost())
    		return;
    	
    	this.playJumpSound();
    	this.leap(2.0D, 1.5D);
    	
    	this.applyStaminaCost();
    }
    
    public float getStaminaCost() {
    	return 15;
    }
    
    public int getStaminaRecoveryWarmup() {
    	return 5 * 20;
    }
    
    public float getStaminaRecoveryMax() {
    	return 1.0F;
    }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 10; }


    // ==================================================
    //                     Abilities
    // ==================================================
    @Override
    public boolean canClimb() { return true; }


	// ==================================================
	//                     Pet Control
	// ==================================================
	public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
    	return 100;
    }
	
	
	// ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityBarghest(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
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
