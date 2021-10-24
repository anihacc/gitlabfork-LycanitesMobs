package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.CustomItemEntity;
import com.lycanitesmobs.core.entity.RapidFireProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Cinder extends TameableCreatureEntity implements Enemy, IFusable {

	public float inWallDamageAbsorbed = 0;
    
    public Cinder(EntityType<? extends Cinder> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.spawnsInBlock = false;
        this.hasAttackSound = false;
        this.setupMob();

        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setStaminaTime(100).setRange(5.0F).setMinChaseDistance(2.0F));
    }

	@Override
    public void aiStep() {
        super.aiStep();
		if(!this.getCommandSenderWorld().isClientSide) {
			if(this.inWallDamageAbsorbed >= 10) {
				this.transform(CreatureManager.getInstance().getEntityType("volcan"), null, false);
			}
		}

        if(this.getCommandSenderWorld().isClientSide) {
			for (int i = 0; i < 2; ++i) {
				this.getCommandSenderWorld().addParticle(ParticleTypes.LARGE_SMOKE, this.position().x() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double) this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
				this.getCommandSenderWorld().addParticle(ParticleTypes.FLAME, this.position().x() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double) this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
			}
		}
    }

    @Override
    public void attackRanged(Entity target, float range) {
    	List<RapidFireProjectileEntity> projectiles = new ArrayList<>();
		ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("ember");
    	
    	RapidFireProjectileEntity projectileEntry = new RapidFireProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(RapidFireProjectileEntity.class), projectileInfo, this.getCommandSenderWorld(), this, 15, 3);
    	projectiles.add(projectileEntry);
    	
    	RapidFireProjectileEntity projectileEntry2 = new RapidFireProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(RapidFireProjectileEntity.class), projectileInfo, this.getCommandSenderWorld(), this, 15, 3);
    	projectileEntry2.offsetX += 1.0D;
    	projectiles.add(projectileEntry2);
    	
    	RapidFireProjectileEntity projectileEntry3 = new RapidFireProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(RapidFireProjectileEntity.class), projectileInfo, this.getCommandSenderWorld(), this, 15, 3);
    	projectileEntry3.offsetX -= 1.0D;
    	projectiles.add(projectileEntry3);
    	
    	RapidFireProjectileEntity projectileEntry4 = new RapidFireProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(RapidFireProjectileEntity.class), projectileInfo, this.getCommandSenderWorld(), this, 15, 3);
    	projectileEntry4.offsetZ += 1.0D;
    	projectiles.add(projectileEntry4);
    	
    	RapidFireProjectileEntity projectileEntry5 = new RapidFireProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(RapidFireProjectileEntity.class), projectileInfo, this.getCommandSenderWorld(), this, 15, 3);
    	projectileEntry5.offsetZ -= 1.0D;
    	projectiles.add(projectileEntry5);
    	
    	RapidFireProjectileEntity projectileEntry6 = new RapidFireProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(RapidFireProjectileEntity.class), projectileInfo, this.getCommandSenderWorld(), this, 15, 3);
    	projectileEntry6.offsetY += 1.0D;
    	projectiles.add(projectileEntry6);
    	
    	RapidFireProjectileEntity projectileEntry7 = new RapidFireProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(RapidFireProjectileEntity.class), projectileInfo, this.getCommandSenderWorld(), this, 15, 3);
    	projectileEntry7.offsetY -= 1.0D;
    	projectiles.add(projectileEntry7);
    	
    	for(RapidFireProjectileEntity projectile : projectiles) {
	        projectile.setProjectileScale(1f);

			projectile.setPos(
					projectile.position().x(),
					projectile.position().y() - this.getDimensions(Pose.STANDING).height / 4,
					projectile.position().z()
			);

	    	float accuracy = 1.0F * (this.getRandom().nextFloat() - 0.5F);

	        double d0 = target.position().x() - this.position().x() + accuracy;
	        double d1 = target.position().y() + (double)target.getEyeHeight() - 1.100000023841858D - projectile.position().y() + accuracy;
	        double d2 = target.position().z() - this.position().z() + accuracy;
	        float f1 = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.2F;
	        float velocity = 0.6F;
	        projectile.shoot(d0, d1 + (double)f1, d2, velocity, 6.0F);

	        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
	        this.getCommandSenderWorld().addFreshEntity(projectile);
    	}
    	
        super.attackRanged(target, range);
    }

    @Override
    public boolean isFlying() { return true; }

    public boolean petControlsEnabled() { return true; }


	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.bagSize; }
    

	@Override
	public boolean isVulnerableTo(String type, DamageSource source, float damage) {
		/*if(type.equals("inWall")) {
			this.inWallDamageAbsorbed += damage;
			return false;
		}*/
		return super.isVulnerableTo(type, source, damage);
	}
    
    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return true; }
    
    

    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFire())
    		return 0F;
    	else return super.getDamageModifier(damageSrc);
    }

    @Override
    public void applyDropEffects(CustomItemEntity entityitem) {
    	entityitem.setCanBurn(false);
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }

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
	public EntityType<? extends LivingEntity> getFusionType(IFusable fusable) {
		if(fusable instanceof Jengu) {
			return CreatureManager.getInstance().getEntityType("xaphan");
		}
		if(fusable instanceof Geonach) {
			return CreatureManager.getInstance().getEntityType("volcan");
		}
		if(fusable instanceof Zephyr) {
			return CreatureManager.getInstance().getEntityType("raidra");
		}
		if(fusable instanceof Aegis) {
			return CreatureManager.getInstance().getEntityType("wisp");
		}
		if(fusable instanceof Argus) {
			return CreatureManager.getInstance().getEntityType("grue");
		}
		return null;
	}
}
