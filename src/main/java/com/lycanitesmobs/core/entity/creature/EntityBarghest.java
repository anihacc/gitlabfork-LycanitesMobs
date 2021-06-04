package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.List;

public class EntityBarghest extends RideableCreatureEntity {

    protected boolean leapedAbilityQueued = false;
    protected boolean leapedAbilityReady = false;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityBarghest(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
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
		this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(EntityPlayer.class).setLongMemory(false));
		this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Random Leaping:
        if(!this.isTamed() && this.onGround && !this.getEntityWorld().isRemote) {
        	if(this.hasAttackTarget()) {
        		if(this.rand.nextInt(10) == 0)
        			this.leap(4.0F, 0.5D, this.getAttackTarget());
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
            List<EntityLivingBase> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(distance, distance, distance), possibleTarget -> {
				if (!possibleTarget.isEntityAlive()
						|| possibleTarget == EntityBarghest.this
						|| EntityBarghest.this.isRidingOrBeingRiddenBy(possibleTarget)
						|| EntityBarghest.this.isOnSameTeam(possibleTarget)
						|| !EntityBarghest.this.canAttackClass(possibleTarget.getClass())
						|| !EntityBarghest.this.canAttackEntity(possibleTarget))
					return false;

				return true;
			});
            if(!possibleTargets.isEmpty()) {
                for(EntityLivingBase possibleTarget : possibleTargets) {
                    boolean doDamage = true;
                    if(this.getRider() instanceof EntityPlayer) {
                        if(MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((EntityPlayer)this.getRider(), possibleTarget))) {
                            doDamage = false;
                        }
                    }
                    if(doDamage) {
                        if (ObjectManager.getEffect("weight") != null)
                            possibleTarget.addPotionEffect(new PotionEffect(ObjectManager.getEffect("weight"), this.getEffectDuration(5), 1));
                        else
                            possibleTarget.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 10 * 20, 0));
                    }
                }
            }
            this.playAttackSound();
        }
    }
    
    public void riderEffects(EntityLivingBase rider) {
    	if(rider.isPotionActive(MobEffects.SLOWNESS))
    		rider.removePotionEffect(MobEffects.SLOWNESS);
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
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }




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
    	return 20;
    }
}
