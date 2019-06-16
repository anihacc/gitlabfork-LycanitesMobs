package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.entity.projectile.EntityArcaneLaserStorm;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class EntityBeholder extends EntityCreatureRideable {
	public boolean beholderGreifing = true;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityBeholder(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;
        
        this.setAttackCooldownMax(20);
		this.beholderGreifing = ConfigBase.getConfig(this.creatureInfo.modInfo, "general").getBool("Features", "Beholder Griefing", this.beholderGreifing, "Set to false to disable Beholder projectile explosions.");
		this.solidCollision = true;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(2, new EntityAIPlayerControl(this));
        this.field_70714_bg.addTask(4, new EntityAITempt(this).setTemptDistanceMin(4.0D));
        this.field_70714_bg.addTask(5, new EntityAIAttackRanged(this).setSpeed(0.25D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
		this.field_70714_bg.addTask(6, this.aiSit);
		this.field_70714_bg.addTask(7, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(8, new EntityAIWander(this).setPauseRate(30));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetOwnerAttack(this));
		this.field_70715_bh.addTask(2, new EntityAITargetOwnerRevenge(this));
		this.field_70715_bh.addTask(3, new EntityAITargetOwnerAttack(this));
		this.field_70715_bh.addTask(4, new EntityAITargetOwnerThreats(this));
        this.field_70715_bh.addTask(5, new EntityAITargetRevenge(this));
        this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(6, new EntityAITargetOwnerThreats(this));
    }


    // ==================================================
    //                      Updates
    // ==================================================
    @Override
    public void riderEffects(LivingEntity rider) {
        if(rider.isPotionActive(MobEffects.MINING_FATIGUE))
            rider.removeEffectInstance(MobEffects.MINING_FATIGUE);
        if(rider.isPotionActive(ObjectManager.getEffect("weight")))
            rider.removeEffectInstance(ObjectManager.getEffect("weight"));
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
        		List<Potion> goodEffects = new ArrayList<Potion>();
        		for(Object potionEffectObj : targetLiving.getActiveEffectInstances()) {
        			if(potionEffectObj instanceof EffectInstance) {
        				Potion potion = ((EffectInstance)potionEffectObj).getPotion();
                        if(potion != null) {
                            if(ObjectLists.inEffectList("buffs", potion))
                                goodEffects.add(potion);
                        }
        			}
        		}
        		if(goodEffects.size() > 0 && this.getRNG().nextBoolean()) {
        			if(goodEffects.size() > 1)
        				targetLiving.removeEffectInstance(goodEffects.get(this.getRNG().nextInt(goodEffects.size())));
        			else
        				targetLiving.removeEffectInstance(goodEffects.get(0));
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

        if(rider instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)rider;
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
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public int getBrightnessForRender() {
        if(isAttackOnCooldown())
        	return 15728880;
        else
        	return super.getBrightnessForRender();
    }
}