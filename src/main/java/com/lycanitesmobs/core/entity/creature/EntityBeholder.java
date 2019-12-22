package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.projectile.EntityArcaneLaserStorm;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class EntityBeholder extends RideableCreatureEntity {
	public boolean griefing = true;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityBeholder(EntityType<? extends EntityBeholder> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;
        
        this.setAttackCooldownMax(20);
		this.solidCollision = true;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.25D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
    }

	@Override
	public void loadCreatureFlags() {
		this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
	}


    // ==================================================
    //                      Updates
    // ==================================================
    @Override
    public void riderEffects(LivingEntity rider) {
        if(rider.isPotionActive(Effects.MINING_FATIGUE))
            rider.removePotionEffect(Effects.MINING_FATIGUE);
        if(rider.isPotionActive(ObjectManager.getEffect("weight")))
            rider.removePotionEffect(ObjectManager.getEffect("weight"));
    }

    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== On Damage ==========
    /** Called when this mob has received damage. **/
    public void onDamage(DamageSource damageSrc, float damage) {
    	super.onDamage(damageSrc, damage);
    	
    	Entity damageEntity = damageSrc.getTrueSource();
    	if(damageEntity != null && ("mob".equals(damageSrc.damageType) || "player".equals(damageSrc.damageType))) {
    		
    		// Eat Buffs:
        	if(damageEntity instanceof LivingEntity) {
        		LivingEntity targetLiving = (LivingEntity)damageEntity;
        		List<Effect> goodEffects = new ArrayList<>();
        		for(EffectInstance effect : targetLiving.getActivePotionEffects()) {
					if(ObjectLists.inEffectList("buffs", effect.getPotion()))
						goodEffects.add(effect.getPotion());
        		}
        		if(goodEffects.size() > 0 && this.getRNG().nextBoolean()) {
        			if(goodEffects.size() > 1)
        				targetLiving.removePotionEffect(goodEffects.get(this.getRNG().nextInt(goodEffects.size())));
        			else
        				targetLiving.removePotionEffect(goodEffects.get(0));
    		    	float leeching = damage * 1.1F;
    		    	this.heal(leeching);
        		}
        	}
    	}
    }
	
	
	// ==================================================
  	//                     Abilities
  	// ==================================================
    // ========== Movement ==========
    public boolean isFlying() { return true; }
    
    
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
   	//                      Attacks
   	// ==================================================
	// ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile(EntityArcaneLaserStorm.class, target, range, 0, new Vec3d(0, 0, 0), 0.6f, 2f, 1F);
        super.attackRanged(target, range);
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isInvulnerableTo(Entity entity) {
    	if(entity instanceof EntityBeholder)
    		return false;
    	return super.isInvulnerableTo(entity);
    }
    
    @Override
    public boolean canBurn() { return false; }


    // ==================================================
    //                      Movement
    // ==================================================
    @Override
    public double getMountedYOffset() {
        return (double)this.getSize(Pose.STANDING).height * 0.9D;
    }

	@Override
	public double getMountedZOffset() {
		return (double)this.getSize(Pose.STANDING).width * -0.2D;
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

        if(this.hasPickupEntity()) {
            this.dropPickupEntity();
            return;
        }

        if(this.getStamina() < this.getStaminaCost())
            return;

        if(rider instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)rider;
            EntityArcaneLaserStorm projectile = new EntityArcaneLaserStorm(ProjectileManager.getInstance().oldProjectileTypes.get(EntityArcaneLaserStorm.class), this.getEntityWorld(), player);
            this.getEntityWorld().addEntity(projectile);
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.triggerAttackCooldown();
        }

        this.applyStaminaCost();
    }

    public float getStaminaCost() {
        return 10;
    }

    public int getStaminaRecoveryWarmup() {
        return 2 * 20;
    }

    public float getStaminaRecoveryMax() {
        return 1.0F;
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    @Override
    public float getBrightness() {
        if(isAttackOnCooldown())
        	return 1.0F;
        else
        	return super.getBrightness();
    }
}
