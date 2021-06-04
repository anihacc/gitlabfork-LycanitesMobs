package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.List;

public class EntityShade extends RideableCreatureEntity {

	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityShade(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.hasJumpSound = true;
        this.canGrow = false;
        this.setupMob();

        this.stepHeight = 1.0F;
        this.attackCooldownMax = 40;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setSpeed(1.5D));
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


    // ==================================================
    //                     Movement
    // ==================================================
    // Mounted Y Offset:
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.85D;
    }

    @Override
    public double getMountedZOffset() {
        return (double)this.width * 0.25D;
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
        if(!super.attackMelee(target, damageScale))
        	return false;

        // Leech:
        float leeching = this.getEffectStrength(this.getAttackDamage(damageScale) / 4);
        this.heal(leeching);

        if(this.getRNG().nextFloat() <= 0.1F)
            this.specialAttack();
    	
        return true;
    }

    // ========== Special Attack ==========
    public void specialAttack() {
        // Horrific Howl:
        double distance = 5.0D;
        List<EntityLivingBase> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(distance, distance, distance), possibleTarget -> {
			if(!possibleTarget.isEntityAlive()
					|| possibleTarget == EntityShade.this
					|| EntityShade.this.isRidingOrBeingRiddenBy(possibleTarget)
					|| EntityShade.this.isOnSameTeam(possibleTarget)
					|| !EntityShade.this.canAttackClass(possibleTarget.getClass())
					|| !EntityShade.this.canAttackEntity(possibleTarget))
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
                    if (ObjectManager.getEffect("fear") != null)
                        possibleTarget.addPotionEffect(new PotionEffect(ObjectManager.getEffect("fear"), this.getEffectDuration(5), 1));
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
    public boolean canBeTempted() {
    	return true;
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
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
        if(type.equals("inWall")) return false;
        return super.isDamageTypeApplicable(type, source, damage);
    }

    @Override
    public float getFallResistance() {
        return 10;
    }
}
