package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.List;

public class EntityCockatrice extends EntityCreatureRideable implements IMob, IGroupHunter {

    protected EntityAIWander wanderAI;
    protected EntityAIAttackMelee attackAI;
    protected boolean wantsToLand;
    protected boolean  isLanded;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCockatrice(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.flySoundSpeed = 20;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIPlayerControl(this));
        this.tasks.addTask(4, new EntityAITempt(this).setTemptDistanceMin(4.0D));
        this.attackAI = new EntityAIAttackMelee(this).setLongMemory(false);
        this.tasks.addTask(5, this.attackAI);
        this.tasks.addTask(6, this.aiSit);
        this.tasks.addTask(7, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.wanderAI = new EntityAIWander(this).setPauseRate(0);
        this.tasks.addTask(8, this.wanderAI);
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetRiderRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetRiderAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(3, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(4, new EntityAITargetOwnerThreats(this));
        this.targetTasks.addTask(5, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(6, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.targetTasks.addTask(6, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
		this.targetTasks.addTask(6, new EntityAITargetAttack(this).setTargetClass(EntityConcapedeSegment.class));
		this.targetTasks.addTask(6, new EntityAITargetAttack(this).setTargetClass(EntityVespid.class));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Land/Fly:
        if(!this.getEntityWorld().isRemote && !this.isAIDisabled()) {
            if(this.isLanded) {
                this.wantsToLand = false;
                if(this.hasPickupEntity() || this.getControllingPassenger() != null || this.getLeashed() || this.isInWater() || (!this.isTamed() && this.updateTick % (5 * 20) == 0 && this.getRNG().nextBoolean())) {
                    this.leap(1.0D, 1.0D);
                    this.wanderAI.setPauseRate(0);
                    this.isLanded = false;
                }
            }
            else {
                if(this.wantsToLand) {
                    if(!this.isLanded && this.isSafeToLand()) {
                        this.wanderAI.setPauseRate(120);
                        this.isLanded = true;
                    }
                }
                else {
                    if (!this.hasPickupEntity() && !this.hasAttackTarget() && this.updateTick % (5 * 20) == 0 && this.getRNG().nextBoolean()) {
                        this.wantsToLand = true;
                    }
                }
            }
            if(this.hasPickupEntity() || this.getControllingPassenger() != null || this.hasAttackTarget() || this.isInWater()) {
                this.wantsToLand = false;
            }
            else if(this.isTamed() && !this.getLeashed()) {
                this.wantsToLand = true;
            }
        }

        // Random Leaping:
        if(!this.isTamed() && !this.getEntityWorld().isRemote) {
            if(this.hasAttackTarget()) {
                if(this.rand.nextInt(10) == 0)
                    this.leap(2.0F, 1D, this.getAttackTarget());
            }
            else {
                if(this.rand.nextInt(50) == 0 && this.isMoving())
                    this.leap(1.0D, 1D);
            }
        }
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Get Wander Position ==========
    public BlockPos getWanderPosition(BlockPos wanderPosition) {
        if(this.wantsToLand || !this.isLanded) {
            BlockPos groundPos;
            for(groundPos = wanderPosition.down(); groundPos.getY() > 0 && this.getEntityWorld().getBlockState(groundPos).getBlock() == Blocks.AIR; groundPos = groundPos.down()) {}
            if(this.getEntityWorld().getBlockState(groundPos).getMaterial().isSolid()) {
                return groundPos.up();
            }
        }
        if(this.hasPickupEntity() && this.getPickupEntity() instanceof PlayerEntity)
            wanderPosition = new BlockPos(wanderPosition.getX(), this.restrictYHeightFromGround(wanderPosition, 6, 14), wanderPosition.getZ());
        return wanderPosition;
    }

    // ========== Get Flight Offset ==========
    public double getFlightOffset() {
        if(!this.wantsToLand) {
            super.getFlightOffset();
        }
        return 0;
    }


	// ==================================================
	//                      Attacks
	// ==================================================
	// ========== Melee Attack ==========
	@Override
	public boolean attackMelee(Entity target, double damageScale) {
		if (!super.attackMelee(target, damageScale))
			return false;

		// Vespid Extermination:
		if (target instanceof EntityVespid) {
			target.remove();
		}

		return true;
	}

	// ========== Special Attack ==========
	public void specialAttack() {
		// Petrifying Caw:
		double distance = 5.0D;
		List<EntityLivingBase> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(distance, distance, distance), possibleTarget -> {
				if(!possibleTarget.isEntityAlive()
						|| possibleTarget == EntityCockatrice.this
						|| EntityCockatrice.this.isRidingOrBeingRiddenBy(possibleTarget)
						|| EntityCockatrice.this.isOnSameTeam(possibleTarget)
						|| !EntityCockatrice.this.canAttackClass(possibleTarget.getClass())
						|| !EntityCockatrice.this.canAttackEntity(possibleTarget))
					return false;
			return true;
		});
		if(!possibleTargets.isEmpty()) {
			for(EntityLivingBase possibleTarget : possibleTargets) {
				boolean doDamage = true;
				if(this.getRider() instanceof PlayerEntity) {
					if(MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((PlayerEntity)this.getRider(), possibleTarget))) {
						doDamage = false;
					}
				}
				if(doDamage) {
					if (ObjectManager.getEffect("paralysis") != null)
						possibleTarget.addPotionEffect(new PotionEffect(ObjectManager.getEffect("paralysis"), this.getEffectDuration(5), 1));

					if (ObjectManager.getEffect("aphagia") != null)
						possibleTarget.addPotionEffect(new PotionEffect(ObjectManager.getEffect("aphagia"), this.getEffectDuration(5), 1));
					else
						possibleTarget.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 10 * 20, 0));
				}
			}
		}
		this.playAttackSound();
		this.triggerAttackCooldown();
	}
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() {
        return !this.isLanded || this.hasPickupEntity();
    }
    
    
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
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 5; }


    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
        return ObjectLists.inItemList("CookedMeat", testStack) || ObjectLists.inItemList("CookedFish", testStack);
    }


    // ==================================================
    //                      Movement
    // ==================================================
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.7D;
    }

	@Override
	public double getMountedZOffset() {
		return -(double)this.width * 0.01D;
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
		return 100;
	}

	public int getStaminaRecoveryWarmup() {
		return 5 * 20;
	}

	public float getStaminaRecoveryMax() {
		return 1.0F;
	}
}
