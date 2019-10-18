package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.projectile.EntityArcaneLaserStorm;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class EntityBeholder extends RideableCreatureEntity {
	public boolean beholderGreifing = true; // TODO Creature flags.
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityBeholder(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;
        
        this.setAttackCooldownMax(20);
		this.solidCollision = true;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.25D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
    }


    // ==================================================
    //                      Updates
    // ==================================================
    @Override
    public void riderEffects(EntityLivingBase rider) {
        if(rider.isPotionActive(MobEffects.MINING_FATIGUE))
            rider.removePotionEffect(MobEffects.MINING_FATIGUE);
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
        	if(damageEntity instanceof EntityLivingBase) {
        		EntityLivingBase targetLiving = (EntityLivingBase)damageEntity;
        		List<Potion> goodEffects = new ArrayList<>();
        		for(PotionEffect effect : targetLiving.getActivePotionEffects()) {
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
    public boolean isDamageEntityApplicable(Entity entity) {
    	if(entity instanceof EntityBeholder)
    		return false;
    	return super.isDamageEntityApplicable(entity);
    }
    
    @Override
    public boolean canBurn() { return false; }


    // ==================================================
    //                      Movement
    // ==================================================
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.9D;
    }

	@Override
	public double getMountedZOffset() {
		return (double)this.width * -0.2D;
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

        if(rider instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)rider;
            EntityArcaneLaserStorm projectile = new EntityArcaneLaserStorm(this.getEntityWorld(), player);
            this.getEntityWorld().spawnEntity(projectile);
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
    
    @SideOnly(Side.CLIENT)
    @Override
    public int getBrightnessForRender() {
        if(isAttackOnCooldown())
        	return 15728880;
        else
        	return super.getBrightnessForRender();
    }
}
