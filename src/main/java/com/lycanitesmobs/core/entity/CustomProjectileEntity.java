package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.core.info.projectile.behaviours.ProjectileBehaviour;
import com.lycanitesmobs.core.info.projectile.behaviours.ProjectileBehaviourLaser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class CustomProjectileEntity extends BaseProjectileEntity {
	/** The Projectile Info to base this projectile from. **/
	public ProjectileInfo projectileInfo;

	/** The id of this projectile's thrower if any, used for network sync by some behaviours, -1 for none. **/
	protected int throwerId = -1;

	/** The projectile that fired this projectile, if any. **/
	protected BaseProjectileEntity parent;

	/** TThe target of this projectile if any, used for network sync by some behaviours. **/
	protected Entity target;

	/** The id of the projectile that fired this projectile if any, used for network sync by some behaviours, -1 for none. **/
	protected int parentId = -1;

	/** The id of the target entity of this projectile, used by some behaviours. **/
	protected int targetId = -1;

	/** Used by laser behaviours to keep track of the laser ending position. **/
	protected Vec3d laserEnd;

	/** The width of this projectile's laser, used by laser behaviours. **/
	public float laserWidth;

	/** The angle to fire a laser from where there is no entity aiming the laser, used by laser behaviours. **/
	public float laserAngle;

	/** A list of projectiles that was spawned by this projectile, used by behaviours. **/
	public List<BaseProjectileEntity> spawnedProjectiles = new ArrayList<>();

	// Data Parameters:
	protected static final DataParameter<String> PROJECTILE_NAME = EntityDataManager.createKey(CustomProjectileEntity.class, DataSerializers.STRING);
	protected static final DataParameter<Integer> THROWING_ENTITY_ID = EntityDataManager.createKey(CustomProjectileEntity.class, DataSerializers.VARINT);
	protected static final DataParameter<Integer> PARENT_PROJECTILE_ID = EntityDataManager.createKey(CustomProjectileEntity.class, DataSerializers.VARINT);
	protected static final DataParameter<Integer> TARGET_ID = EntityDataManager.createKey(CustomProjectileEntity.class, DataSerializers.VARINT);
	protected static final DataParameter<Float> LASER_ANGLE = EntityDataManager.createKey(CustomProjectileEntity.class, DataSerializers.FLOAT);



	// ==================================================
	//                   Constructors
	// ==================================================
	public CustomProjectileEntity(World world) {
		super(world);
		this.dataManager.register(PROJECTILE_NAME, "");
		this.modInfo = LycanitesMobs.modInfo;
	}

	public CustomProjectileEntity(World world, ProjectileInfo projectileInfo) {
		super(world);
		this.dataManager.register(PROJECTILE_NAME, "");
		this.modInfo = LycanitesMobs.modInfo;
		this.setProjectileInfo(projectileInfo);
	}

	public CustomProjectileEntity(World world, EntityLivingBase entityLiving, ProjectileInfo projectileInfo) {
		super(world, entityLiving);
		if(projectileInfo != null)
			this.shoot(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0.0F, (float)projectileInfo.velocity, 1.0F);
		this.dataManager.register(PROJECTILE_NAME, "");
		this.modInfo = LycanitesMobs.modInfo;
		this.setProjectileInfo(projectileInfo);
	}

	public CustomProjectileEntity(World world, double x, double y, double z, ProjectileInfo projectileInfo) {
		super(world, x, y, z);
		this.dataManager.register(PROJECTILE_NAME, "");
		this.modInfo = LycanitesMobs.modInfo;
		this.setProjectileInfo(projectileInfo);
	}

	@Override
	public void setup() {
		super.setup();
		this.dataManager.register(THROWING_ENTITY_ID, this.throwerId);
		this.dataManager.register(PARENT_PROJECTILE_ID, this.parentId);
		this.dataManager.register(TARGET_ID, this.targetId);
		this.dataManager.register(LASER_ANGLE, this.laserAngle);
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
		this.setSize(this.projectileInfo.width, this.projectileInfo.height);
		this.projectileLife = this.projectileInfo.lifetime;
		this.setDamage(this.projectileInfo.damage);
		this.setPierce(this.projectileInfo.pierce);
		this.knockbackChance = this.projectileInfo.knockbackChance;
		this.weight = this.projectileInfo.weight;

		// Visual:
		this.rollSpeed = this.projectileInfo.rollSpeed;
		if(this.rollSpeed > 0 && this.rand.nextBoolean()) {
			this.rollSpeed = -this.rollSpeed;
		}

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
	public void onUpdate() {
		if(this.projectileInfo == null) {
			this.loadProjectileInfo(this.getStringFromDataManager(PROJECTILE_NAME));
		}

		super.onUpdate();

		if(this.projectileInfo != null) {
			for(ProjectileBehaviour behaviour : this.projectileInfo.behaviours) {
				behaviour.onProjectileUpdate(this);
			}
		}
	}

	/**
	 * Syncs the Throwing Entity from server to client.
	 */
	public void syncThrower() {
		if(!this.getEntityWorld().isRemote) {
			this.throwerId = this.getThrower() != null ? this.getThrower().getEntityId() : -1;
			this.dataManager.set(THROWING_ENTITY_ID, this.throwerId);
		}
		else {
			this.throwerId = this.dataManager.get(THROWING_ENTITY_ID);
			if(this.throwerId == -1) {
				this.thrower = null;
			}
			else if(this.getThrower() == null || this.getThrower().getEntityId() != this.throwerId) {
				Entity possibleThrower = this.getEntityWorld().getEntityByID(this.throwerId);
				if(possibleThrower instanceof EntityLivingBase) {
					this.thrower = (EntityLivingBase)possibleThrower;
				}
				else {
					this.thrower = null;
				}
			}
		}
	}

	/**
	 * Returns true if this projectile should be channels where it's lifetime is reset constantly.
	 * @return True if this projectile can be channeled.
	 */
	public boolean shouldChannel() {
		if(this.projectileInfo != null) {
			for(ProjectileBehaviour behaviour : this.projectileInfo.behaviours) {
				if(behaviour instanceof ProjectileBehaviourLaser) {
					return true;
				}
			}
		}
		return false;
	}


	// ==================================================
	//                      Projectile
	// ==================================================
	@Override
	public void onDamage(EntityLivingBase target, float damage, boolean attackSuccess) {
		super.onDamage(target, damage, attackSuccess);
		if(!this.getEntityWorld().isRemote && attackSuccess && this.projectileInfo != null) {
			for(ElementInfo element : this.projectileInfo.elements) {
				element.debuffEntity(target, this.projectileInfo.effectDuration * 20, this.projectileInfo.effectAmplifier);
			}
		}

		if(attackSuccess) {
			for(ProjectileBehaviour behaviour : this.projectileInfo.behaviours) {
				behaviour.onProjectileDamage(this, this.getEntityWorld(), target, damage);
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

	/**
	 * Sets the projectile that fired this projectile.
	 */
	public void setParent(BaseProjectileEntity parent) {
		this.parent = parent;
		if(!this.getEntityWorld().isRemote) {
			this.parentId = this.parent != null ? this.parent.getEntityId() : -1;
			this.dataManager.set(PARENT_PROJECTILE_ID, this.parentId);
		}
	}

	/**
	 * Gets the projectile that fired this projectile
	 * @return The projectile that fired this projectile or null.
	 */
	public BaseProjectileEntity getParent() {
		if(this.getEntityWorld().isRemote) {
			this.parentId = this.dataManager.get(PARENT_PROJECTILE_ID);
			if(this.parentId == -1) {
				this.parent = null;
			}
			else if(this.parent == null || this.parent.getEntityId() != this.parentId) {
				Entity possibleParent = this.getEntityWorld().getEntityByID(this.parentId);
				if(possibleParent instanceof BaseProjectileEntity) {
					this.parent = (BaseProjectileEntity)possibleParent;
				}
			}
		}
		return this.parent;
	}

	/**
	 * Sets the target of this projectile, used by some behaviours.
	 */
	public void setTarget(Entity target) {
		this.target = target;
		if(!this.getEntityWorld().isRemote) {
			this.targetId = this.target != null ? this.target.getEntityId() : -1;
			this.dataManager.set(TARGET_ID, this.targetId);
		}
	}

	/**
	 * Gets the target of this projectile, used by some behaviours.
	 * @return The projectile target entity if any.
	 */
	public Entity getTarget() {
		if(this.getEntityWorld().isRemote) {
			this.targetId = this.dataManager.get(TARGET_ID);
			if(this.targetId == -1) {
				this.target = null;
			}
			else if(this.target == null || this.target.getEntityId() != this.targetId) {
				this.target = this.getEntityWorld().getEntityByID(this.targetId);
			}
		}
		return this.target;
	}

	/**
	 * Sets the laser end used by this projectile or clears it if null. Also updates the laser angle.
	 */
	public void setLaserEnd(Vec3d laserEnd) {
		this.laserEnd = laserEnd;
		if(!this.getEntityWorld().isRemote) {
			this.dataManager.set(LASER_ANGLE, this.laserAngle);
		}
	}

	/**
	 * Gets the laser end used by this projectile if any. Also updates the laser angle.
	 * @return The laser end for laser projectile behaviours.
	 */
	public Vec3d getLaserEnd() {
		if(this.getEntityWorld().isRemote) {
			this.laserAngle = this.dataManager.get(LASER_ANGLE);
		}
		return this.laserEnd;
	}


	// ==================================================
	//                       NBT
	// ==================================================
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		if(this.projectileInfo != null) {
			compound.setString("ProjectileName", this.projectileInfo.getName());
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		if(compound.hasKey("ProjectileName")) {
			this.loadProjectileInfo(compound.getString("ProjectileName"));
		}
	}


	// ==================================================
	//                      Visuals
	// ==================================================
	@Override
	public String getTextureName() {
		return this.entityName;
	}

	public ResourceLocation getBeamTexture() {
		String textureName = this.getTextureName() + "_beam";
		if(AssetManager.getTexture(textureName) == null)
			AssetManager.addTexture(textureName, this.modInfo, "textures/items/" + textureName + ".png");
		return AssetManager.getTexture(textureName);
	}

	@Override
	public float getBrightness() {
		if(this.projectileInfo == null || !this.projectileInfo.glow)
			return super.getBrightness();
		return 1.0F;
	}

	@Override
	@SideOnly(Side.CLIENT)
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
