package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallGrassBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class EntityProjectileBase extends ThrowableEntity {
	public String entityName = "projectile";
	public ModInfo modInfo;
	public long updateTick;
	
	// Properties:
    public boolean movement = true;

    // Stats:
	public float projectileScale = 1F;
	public int projectileLife = 200;
	public int damage = 1;
	public int pierce = 1;
	public double weight = 1.0D;
	public double knockbackChance = 1;

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

    // Data Manager:
    protected static final DataParameter<Float> SCALE = EntityDataManager.createKey(EntityProjectileBase.class, DataSerializers.field_187193_c);


	// ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityProjectileBase(World world) {
        super(EntityType.SNOWBALL, world);
        this.dataManager.register(SCALE, this.projectileScale);
        this.setProjectileScale(this.projectileScale);
        this.setup();
    }

    public EntityProjectileBase(World world, LivingEntity entityLiving) {
        this(world);
        this.shoot(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0.0F, 1.1F, 1.0F);
        this.dataManager.register(SCALE, this.projectileScale);
        this.setProjectileScale(this.projectileScale);
        this.setup();
    }

    public EntityProjectileBase(World world, double x, double y, double z) {
        this(world);
        this.dataManager.register(SCALE, this.projectileScale);
        this.setProjectileScale(this.projectileScale);
        this.setup();
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

	}
	
    
    // ==================================================
 	//                      Update
 	// ==================================================
    @Override
    public void tick() {
    	this.updateTick++;

        if(!this.movement) {
            this.inGround = false;
            this.timeUntilPortal = this.getPortalCooldown();
        }
        double initX = this.posX;
        double initY = this.posY;
        double initZ = this.posZ;

		super.tick();

        if(!this.movement) {
            this.posX = initX;
            this.posY = initY;
            this.posZ = initZ;
            this.setMotion(0, 0, 0);
            this.setPosition(this.posX, this.posY, this.posZ);
        }
    	
    	// Terrain Destruction
    	if(!this.getEntityWorld().isRemote) {
    		if(!this.waterProof && this.isInWater())
    			this.remove();
    		else if(!this.lavaProof && this.isInLava())
    			this.remove();
    	}

    	// Life Timeout:
        if(!this.getEntityWorld().isRemote || this.clientOnly) {
            if(this.projectileLife-- <= 0)
                this.remove();
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
 	//                      Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
    	return (float)this.weight * 0.03F;
    }

    @Override
	public void spawnRunningParticles() {}
    
    
    // ==================================================
  	//                       Impact
  	// ==================================================
     @Override
     protected void onImpact(RayTraceResult rayTraceResult) {
     	 boolean collided = false;
         boolean entityCollision = false;
         boolean blockCollision = false;
         BlockPos impactPos = this.getPosition();
     	
     	// Entity Hit:
		 Entity entityHit = null;
		if(rayTraceResult.getType() == RayTraceResult.Type.ENTITY && rayTraceResult.hitInfo instanceof Entity) {
			entityHit = (Entity)rayTraceResult.hitInfo;
		}
     	if(entityHit != null) {
     		if(this.getThrower() != null && entityHit == this.getThrower())
     			return;
     		boolean doDamage = true;
 			if(entityHit instanceof LivingEntity) {
 				doDamage = this.canDamage((LivingEntity)entityHit);
 			}
 			if(!this.getEntityWorld().isRemote) {
				if (this.getThrower() == null || entityHit != this.getThrower()) {
					this.onEntityCollision(entityHit);
				}
			}
			if(doDamage) {
 				if(entityHit instanceof LivingEntity) {
 					LivingEntity target = (LivingEntity)entityHit;
 					if(this.onEntityLivingDamage(target)) {
 						//movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), this.getDamage((LivingEntity)movingObjectPosition.entityHit));

                        boolean attackSuccess = false;
 						float damage = this.getDamage(target);

 						if(damage != 0) {
							float damageInit = damage;

							// Prevent Knockback:
							double targetKnockbackResistance = 0;
							boolean stopKnockback = false;
							if (this.knockbackChance < 1) {
								if (this.knockbackChance <= 0 || this.rand.nextDouble() <= this.knockbackChance) {
									if (target instanceof LivingEntity) {
										targetKnockbackResistance = target.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getValue();
										target.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
										stopKnockback = true;
									}
								}
							}

							// Deal Damage:
							if (this.getThrower() instanceof EntityCreatureBase) {
								EntityCreatureBase creatureThrower = (EntityCreatureBase) this.getThrower();
								attackSuccess = creatureThrower.doRangedDamage(target, this, damage);
							}
							else {
								double pierceDamage = this.pierce;
								if (damage <= pierceDamage)
									attackSuccess = target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()).setDamageBypassesArmor().setDamageIsAbsolute(), damage);
								else {
									int hurtResistantTimeBefore = target.hurtResistantTime;
									target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()).setDamageBypassesArmor().setDamageIsAbsolute(), (float) pierceDamage);
									target.hurtResistantTime = hurtResistantTimeBefore;
									damage -= pierceDamage;
									attackSuccess = target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), damage);
								}
							}

							this.onDamage(target, damageInit, attackSuccess);

							// Restore Knockback:
							if (stopKnockback) {
								target.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
							}
						}
 					}
 				}
 			}
 			collided = true;
            entityCollision = true;

     		int i = (int)Math.floor(entityHit.posX);
     		int j = (int)Math.floor(entityHit.posY);
            int k = (int)Math.floor(entityHit.posZ);
            impactPos = new BlockPos(i, j, k);
            if(!this.getEntityWorld().isRemote && this.canDestroyBlock(impactPos)) {
            	try {
					this.placeBlock(this.getEntityWorld(), impactPos);
				}
				catch(Exception e) {}
			}
     	}
     	
     	// Block Hit:
     	else {
			int i = (int)rayTraceResult.getHitVec().getX();
			int j = (int)rayTraceResult.getHitVec().getY();
			int k = (int)rayTraceResult.getHitVec().getZ();
			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = this.getEntityWorld().getBlockState(blockPos);
			if (blockState.getBlock() instanceof TallGrassBlock || blockState.getBlock() == Blocks.TALL_GRASS) {
				if (this.cutsGrass) {
					world.destroyBlock(blockPos, false);
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

                impactPos = new BlockPos(i, j, k);
 	            if(!this.getEntityWorld().isRemote && this.canDestroyBlock(impactPos)) {
 	            	try {
						this.placeBlock(this.getEntityWorld(), impactPos);
					}
					catch(Exception e) {}
				}
 	        }
     	}
     	
     	if(collided) {
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
 	        if(!this.getEntityWorld().isRemote && !entityPierced && !blockPierced) {
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

    	 LivingEntity owner = this.getThrower();
	     if(owner != null) {

            if(owner instanceof EntityCreatureBase) {
                EntityCreatureBase ownerCreature = (EntityCreatureBase)owner;
                if(!ownerCreature.canAttack(targetEntity))
                    return false;
            }
	    	
	    	// Player Damage Event:
		    if(owner instanceof PlayerEntity) {
		    	if(MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((PlayerEntity)owner, targetEntity))) {
		    		return false;
		    	}
		    }
		    
		    // Player PVP:
		    if(!this.getEntityWorld().getServer().isPVPEnabled()) {
		    	if(owner instanceof PlayerEntity) {
			    	if(targetEntity instanceof PlayerEntity)
			    		return false;
			    	if(targetEntity instanceof EntityCreatureTameable) {
			    		EntityCreatureTameable tamedTarget = (EntityCreatureTameable)targetEntity;
			    		if(tamedTarget.isTamed()) {
			    			return false;
			    		}
			    	}
		    	}
		    }
		    
		    // Friendly Fire:
		    if(owner.isOnSameTeam(targetEntity) && CreatureManager.getInstance().config.friendlyFire)
		    	return false;
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
     
     //========== Entity Living Collision ==========
     public boolean onEntityLivingDamage(LivingEntity entityLiving) {
    	 return true;
     }
     
     //========== Can Destroy Block ==========
     public boolean canDestroyBlock(BlockPos pos) {
    	 return this.getEntityWorld().isAirBlock(pos) && this.getEntityWorld().getBlockState(pos.down()).getMaterial().isSolid();
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
    		 //this.getEntityWorld().spawnParticle("particlename", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
     }
     
     
     // ==================================================
     //                    Collision
     // ==================================================
     @Override
     public boolean canBeCollidedWith() {
         return false;
     }
     
     
     // ==================================================
     //                     Attacked
     // ==================================================
     @Override
     public boolean attackEntityFrom(DamageSource damageSource, float damage) {
         return false;
     }
     
     
     // ==================================================
     //                      Scale
     // ==================================================
     public void setProjectileScale(float scale) {
     	 this.projectileScale = scale;
         //this.getSize(Pose.STANDING).setSize(scale, scale); TODO Move to EntityType
         if(this.getEntityWorld().isRemote && !this.clientOnly)
             return;
         if(this.getThrower() != null && this.getThrower() instanceof EntityCreatureBase)
             this.projectileScale *= ((EntityCreatureBase)this.getThrower()).sizeScale;
         this.dataManager.set(SCALE, this.projectileScale);
     }
     
     public float getProjectileScale() {
         return this.projectileScale;
     }

    public float getTextureOffsetY() {
        return 0;
    }
     
     
     // ==================================================
     //                      Damage
     // ==================================================
	public void setDamage(int damage) {
		this.damage = damage;
	}

	public float getDamage(Entity entity) {
		float damage = (float)this.damage;
		if(this.getThrower() != null) {
			// 20% Extra Damage From Players vs Entities
			if((this.getThrower() instanceof PlayerEntity  || this.getThrower().getControllingPassenger() instanceof PlayerEntity) && !(entity instanceof PlayerEntity))
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
    	 if(this.getThrower() != null && this.getThrower() instanceof EntityCreatureBase)
    		 return Math.round((float)((EntityCreatureBase)this.getThrower()).getEffectDuration(seconds) / 5);
    	 return seconds * 20;
     }

    /** When given a base effect strngth value such as a life drain amount, this will return the scaled value with difficulty and other modifiers taken into account
     * value - The base effect strength.
     **/
    public float getEffectStrength(float value) {
        if(this.getThrower() != null && this.getThrower() instanceof EntityCreatureBase)
            return ((EntityCreatureBase)this.getThrower()).getEffectStrength(value);
        return value;
    }


    // ==================================================
    //                      Utility
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
        coords[0] = entity.posX + (distance * xAmount);
        coords[1] = entity.posY;
        coords[2] = entity.posZ + (distance * zAmount);
        return coords;
    }


	// ==================================================
	//                       NBT
	// ==================================================
	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);

		compound.putFloat("ProjectileScale", this.projectileScale);
		compound.putInt("ProjectileLife", this.projectileLife);
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);

		if(compound.contains("ProjectileScale")) {
			this.setProjectileScale(compound.getFloat("ProjectileScale"));
		}
		if(compound.contains("ProjectileLife")) {
			this.projectileLife = compound.getInt("ProjectileLife");
		}
	}
     
     
     // ==================================================
     //                      Visuals
     // ==================================================
    public String getTextureName() {
        return this.entityName.toLowerCase();
    }

     public ResourceLocation getTexture() {
     	if(AssetManager.getTexture(this.getTextureName()) == null)
     		AssetManager.addTexture(this.getTextureName(), this.modInfo, "textures/items/" + this.getTextureName() + ".png");
     	return AssetManager.getTexture(this.getTextureName());
     }
     
     
     // ==================================================
     //                      Sounds
     // ==================================================
     public SoundEvent getLaunchSound() {
     	return AssetManager.getSound(this.entityName);
     }

	public SoundEvent getImpactSound() {
		return AssetManager.getSound(this.entityName + "_impact");
	}
}
