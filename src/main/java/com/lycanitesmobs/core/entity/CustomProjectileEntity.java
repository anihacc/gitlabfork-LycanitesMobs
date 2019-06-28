package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.core.info.projectile.behaviours.ProjectileBehaviour;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CustomProjectileEntity extends BaseProjectileEntity {
	/** Used to sync the Projectile Info's name to use. **/
	protected static final DataParameter<String> PROJECTILE_NAME = EntityDataManager.createKey(CustomProjectileEntity.class, DataSerializers.STRING);

	/** The Projectile Info to base this projectile from. **/
	public ProjectileInfo projectileInfo;


	// ==================================================
	//                   Constructors
	// ==================================================
	public CustomProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world) {
		super(entityType, world);
		this.modInfo = LycanitesMobs.modInfo;
	}

	public CustomProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world, ProjectileInfo projectileInfo) {
		super(entityType, world);
		this.modInfo = LycanitesMobs.modInfo;
		this.setProjectileInfo(projectileInfo);
	}

	public CustomProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity entityLiving, ProjectileInfo projectileInfo) {
		super(entityType, world, entityLiving);
		if(projectileInfo != null)
			this.shoot(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0.0F, (float)projectileInfo.velocity, 1.0F);
		this.modInfo = LycanitesMobs.modInfo;
		this.setProjectileInfo(projectileInfo);
	}

	public CustomProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world, double x, double y, double z, ProjectileInfo projectileInfo) {
		super(entityType, world, x, y, z);
		this.modInfo = LycanitesMobs.modInfo;
		this.setProjectileInfo(projectileInfo);
	}

	@Override
	public EntityType getType() {
		return this.projectileInfo.getEntityType();
	}

	@Override
	public void registerData() {
		super.registerData();
		this.dataManager.register(PROJECTILE_NAME, "");
	}


	// ==================================================
	//                Projectile Custom
	// ==================================================
	/**
	 * Loads the projectile info for this projectile to use.
	 * @param projectileName The name of the projectile info to use.
	 */
	public void loadProjectileInfo(String projectileName) {
		this.setProjectileInfo(ProjectileManager.getInstance().getProjectile(projectileName));
	}

	/**
	 * Sets the projectile info for this projectile to use.
	 * @param projectileInfo The projectile info to use.
	 */
	public void setProjectileInfo(ProjectileInfo projectileInfo) {
		this.projectileInfo = projectileInfo;
		if(this.projectileInfo == null) {
			return;
		}
		if(!this.getEntityWorld().isRemote) {
			this.dataManager.set(PROJECTILE_NAME, this.projectileInfo.getName());
		}
		this.modInfo = this.projectileInfo.modInfo;
		this.entityName = this.projectileInfo.getName();

		// Stats:
		this.setProjectileScale(this.projectileInfo.scale);
		//this.setSize(this.projectileInfo.width * this.projectileScale, this.projectileInfo.height * this.projectileScale); TODO EntityType dynamic sizes.
		this.projectileLife = this.projectileInfo.lifetime;
		this.setDamage(this.projectileInfo.damage);
		this.setPierce(this.projectileInfo.pierce);
		this.knockbackChance = this.projectileInfo.knockbackChance;
		this.weight = this.projectileInfo.weight;

		// Flags:
		this.waterProof = this.projectileInfo.waterproof;
		this.lavaProof = this.projectileInfo.lavaproof;
		this.cutsGrass = this.projectileInfo.cutGrass;
		this.ripper = this.projectileInfo.ripper;
		this.pierceBlocks = this.projectileInfo.pierceBlocks;
	}


	// ==================================================
	//                      Update
	// ==================================================
	@Override
	public void tick() {
		if(this.projectileInfo == null && this.getEntityWorld().isRemote) {
			this.loadProjectileInfo(this.getStringFromDataManager(PROJECTILE_NAME));
		}

		super.tick();

		if(this.projectileInfo != null && !this.getEntityWorld().isRemote) {
			for (ProjectileBehaviour behaviour : this.projectileInfo.behaviours) {
				behaviour.onProjectileUpdate(this);
			}
		}
	}


	// ==================================================
	//                      Projectile
	// ==================================================
	@Override
	public void onDamage(LivingEntity target, float damage, boolean attackSuccess) {
		super.onDamage(target, damage, attackSuccess);
		if(!this.getEntityWorld().isRemote && attackSuccess && this.projectileInfo != null) {
			for(ElementInfo element : this.projectileInfo.elements) {
				element.debuffEntity(target, this.projectileInfo.effectDuration * 20, this.projectileInfo.effectAmplifier);
			}
		}
	}

	@Override
	public void onImpactComplete(BlockPos impactPos) {
		super.onImpactComplete(impactPos);
		if(this.projectileInfo == null) {
			return;
		}

		for(ProjectileBehaviour behaviour : this.projectileInfo.behaviours) {
			behaviour.onProjectileImpact(this, this.getEntityWorld(), impactPos);
		}
	}


	// ==================================================
	//                       NBT
	// ==================================================
	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);

		if(this.projectileInfo != null) {
			compound.putString("ProjectileName", this.projectileInfo.getName());
		}
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);

		if(compound.contains("ProjectileName")) {
			this.loadProjectileInfo(compound.getString("ProjectileName"));
		}
	}


	// ==================================================
	//                      Visuals
	// ==================================================
	@Override
	public String getTextureName() {
		if(this.projectileInfo != null) {
			return this.projectileInfo.chargeItemName;
		}
		return this.entityName;
	}

	@Override
	public float getBrightness() {
		if(this.projectileInfo == null || !this.projectileInfo.glow)
			return super.getBrightness();
		return 1.0F;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getBrightnessForRender() {
		if(this.projectileInfo == null || !this.projectileInfo.glow)
			return super.getBrightnessForRender();
		return 15728880;
	}


	// ==================================================
	//                      Sounds
	// ==================================================
	public SoundEvent getLaunchSound() {
		if(this.projectileInfo != null) {
			return this.projectileInfo.getLaunchSound();
		}
		return super.getLaunchSound();
	}

	public SoundEvent getImpactSound() {
		if(this.projectileInfo != null) {
			return this.projectileInfo.getImpactSound();
		}
		return super.getImpactSound();
	}
}
