package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallGrassBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.network.NetworkHooks;

public class BaseProjectileEntity extends ThrowableEntity {
	public String entityName = "projectile";
	public ModInfo modInfo;
	public long updateTick;
	protected Entity owner;
	
	// Properties:
    public boolean movement = true;

    // Stats:
	public float projectileScale = 1F;
	public int projectileLife = 200;
	public int damage = 1;
	public int pierce = 1;
	public double weight = 1.0D;
	public double knockbackChance = 1;
	public int bonusDamage = 0;

    // Flags:
	public boolean waterProof = false;
	public boolean lavaProof = false;
	public boolean cutsGrass = false;
	public boolean ripper = false;
	public boolean pierceBlocks = false;

	// Texture and Animation:
    public int animationFrame = 0;
    public int animationFrameMax = 0;
    public int textureTiling = 1;
    public float textureScale = 1;
    public float textureOffsetY = 0;
    public boolean clientOnly = false;
    public float rollSpeed = 0;

    // Data Manager:
    protected static final DataParameter<Float> SCALE = EntityDataManager.createKey(BaseProjectileEntity.class, DataSerializers.FLOAT);


	// ==================================================
 	//			    Constructors
 	// ==================================================
    public BaseProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world) {
	   super(entityType, world);
	   this.setup();
    }

    public BaseProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity entityLiving) {
	   this(entityType, world);
	   this.setPosition(entityLiving.getPositionVec().getX(), entityLiving.getPositionVec().getY() + entityLiving.getEyeHeight(), entityLiving.getPositionVec().getZ());
	   this.func_234612_a_(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0.0F, 1.1F, 1.0F); // Shoot from entity
	   this.setShooter(entityLiving);
	   this.setup();
    }

    public BaseProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world, double x, double y, double z) {
	   this(entityType, world);
	   this.setPosition(x, y, z);
	   this.setup();
    }

    @Override
	public EntityType getType() {
		if(ProjectileManager.getInstance().oldProjectileTypes.get(this.getClass()) == null) {
			return super.getType();
		}
    	return ProjectileManager.getInstance().oldProjectileTypes.get(this.getClass());
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
    
    // ========== Setup Projectile ==========
    public void setup() {

    }

	public String getStringFromDataManager(DataParameter<String> key) {
		try {
			return this.getDataManager().get(key);
		}
		catch (Exception e) {
			return null;
		}
	}

	@Override
	public void registerData() {
		this.dataManager.register(SCALE, this.projectileScale);
		this.setProjectileScale(this.projectileScale);
	}

	@Override
	public void setShooter(Entity entity) {
		super.setShooter(entity);
		this.owner = entity;
	}

	public Entity getShooter() {
		return this.owner;
	}

	public LivingEntity getOwner() {
		if(this.owner instanceof LivingEntity) {
			return (LivingEntity) this.owner;
		}
		return null;
	}
	
    
    // ==================================================
 	//				        Update
 	// ==================================================
    @Override
    public void tick() {
		this.updateTick++;

	   if(!this.movement) {
		  this.onGround = false;
		  this.portalCounter = this.getPortalCooldown();
	   }
	   double initX = this.getPositionVec().getX();
	   double initY = this.getPositionVec().getY();
	   double initZ = this.getPositionVec().getZ();

		super.tick();

	   if(!this.movement) {
		  this.setPosition(initX, initY, initZ);
		  this.setMotion(0, 0, 0);
		  this.setPosition(this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ());
	   }
    	
    	// Terrain Destruction
    	if(!this.getEntityWorld().isRemote || this.clientOnly) {
    		if(!this.waterProof && this.isInWater())
    			this.remove();
    		else if(!this.lavaProof && this.isInLava())
    			this.remove();
    	}

    	// Life Timeout:
	   if(!this.getEntityWorld().isRemote || this.clientOnly) {
		  if(this.projectileLife-- <= 0) {
				this.remove();
			}
		}

	   // Sync Scale:
	   if(this.getEntityWorld().isRemote) {
		  this.projectileScale = this.dataManager.get(SCALE);
	   }

	   // Animation:
	   if(this.animationFrameMax > 0) {
		  if (this.animationFrame == this.animationFrameMax || this.animationFrame < 0)
			 this.animationFrame = 0;
		  else
			 this.animationFrame++;
	   }
    }

	/**
	 * This is an expensive check for when there are a lot of projectiles. The isLavaProof death check is checked on impact instead for significantly greater performance.
	 * @return Always false for performance.
	 */
	@Override
	public boolean isInLava() {
		return false;
	}
	
    
    // ==================================================
 	//				  Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
    	return (float)this.weight * 0.03F;
    }

//    @Override
//	public void spawnRunningParticles() {} TODO See what this was changed to...

//	@Override
//	public boolean handleWaterMovement() {
//		return this.inWater;
//	}
    
    
    // ==================================================
  	//                     Impact
  	// ==================================================
	@Override
	protected void onImpact(RayTraceResult rayTraceResult) {
		if(this.getEntityWorld().isRemote)
			return;
		boolean collided = false;
	    boolean entityCollision = false;
		boolean doDamage = true;
		boolean blockCollision = false;
		BlockPos impactPos = new BlockPos(this.getPositionVec());

		// Entity Hit:
		Entity entityHit = null;
		if(rayTraceResult.getType() == RayTraceResult.Type.ENTITY && rayTraceResult instanceof EntityRayTraceResult) {
			entityHit = ((EntityRayTraceResult)rayTraceResult).getEntity();
		}
		if(entityHit != null) {
			if(this.func_234616_v_() != null) {
				if(entityHit == this.func_234616_v_()) {
					return;
				}
			}
 			if(entityHit instanceof LivingEntity) {
 				doDamage = this.canDamage((LivingEntity)entityHit);
 			}
 			if(!this.getEntityWorld().isRemote) {
				if (this.func_234616_v_() == null || entityHit != this.func_234616_v_()) {
					this.onEntityCollision(entityHit);
				}
			}
			if(doDamage) {
 				if(entityHit instanceof LivingEntity) {
 					LivingEntity target = (LivingEntity)entityHit;
					boolean attackSuccess;
					float damage = this.getDamage(target);

					if(damage != 0) {
						float damageInit = damage;

						// Prevent Knockback:
						double targetKnockbackResistance = 0;
						boolean stopKnockback = false;
						if (this.knockbackChance < 1) {
							if (this.knockbackChance <= 0 || this.rand.nextDouble() <= this.knockbackChance) {
								if (target instanceof LivingEntity) {
									targetKnockbackResistance = target.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
									target.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
									stopKnockback = true;
								}
							}
						}

						// Deal Damage:
						if (this.func_234616_v_() instanceof BaseCreatureEntity) {
							BaseCreatureEntity creatureThrower = (BaseCreatureEntity) this.func_234616_v_();
							attackSuccess = creatureThrower.doRangedDamage(target, this, damage);
						}
						else {
							double pierceDamage = this.pierce;
							if (damage <= pierceDamage)
								attackSuccess = target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()).setDamageBypassesArmor().setDamageIsAbsolute(), damage);
							else {
								int hurtResistantTimeBefore = target.hurtResistantTime;
								target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()).setDamageBypassesArmor().setDamageIsAbsolute(), (float) pierceDamage);
								target.hurtResistantTime = hurtResistantTimeBefore;
								damage -= pierceDamage;
								attackSuccess = target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()), damage);
							}
						}

						// Apply Damage Effects If Not Blocking:
						if(!(target.isActiveItemStackBlocking() && target.getActiveItemStack().isShield(target))) {
							this.onEntityLivingDamage(target); // Old Projectiles
							this.onDamage(target, damageInit, attackSuccess); // JSON Projectiles
						}

						// Restore Knockback:
						if (stopKnockback) {
							target.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
						}
					}
 				}
 			}
 			collided = true;
		  entityCollision = true;

		  impactPos = entityHit.getPosition();
		  if(!this.getEntityWorld().isRemote && this.canDestroyBlock(impactPos)) {
		  	try {
					this.placeBlock(this.getEntityWorld(), impactPos);
				}
				catch(Exception e) {}
			}
		}
		
		// Block Hit:
		else if(rayTraceResult.getType() == RayTraceResult.Type.BLOCK && rayTraceResult instanceof BlockRayTraceResult) {
			BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult)rayTraceResult;
			impactPos = blockRayTraceResult.getPos();
			BlockState blockState = this.getEntityWorld().getBlockState(impactPos);
			if (blockState.getBlock() instanceof TallGrassBlock || blockState.getBlock() == Blocks.TALL_GRASS) {
				if (this.cutsGrass) {
					world.destroyBlock(impactPos, false);
				}
			}
			else {
				collided = blockState.getMaterial().isSolid();
				if (!this.waterProof && blockState.getMaterial() == Material.WATER) {
					collided = true;
				}
				if (!this.lavaProof && blockState.getMaterial() == Material.LAVA) {
					collided = true;
				}
			}
		   
 		   if(collided) {
			 blockCollision = true;
 			  /*switch(rayTraceResult.sideHit) { TODO Block Collision Side
				case DOWN:
 					 --j;
 					 break;
				case UP:
 					 ++j;
 					 break;
				case SOUTH:
 					 --k;
 					 break;
				case NORTH:
 					 ++k;
 					 break;
				case WEST:
 					 --i;
 					 break;
				case EAST:
 					 ++i;
 			  }*/

 			  if(!this.getEntityWorld().isRemote && this.canDestroyBlock(impactPos.up())) {
 			  	try {
						this.placeBlock(this.getEntityWorld(), impactPos.up());
					}
					catch(Exception e) {}
				}
 		   }
		}
		
		if(collided && (!entityCollision || doDamage)) {
 	    	// Impact Particles:
 		   if(!this.getEntityWorld().isRemote) {
				this.onImpactComplete(impactPos);
			}
 		   else {
				this.onImpactVisuals();
			}
 		   
 		   // Remove Projectile:
		  boolean entityPierced = this.ripper && entityCollision;
		  boolean blockPierced = this.pierceBlocks && blockCollision;
		  if((!this.getEntityWorld().isRemote || this.clientOnly) && !entityPierced && !blockPierced) {
 			  this.remove();
				if(this.getImpactSound() != null) {
					this.playSound(this.getImpactSound(), 1.0F, 1.0F / (this.getEntityWorld().rand.nextFloat() * 0.4F + 0.8F));
				}
 		   }
		}
	}
	
	//========== Do Damage Check ==========
	public boolean canDamage(LivingEntity targetEntity) {
		if(this.getEntityWorld().isRemote)
			return false;

		LivingEntity owner = this.getOwner();
		if(owner != null) {
			if(owner instanceof BaseCreatureEntity) {
				BaseCreatureEntity ownerCreature = (BaseCreatureEntity) owner;
				if (!ownerCreature.canAttack(targetEntity)) {
					return false;
				}
			}

			// Player Damage Event:
			if(owner instanceof PlayerEntity) {
				if(MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((PlayerEntity)owner, targetEntity))) {
					return false;
				}
				if(targetEntity instanceof TameableCreatureEntity) {
					TameableCreatureEntity targetCreature = (TameableCreatureEntity)targetEntity;
					if(targetCreature.getPlayerOwner() == owner)
						return false;
				}
			}

			// Player PVP:
			if(!this.getEntityWorld().getServer().isPVPEnabled()) {
				if(owner instanceof PlayerEntity) {
					if(targetEntity instanceof PlayerEntity) {
						return false;
					}
					if(targetEntity instanceof TameableCreatureEntity) {
						TameableCreatureEntity tamedTarget = (TameableCreatureEntity)targetEntity;
						if(tamedTarget.isTamed()) {
							return false;
						}
					}
				}
			}

			// Friendly Fire:
			if(owner.isOnSameTeam(targetEntity) && CreatureManager.getInstance().config.friendlyFire) {
				return false;
			}
		}

		return true;
	}
	

	/**
	 * Called when this projectile damages an entity (successfully or on failure).
	 * @param target The entity damaged.
	 * @param damage The full amount of damage that was meant to be dealt.
	 * @param attackSuccess True if the entity was damaged, false if it wasn't.
	 */
	public void onDamage(LivingEntity target, float damage, boolean attackSuccess) {}
	
	//========== Entity Collision ==========
	public void onEntityCollision(Entity entity) {}
	
	//========== Entity Living Damage ==========
	public boolean onEntityLivingDamage(LivingEntity entityLiving) {
    	 return true;
	}
	
	//========== Can Destroy Block ==========
	public boolean canDestroyBlock(BlockPos pos) {
    	 return this.getEntityWorld().isAirBlock(pos) && this.getEntityWorld().getBlockState(pos.down()).isSolid();
	}
	
	//========== Place Block ==========
	public void placeBlock(World world, BlockPos pos) {
    	 //world.setBlock(pos, ObjectManager.getBlock("BlockName").blockID);
	}
	
	//========== On Impact Splash/Ricochet Server Side ==========
	public void onImpactComplete(BlockPos impactPos) {}
	
	//========== On Impact Particles/Sounds Client Side ==========
	public void onImpactVisuals() {
    	 //for(int i = 0; i < 8; ++i)
    		 //this.getEntityWorld().addParticle("particlename", this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ(), 0.0D, 0.0D, 0.0D);
	}
	
	
	// ==================================================
	//				Collision
	// ==================================================
	@Override
	public boolean canBeCollidedWith() {
	    return false;
	}
	
	
	// ==================================================
	//				 Attacked
	// ==================================================
	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float damage) {
	    return false;
	}
	
	
	// ==================================================
	//				  Scale
	// ==================================================
	public void setProjectileScale(float scale) {
		 this.projectileScale = scale;
	    //this.getSize(Pose.STANDING).setSize(scale, scale); TODO Move to EntityType
	    if(this.getEntityWorld().isRemote && !this.clientOnly)
		   return;
	    if(this.func_234616_v_() != null && this.func_234616_v_() instanceof BaseCreatureEntity)
		   this.projectileScale *= ((BaseCreatureEntity)this.func_234616_v_()).sizeScale;
	    this.dataManager.set(SCALE, this.projectileScale);
	}
	
	public float getProjectileScale() {
	    return this.projectileScale;
	}

    public float getTextureOffsetY() {
	   return 0;
    }
	
	
	// ==================================================
	//				  Damage
	// ==================================================
	public void setDamage(int damage) {
		this.damage = damage;
	}

	public float getDamage(Entity entity) {
		float damage = (float)this.damage + this.bonusDamage;
		if(this.func_234616_v_() != null) {
			// 20% Extra Damage From Players vs Entities
			if((this.func_234616_v_() instanceof PlayerEntity  || this.func_234616_v_().getControllingPassenger() instanceof PlayerEntity) && !(entity instanceof PlayerEntity))
				damage *= 1.2f;
		}
		return damage;
	}

	public void setPierce(int pierce) {
		this.pierce = pierce;
	}

	public int getPierce() {
		return this.pierce;
	}

	/** When given a base time (in seconds) this will return the scaled time with difficulty and other modifiers taken into account
	 * seconds - The base duration in seconds that this effect should last for.
	**/
	public int getEffectDuration(int seconds) {
    	 if(this.func_234616_v_() != null && this.func_234616_v_() instanceof BaseCreatureEntity)
    		 return Math.round((float)((BaseCreatureEntity)this.func_234616_v_()).getEffectDuration(seconds) / 5);
    	 return seconds * 20;
	}

    /** When given a base effect strength value such as a life drain amount, this will return the scaled value with difficulty and other modifiers taken into account
	* value - The base effect strength.
	**/
    public float getEffectStrength(float value) {
	   if(this.func_234616_v_() != null && this.func_234616_v_() instanceof BaseCreatureEntity)
		  return ((BaseCreatureEntity)this.func_234616_v_()).getEffectStrength(value);
	   return value;
    }

	/**
	 * Sets additional damage to be dealt by this projectile on top of it's base damage.
	 */
	public void setBonusDamage(int bonusDamage) {
		this.bonusDamage = bonusDamage;
	}


    // ==================================================
    //				  Utility
    // ==================================================
    // ========== Get Facing Coords ==========
    /** Returns the XYZ coordinate in front or behind this entity (using its rotation angle) with the given distance, use a negative distance for behind. **/
    public double[] getFacingPosition(double distance) {
	   return this.getFacingPosition(this, distance, 0D);
    }

    /** Returns the XYZ coordinate in front or behind the provided entity with the given distance and angle offset (in degrees), use a negative distance for behind. **/
    public double[] getFacingPosition(Entity entity, double distance, double angleOffset) {
	   double angle = Math.toRadians(entity.rotationYaw) + angleOffset;
	   double xAmount = -Math.sin(angle);
	   double zAmount = Math.cos(angle);
	   double[] coords = new double[3];
	   coords[0] = entity.getPositionVec().getX() + (distance * xAmount);
	   coords[1] = entity.getPositionVec().getY();
	   coords[2] = entity.getPositionVec().getZ() + (distance * zAmount);
	   return coords;
    }


	// ==================================================
	//				   NBT
	// ==================================================
	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);

		compound.putFloat("ProjectileScale", this.projectileScale);
		compound.putInt("ProjectileLife", this.projectileLife);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);

		if(compound.contains("ProjectileScale")) {
			this.setProjectileScale(compound.getFloat("ProjectileScale"));
		}
		if(compound.contains("ProjectileLife")) {
			this.projectileLife = compound.getInt("ProjectileLife");
		}
	}
	
	
	// ==================================================
	//				  Visuals
	// ==================================================
    public String getTextureName() {
	   return this.entityName.toLowerCase();
    }

	public ResourceLocation getTexture() {
		if(TextureManager.getTexture(this.getTextureName()) == null)
			TextureManager.addTexture(this.getTextureName(), this.modInfo, "textures/item/" + this.getTextureName() + ".png");
		return TextureManager.getTexture(this.getTextureName());
	}
	
	
	// ==================================================
	//				  Sounds
	// ==================================================
	public SoundEvent getLaunchSound() {
		return ObjectManager.getSound(this.entityName);
	}

	public SoundEvent getImpactSound() {
		return ObjectManager.getSound(this.entityName + "_impact");
	}

	public SoundEvent getBeamSound() {
		return ObjectManager.getSound(this.entityName);
	}
}
