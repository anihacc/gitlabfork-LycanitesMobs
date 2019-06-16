package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.List;

public class EntityWarg extends EntityCreatureRideable implements IGroupPredator {

    protected boolean leapedAbilityQueued = false;
    protected boolean leapedAbilityReady = false;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityWarg(World world) {
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
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        //this.field_70714_bg.addTask(2, new EntityAIPlayerControl(this));
        this.field_70714_bg.addTask(4, new EntityAITempt(this).setTemptDistanceMin(4.0D));
        this.field_70714_bg.addTask(5, new EntityAIAttackMelee(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.field_70714_bg.addTask(6, new EntityAIAttackMelee(this));
        this.field_70714_bg.addTask(7, this.aiSit);
        this.field_70714_bg.addTask(8, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(9, new EntityAIFollowParent(this).setSpeed(1.0D));
        this.field_70714_bg.addTask(10, new EntityAIWander(this));
        this.field_70714_bg.addTask(11, new EntityAIBeg(this));
        this.field_70714_bg.addTask(12, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(13, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetRiderRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetRiderAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(3, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(3, new EntityAITargetOwnerThreats(this));
        this.field_70715_bh.addTask(4, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(6, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        this.field_70715_bh.addTask(7, new EntityAITargetAttack(this).setTargetClass(IGroupAlpha.class).setPackHuntingScale(1, 1));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(8, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(1, 3));
            this.field_70715_bh.addTask(8, new EntityAITargetAttack(this).setTargetClass(AnimalEntity.class).setPackHuntingScale(1, 3));
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
        			this.leap(6.0F, 1D, this.getAttackTarget());
        	}
        }

        // Leap Landing Effect:
        if(this.leapedAbilityQueued && !this.onGround && !this.getEntityWorld().isRemote) {
            this.leapedAbilityQueued = false;
            this.leapedAbilityReady = true;
        }
        if(this.leapedAbilityReady && this.onGround && !this.getEntityWorld().isRemote) {
            this.leapedAbilityReady = false;
            double distance = 4.0D;
            List<LivingEntity> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, this.getEntityBoundingBox().grow(distance, distance, distance), possibleTarget -> {
				if (!possibleTarget.isAlive()
						|| possibleTarget == EntityWarg.this
						|| EntityWarg.this.isRidingOrBeingRiddenBy(possibleTarget)
						|| EntityWarg.this.isOnSameTeam(possibleTarget)
						|| !EntityWarg.this.canAttackClass(possibleTarget.getClass())
						|| !EntityWarg.this.canAttackEntity(possibleTarget))
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
                        for(ElementInfo element : this.creatureInfo.elements) {
							element.debuffEntity(possibleTarget, this.getEffectDuration(1), this.getEffectAmplifier(1));
						}
                    }
                }
            }
            this.playAttackSound();
        }
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
        return (double)this.height * 0.85D;
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
    @Override
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
        this.leap(4.0D, 0.5D);

        this.applyStaminaCost();
    }

    @Override
    public float getStaminaCost() {
        return 15;
    }

    @Override
    public int getStaminaRecoveryWarmup() {
        return 5 * 20;
    }

    @Override
    public float getStaminaRecoveryMax() {
        return 1.0F;
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
    public int getBagSize() { return 10; }
    
    
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
		return new EntityWarg(this.getEntityWorld());
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