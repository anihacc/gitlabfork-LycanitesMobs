package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.entity.EntityProjectileRapidFire;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class EntityCinder extends EntityCreatureTameable implements IMob, IGroupFire, IFusable {

	public float inWallDamageAbsorbed = 0;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCinder(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsInBlock = false;
        this.hasAttackSound = false;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
		this.field_70714_bg.addTask(1, new FollowFuseGoal(this).setLostDistance(16));
        this.field_70714_bg.addTask(5, new AttackRangedGoal(this).setSpeed(0.75D).setStaminaTime(100).setRange(5.0F).setMinChaseDistance(2.0F));
        this.field_70714_bg.addTask(6, this.aiSit);
        this.field_70714_bg.addTask(7, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(8, new WanderGoal(this));
        this.field_70714_bg.addTask(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new LookIdleGoal(this));

        this.field_70715_bh.addTask(0, new OwnerRevengeTargetingGoal(this));
        this.field_70715_bh.addTask(1, new OwnerAttackTargetingGoal(this));
        this.field_70715_bh.addTask(2, new RevengeTargetingGoal(this));
        this.field_70715_bh.addTask(2, new AttackTargetingGoal(this).setTargetClass(IGroupIce.class));
        this.field_70715_bh.addTask(2, new AttackTargetingGoal(this).setTargetClass(IGroupWater.class));
        this.field_70715_bh.addTask(2, new AttackTargetingGoal(this).setTargetClass(EntitySnowman.class));
        this.field_70715_bh.addTask(3, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(3, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(IGroupPlant.class));
        this.field_70715_bh.addTask(6, new OwnerDefenseTargetingGoal(this));
		this.field_70715_bh.addTask(7, new FuseTargetingGoal(this));
    }
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

        // Suffocation Transform:
		if(!this.getEntityWorld().isRemote) {
			if(this.inWallDamageAbsorbed >= 10) {
				this.transform(EntityVolcan.class, null, false);
			}
		}
        
        // Particles:
        if(this.getEntityWorld().isRemote) {
			for (int i = 0; i < 2; ++i) {
				this.getEntityWorld().addParticle(ParticleTypes.SMOKE_LARGE, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);
				this.getEntityWorld().addParticle(ParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);
			}
		}
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(IGroupFire.class))
    		return false;
        return super.canAttackClass(targetClass);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
    	// Type:
    	List<EntityProjectileRapidFire> projectiles = new ArrayList<>();
		ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("ember");
    	
    	EntityProjectileRapidFire projectileEntry = new EntityProjectileRapidFire(projectileInfo, this.getEntityWorld(), this, 15, 3);
    	projectiles.add(projectileEntry);
    	
    	EntityProjectileRapidFire projectileEntry2 = new EntityProjectileRapidFire(projectileInfo, this.getEntityWorld(), this, 15, 3);
    	projectileEntry2.offsetX += 1.0D;
    	projectiles.add(projectileEntry2);
    	
    	EntityProjectileRapidFire projectileEntry3 = new EntityProjectileRapidFire(projectileInfo, this.getEntityWorld(), this, 15, 3);
    	projectileEntry3.offsetX -= 1.0D;
    	projectiles.add(projectileEntry3);
    	
    	EntityProjectileRapidFire projectileEntry4 = new EntityProjectileRapidFire(projectileInfo, this.getEntityWorld(), this, 15, 3);
    	projectileEntry4.offsetZ += 1.0D;
    	projectiles.add(projectileEntry4);
    	
    	EntityProjectileRapidFire projectileEntry5 = new EntityProjectileRapidFire(projectileInfo, this.getEntityWorld(), this, 15, 3);
    	projectileEntry5.offsetZ -= 1.0D;
    	projectiles.add(projectileEntry5);
    	
    	EntityProjectileRapidFire projectileEntry6 = new EntityProjectileRapidFire(projectileInfo, this.getEntityWorld(), this, 15, 3);
    	projectileEntry6.offsetY += 1.0D;
    	projectiles.add(projectileEntry6);
    	
    	EntityProjectileRapidFire projectileEntry7 = new EntityProjectileRapidFire(projectileInfo, this.getEntityWorld(), this, 15, 3);
    	projectileEntry7.offsetY -= 1.0D;
    	projectiles.add(projectileEntry7);
    	
    	for(EntityProjectileRapidFire projectile : projectiles) {
	        projectile.setProjectileScale(1f);
	    	
	    	// Y Offset:
	    	projectile.posY -= this.height / 4;
	    	
	    	// Accuracy:
	    	float accuracy = 1.0F * (this.getRNG().nextFloat() - 0.5F);
	    	
	    	// Set Velocities:
	        double d0 = target.posX - this.posX + accuracy;
	        double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + accuracy;
	        double d2 = target.posZ - this.posZ + accuracy;
	        float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
	        float velocity = 0.6F;
	        projectile.shoot(d0, d1 + (double)f1, d2, velocity, 6.0F);
	        
	        // Launch:
	        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	        this.getEntityWorld().func_217376_c(projectile);
    	}
    	
        super.attackRanged(target, range);
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
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
   	//                     Immunities
   	// ==================================================
	@Override
	public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
		/*if(type.equals("inWall")) {
			this.inWallDamageAbsorbed += damage;
			return false;
		}*/
		return super.isInvulnerableTo(type, source, damage);
	}
    
    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return true; }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFireDamage())
    		return 0F;
    	else return super.getDamageModifier(damageSrc);
    }
    
    
    // ==================================================
   	//                       Drops
   	// ==================================================
    // ========== Apply Drop Effects ==========
    /** Used to add effects or alter the dropped entity item. **/
    @Override
    public void applyDropEffects(EntityItemCustom entityitem) {
    	entityitem.setCanBurn(false);
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    @Override
    public float getBrightness() {
        return 1.0F;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }


	// ==================================================
	//                      Fusion
	// ==================================================
	protected IFusable fusionTarget;

	@Override
	public IFusable getFusionTarget() {
		return this.fusionTarget;
	}

	@Override
	public void setFusionTarget(IFusable fusionTarget) {
		this.fusionTarget = fusionTarget;
	}

	@Override
	public Class getFusionClass(IFusable fusable) {
		if(fusable instanceof EntityJengu) {
			return EntityXaphan.class;
		}
		if(fusable instanceof EntityGeonach) {
			return EntityVolcan.class;
		}
		if(fusable instanceof EntityDjinn) {
			return EntityZephyr.class;
		}
		if(fusable instanceof EntityAegis) {
			return EntityWisp.class;
		}
		if(fusable instanceof EntityArgus) {
			return EntityGrue.class;
		}
		return null;
	}
}
