package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;
import java.util.HashSet;

public class LaserProjectileEntity extends BaseProjectileEntity {
	// Properties:
	public LivingEntity shootingEntity;
    /** The entity that this laser should appear from. **/
	public Entity followEntity;
	public int shootingEntityRef = -1;
	public int shootingEntityID = 11;
	
	public float projectileWidth = 0.2f;
	public float projectileHeight = 0.2f;
	
	// Laser:
	public LaserEndProjectileEntity laserEnd;
	public int laserEndRef = -1;
	public int laserEndID = 12;
	
	public int laserTime = 100;
	public int laserDelay = 20;
	public float laserRange;
	public float laserWidth;
	public float laserLength = 10;
	public int laserTimeID = 13;

	// Laser End:
    /** If true, this entity will use the attack target position of the entity that has fired this if possible. **/
    public boolean useEntityAttackTarget = true;
	private double targetX;
	private double targetY;
	private double targetZ;
	
	// Offsets:
	public double offsetX = 0;
	public double offsetY = 0;
	public double offsetZ = 0;
	public int offsetIDStart = 14;

    // Data Parameters:
    protected static final DataParameter<Integer> SHOOTING_ENTITY_ID = EntityDataManager.createKey(LaserProjectileEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> LASER_END_ID = EntityDataManager.createKey(LaserProjectileEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> LASER_TIME = EntityDataManager.createKey(LaserProjectileEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Float> OFFSET_X = EntityDataManager.createKey(LaserProjectileEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> OFFSET_Y = EntityDataManager.createKey(LaserProjectileEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> OFFSET_Z = EntityDataManager.createKey(LaserProjectileEntity.class, DataSerializers.FLOAT);
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public LaserProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setStats();
        this.setTime(0);
    }

    public LaserProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6, int setTime, int setDelay) {
        super(entityType, world, par2, par4, par6);
        this.laserTime = setTime;
        this.laserDelay = setDelay;
        this.setStats();
    }

    public LaserProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6, int setTime, int setDelay, Entity followEntity) {
        this(entityType, world, par2, par4, par6, setTime, setDelay);
        this.followEntity = followEntity;
    }

    public LaserProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity par2LivingEntity, int setTime, int setDelay) {
        this(entityType, world, par2LivingEntity, setTime, setDelay, null);
    }

    public LaserProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity entityLiving, int setTime, int setDelay, Entity followEntity) {
        super(entityType, world, entityLiving);
        this.shootingEntity = entityLiving;
        this.laserTime = setTime;
        this.laserDelay = setDelay;
        this.setStats();
        this.followEntity = followEntity;
        this.syncOffset();
    }
    
    public void setStats() {
        //this.setSize(projectileWidth, projectileHeight);
        this.setRange(16.0F);
        this.setLaserWidth(1.0F);
        this.knockbackChance = 0D;
        this.targetX = this.getPositionVec().getX();
        this.targetY = this.getPositionVec().getY();
        this.targetZ = this.getPositionVec().getZ();
        this.dataManager.register(SHOOTING_ENTITY_ID, this.shootingEntityRef);
        this.dataManager.register(LASER_END_ID, this.laserEndRef);
        this.dataManager.register(LASER_TIME, this.laserTime);
        this.dataManager.register(OFFSET_X, (float) this.offsetX);
        this.dataManager.register(OFFSET_Y, (float) this.offsetY);
        this.dataManager.register(OFFSET_Z, (float) this.offsetZ);
        this.noClip = true;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if(this.laserEnd == null)
            return super.getRenderBoundingBox();
        double distance = this.getDistance(this.laserEnd);
        return super.getRenderBoundingBox().expand(distance, distance, distance);
    }
	
    
    // ==================================================
 	//                   Properties
 	// ==================================================
	public void setOffset(double x, double y, double z) {
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
		this.syncOffset();
	}
    
    
    // ==================================================
 	//                      Update
 	// ==================================================
    @Override
    public void tick() {
    	if(!this.getEntityWorld().isRemote) {
    		this.dataManager.set(LASER_TIME, this.laserTime);
    	}
    	else {
    		this.laserTime = this.dataManager.get(LASER_TIME);
    	}
    	this.syncShootingEntity();
    	
    	//this.syncOffset(); Broken? :(
    	if(!this.getEntityWorld().isRemote && this.shootingEntity != null) {
    		Entity entityToFollow = this.shootingEntity;
    		if(this.followEntity != null)
    			entityToFollow = this.followEntity;
    		double xPos = entityToFollow.getPositionVec().getX() + this.offsetX;
			double yPos = entityToFollow.getPositionVec().getY() + (this.getSize(Pose.STANDING).height / 2) + this.offsetY;
			double zPos = entityToFollow.getPositionVec().getZ() + this.offsetZ;
    		if(entityToFollow instanceof BaseCreatureEntity) {
				BaseCreatureEntity creatureToFollow = (BaseCreatureEntity)entityToFollow;
				xPos = creatureToFollow.getFacingPosition(creatureToFollow, this.offsetX, creatureToFollow.rotationYaw + 90F).getX();
				zPos = creatureToFollow.getFacingPosition(creatureToFollow, this.offsetZ, creatureToFollow.rotationYaw).getZ();
			}
    		this.setPosition(xPos, yPos, zPos);
    	}
    	
    	if(this.laserTime > 0) {
	    	this.updateEnd();
	    	this.laserTime--;
            double minX;
            double maxX;
            double minY;
            double maxY;
            double minZ;
            double maxZ;

	    	if(this.laserEnd != null) {
	    		if(this.getPositionVec().getX() - this.getSize(Pose.STANDING).width < this.laserEnd.getPositionVec().getX() - this.laserEnd.getSize(Pose.STANDING).width)
                    minX = this.getPositionVec().getX() - this.getSize(Pose.STANDING).width;
	    		else
                    minX = this.laserEnd.getPositionVec().getX() - this.laserEnd.getSize(Pose.STANDING).width;
	    		
	    		if(this.getPositionVec().getX() + this.getSize(Pose.STANDING).width > this.laserEnd.getPositionVec().getX() + this.laserEnd.getSize(Pose.STANDING).width)
	    			maxX = this.getPositionVec().getX() + this.getSize(Pose.STANDING).width;
	    		else
	    			maxX = this.laserEnd.getPositionVec().getX() + this.laserEnd.getSize(Pose.STANDING).width;
	    		
	    		
	    		if(this.getPositionVec().getY() - this.getSize(Pose.STANDING).height < this.laserEnd.getPositionVec().getY() - this.laserEnd.getSize(Pose.STANDING).height)
	    			minY = this.getPositionVec().getY() - this.getSize(Pose.STANDING).height;
	    		else
	    			minY = this.laserEnd.getPositionVec().getY() - this.laserEnd.getSize(Pose.STANDING).height;
	    		
	    		if(this.getPositionVec().getY() + this.getSize(Pose.STANDING).width > this.laserEnd.getPositionVec().getY() + this.laserEnd.getSize(Pose.STANDING).height)
	    			maxY = this.getPositionVec().getY() + this.getSize(Pose.STANDING).height;
	    		else
	    			maxY = this.laserEnd.getPositionVec().getY() + this.laserEnd.getSize(Pose.STANDING).height;
	    		
	    		
	    		if(this.getPositionVec().getZ() - this.getSize(Pose.STANDING).width < this.laserEnd.getPositionVec().getZ() - this.laserEnd.getSize(Pose.STANDING).width)
	    			minZ = this.getPositionVec().getZ() - this.getSize(Pose.STANDING).width;
	    		else
	    			minZ = this.laserEnd.getPositionVec().getZ() - this.laserEnd.getSize(Pose.STANDING).width;
	    		
	    		if(this.getPositionVec().getZ() + this.getSize(Pose.STANDING).width > this.laserEnd.getPositionVec().getZ() + this.laserEnd.getSize(Pose.STANDING).width)
	    			maxZ = this.getPositionVec().getZ() + this.getSize(Pose.STANDING).width;
	    		else
	    			maxZ = this.laserEnd.getPositionVec().getZ() + this.laserEnd.getSize(Pose.STANDING).width;
	    	}
	    	else {
	    		minX = this.getPositionVec().getX() - this.getSize(Pose.STANDING).width;
	    		maxX = this.getPositionVec().getX() + this.getSize(Pose.STANDING).width;
	    		minY = this.getPositionVec().getY() - this.getSize(Pose.STANDING).height;
	    		maxY = this.getPositionVec().getY() + this.getSize(Pose.STANDING).height;
	    		minZ = this.getPositionVec().getZ() - this.getSize(Pose.STANDING).width;
	    		maxZ = this.getPositionVec().getZ() + this.getSize(Pose.STANDING).width;
	    	}

            this.getBoundingBox().expand(
                    (maxX - minX) - (this.getBoundingBox().maxX - this.getBoundingBox().minX),
                    (maxY - minY) - (this.getBoundingBox().maxY - this.getBoundingBox().minY),
                    (maxZ - minZ) - (this.getBoundingBox().maxZ - this.getBoundingBox().minZ)
            );
    	}
    	else if(this.isAlive()) {
    		this.remove();
    	}
    }
    
    
    // ==================================================
 	//                   Update End
 	// ==================================================
	public void updateEnd() {
		if(this.getEntityWorld().isRemote) {
			this.laserEndRef = this.dataManager.get(LASER_END_ID);
			Entity possibleLaserEnd = null;
			if(this.laserEndRef != -1)
				possibleLaserEnd = this.getEntityWorld().getEntityByID(this.laserEndRef);
			if(possibleLaserEnd != null && possibleLaserEnd instanceof LaserEndProjectileEntity)
				this.laserEnd = (LaserEndProjectileEntity)possibleLaserEnd;
			else {
				this.laserEnd = null;
				return;
			}
		}

		if(this.laserEnd == null)
			fireProjectile();
		
		if(this.laserEnd == null)
			this.laserEndRef = -1;
		else {
			if(!this.getEntityWorld().isRemote)
				this.laserEndRef = this.laserEnd.getEntityId();
			
			// Entity Aiming:
			boolean lockedLaser = false;
			if(this.shootingEntity != null && this.useEntityAttackTarget) {
				if(this.shootingEntity instanceof BaseCreatureEntity && ((BaseCreatureEntity)this.shootingEntity).getAttackTarget() != null) {
					LivingEntity attackTarget = ((BaseCreatureEntity)this.shootingEntity).getAttackTarget();
					this.targetX = attackTarget.getPositionVec().getX();
					this.targetY = attackTarget.getPositionVec().getY() + (attackTarget.getSize(Pose.STANDING).height / 2);
					this.targetZ = attackTarget.getPositionVec().getZ();
					lockedLaser = true;
				}
				else {
					Vector3d lookDirection = this.shootingEntity.getLookVec();
					this.targetX = this.shootingEntity.getPositionVec().getX() + (lookDirection.x * this.laserRange);
					this.targetY = this.shootingEntity.getPositionVec().getY() + this.shootingEntity.getEyeHeight() + (lookDirection.y * this.laserRange);
					this.targetZ = this.shootingEntity.getPositionVec().getZ() + (lookDirection.z * this.laserRange);
				}
			}
			
			// Raytracing:
			HashSet<Entity> excludedEntities = new HashSet<>();
			excludedEntities.add(this);
			if(this.shootingEntity != null)
				excludedEntities.add(this.shootingEntity);
			if(this.followEntity != null)
				excludedEntities.add(this.followEntity);
			RayTraceResult rayTraceResult = Utilities.raytrace(this.getEntityWorld(), this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ(), this.targetX, this.targetY, this.targetZ, this.laserWidth, this, excludedEntities);
			
			// Update Laser End Position:
			double newTargetX = this.targetX;
			double newTargetY = this.targetY;
			double newTargetZ = this.targetZ;
			if(rayTraceResult != null && !lockedLaser) {
				newTargetX = rayTraceResult.getHitVec().x;
				newTargetY = rayTraceResult.getHitVec().y;
				newTargetZ = rayTraceResult.getHitVec().z;
			}
			this.laserEnd.onUpdateEnd(newTargetX, newTargetY, newTargetZ);
			
			// Damage:
			if(this.laserTime % this.laserDelay == 0 && this.isAlive() && rayTraceResult instanceof EntityRayTraceResult) {
				EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult)rayTraceResult;
				if(this.laserEnd.getDistance(entityRayTraceResult.getEntity()) <= (this.laserWidth * 10)) {
					boolean doDamage = true;
					if (entityRayTraceResult.getEntity() instanceof LivingEntity) {
						doDamage = this.canDamage((LivingEntity) entityRayTraceResult.getEntity());
					}
					if (doDamage)
						this.updateDamage(entityRayTraceResult.getEntity());
				}
            }
		}
		
		this.dataManager.set(LASER_END_ID, this.laserEndRef);
		if(this.getBeamSound() != null)
			this.playSound(this.getBeamSound(), 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
	}
	
    
    // ==================================================
 	//                    Laser Time
 	// ==================================================
	public void setTime(int time) {
		this.laserTime = time;
	}

	public int getTime() {
		return this.laserTime;
	}
    
    
    // ==================================================
 	//                 Fire Projectile
 	// ==================================================
    public void fireProjectile() {
    	World world = this.getEntityWorld();
    	if(world.isRemote)
    		return;
    	
		try {
			if(this.shootingEntity == null) {
				Constructor laserEndConstructor = this.getLaserEndClass().getConstructor(EntityType.class, World.class, Double.class, Double.class, Double.class, LaserProjectileEntity.class);
				this.laserEnd = (LaserEndProjectileEntity)laserEndConstructor.newInstance(ProjectileManager.getInstance().oldProjectileTypes.get(this.getLaserEndClass()), world, this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ(), this);
			}
	        else {
				Constructor laserEndConstructor = this.getLaserEndClass().getConstructor(EntityType.class, World.class, LivingEntity.class, LaserProjectileEntity.class);
				this.laserEnd = (LaserEndProjectileEntity)laserEndConstructor.newInstance(ProjectileManager.getInstance().oldProjectileTypes.get(this.getLaserEndClass()), world, this.shootingEntity, this);
	        }
	        
			if(this.getLaunchSound() != null)
				this.playSound(this.getLaunchSound(), 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
	        
	        world.addEntity(laserEnd);
		}
		catch (Exception e) {
			System.out.println("[WARNING] [LycanitesMobs] EntityLaser was unable to instantiate the EntityLaserEnd.");
			e.printStackTrace();
		}
    }
	
    
    // ==================================================
 	//               Sync Shooting Entity
 	// ==================================================
    public void syncShootingEntity() {
    	if(!this.getEntityWorld().isRemote) {
    		if(this.shootingEntity == null) this.shootingEntityRef = -1;
    		else this.shootingEntityRef = this.shootingEntity.getEntityId();
    		this.dataManager.set(SHOOTING_ENTITY_ID, this.shootingEntityRef);
    	}
    	else {
    		this.shootingEntityRef = this.dataManager.get(SHOOTING_ENTITY_ID);
            if(this.shootingEntityRef == -1) this.shootingEntity = null;
    		else {
    			Entity possibleShootingEntity = this.getEntityWorld().getEntityByID(this.shootingEntityRef);
    			if(possibleShootingEntity != null && possibleShootingEntity instanceof LivingEntity)
    				this.shootingEntity = (LivingEntity)possibleShootingEntity;
    			else
    				this.shootingEntity = null;
    		}
    	}
    }
    
    public void syncOffset() {
    	if(!this.getEntityWorld().isRemote) {
    		this.dataManager.set(OFFSET_X, (float) this.offsetX);
    		this.dataManager.set(OFFSET_Y, (float) this.offsetY);
    		this.dataManager.set(OFFSET_Z, (float) this.offsetZ);
    	}
    	else {
    		this.offsetX = this.dataManager.get(OFFSET_X);
    		this.offsetY = this.dataManager.get(OFFSET_Y);
    		this.offsetZ = this.dataManager.get(OFFSET_Z);
        }
    }
	
    
    // ==================================================
 	//                   Get laser End
 	// ==================================================
    public LaserEndProjectileEntity getLaserEnd() {
        return this.laserEnd;
    }

    public Class getLaserEndClass() {
        return LaserEndProjectileEntity.class;
    }
	
    
    // ==================================================
 	//                    Set Target
 	// ==================================================
    public void setTarget(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    @Override
    protected void onImpact(RayTraceResult rayTraceResult) {
    	return;
    }
    
    
    // ==================================================
 	//                      Damage
 	// ==================================================
    public boolean updateDamage(Entity target) {
    	boolean attackSuccess = false;
    	float damage = this.getDamage(target);
		float damageInit = damage;

		// Prevent Knockback:
		double targetKnockbackResistance = 0;
		if(this.knockbackChance < 1) {
			if(this.knockbackChance <= 0 || this.rand.nextDouble() <= this.knockbackChance) {
				if(target instanceof LivingEntity) {
					targetKnockbackResistance = ((LivingEntity)target).getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
					((LivingEntity)target).getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
				}
			}
		}

		// Deal Damage:
		if(this.func_234616_v_() instanceof BaseCreatureEntity) {
			BaseCreatureEntity creatureThrower = (BaseCreatureEntity)this.func_234616_v_();
			attackSuccess = creatureThrower.doRangedDamage(target, this, damage);
		}
        else {
			double pierceDamage = 1;
			if(damage <= pierceDamage)
                attackSuccess = target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()).setDamageBypassesArmor().setDamageIsAbsolute(), damage);
            else {
                int hurtResistantTimeBefore = target.hurtResistantTime;
                target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()).setDamageBypassesArmor().setDamageIsAbsolute(), (float)pierceDamage);
                target.hurtResistantTime = hurtResistantTimeBefore;
                damage -= pierceDamage;
                attackSuccess = target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()), damage);
            }
        }
        
        if(target instanceof LivingEntity)
        	this.onDamage((LivingEntity)target, damageInit, attackSuccess);
    	
        // Restore Knockback:
        if(this.knockbackChance < 1) {
            if(this.knockbackChance <= 0 || this.rand.nextDouble() <= this.knockbackChance) {
                if(target instanceof LivingEntity)
                    ((LivingEntity)target).getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
            }
        }

        return attackSuccess;
    }
    
    
    // ==================================================
 	//                      Stats
 	// ==================================================
    public void setRange(float range) {
    	this.laserRange = range;
    }

    public void setLaserWidth(float width) {
    	this.laserWidth = width;
    }

    public float getLaserWidth() {
    	return this.laserWidth;
    }

    public float getLaserAlpha() {
        return 0.25F + (float)(0.1F * Math.sin(this.ticksExisted));
    }
    
    
    // ==================================================
 	//                      Visuals
 	// ==================================================
    public ResourceLocation getBeamTexture() {
    	return null;
    }
    
    public double[] getLengths() {
    	if(this.laserEnd == null)
    		return new double[] {0.0D, 0.0D, 0.0D};
    	else
    		return new double[] {
    			this.laserEnd.getPositionVec().getX() - this.getPositionVec().getX(),
    			this.laserEnd.getPositionVec().getY() - this.getPositionVec().getY(),
    			this.laserEnd.getPositionVec().getZ() - this.getPositionVec().getZ()
    		};
    }
    
    public float getLength() {
    	if(this.laserEnd == null)
    		return 0;
    	return this.getDistance(this.laserEnd);
    }
    
    public float[] getBeamAngles() {
    	float[] angles = new float[] {0, 0, 0, 0};
    	if(this.laserEnd != null) {
    		float dx = (float)(this.laserEnd.getPositionVec().getX() - this.getPositionVec().getX());
    		float dy = (float)(this.laserEnd.getPositionVec().getY() - this.getPositionVec().getY());
    		float dz = (float)(this.laserEnd.getPositionVec().getZ() - this.getPositionVec().getZ());
			angles[0] = (float)Math.toDegrees(Math.atan2(dz, dy)) - 90;
			angles[1] = (float)Math.toDegrees(Math.atan2(dx, dz));
			angles[2] = (float)Math.toDegrees(Math.atan2(dx, dy)) - 90;
			
			// Distance based x/z rotation:
			float dr = (float)Math.sqrt(dx * dx + dz * dz);
			angles[3] = (float)Math.toDegrees(Math.atan2(dr, dy)) - 90;
		}
    	return angles;
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
	@Override
	public SoundEvent getLaunchSound() {
		return null;
	}

	@Override
	public SoundEvent getBeamSound() {
		return null;
	}
    
    
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    @Override
    public void readAdditional(CompoundNBT nbtTagCompound) {
    	if(nbtTagCompound.contains("LaserTime"))
    		this.setTime(nbtTagCompound.getInt("LaserTime"));
    	if(nbtTagCompound.contains("OffsetX"))
    		this.offsetX = nbtTagCompound.getDouble("OffsetX");
    	if(nbtTagCompound.contains("OffsetY"))
    		this.offsetY = nbtTagCompound.getDouble("OffsetY");
    	if(nbtTagCompound.contains("OffsetZ"))
    		this.offsetZ = nbtTagCompound.getDouble("OffsetZ");
        super.readAdditional(nbtTagCompound);
    }
    
    // ========== Write ==========
    @Override
    public void writeAdditional(CompoundNBT nbtTagCompound) {
    	nbtTagCompound.putInt("LaserTime", this.laserTime);
    	nbtTagCompound.putDouble("OffsetX", this.offsetX);
    	nbtTagCompound.putDouble("OffsetY", this.offsetY);
    	nbtTagCompound.putDouble("OffsetZ", this.offsetZ);
        super.writeAdditional(nbtTagCompound);
    }
}
