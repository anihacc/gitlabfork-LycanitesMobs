package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.capabilities.IExtendedEntity;
import com.lycanitesmobs.core.config.ConfigAdmin;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.network.MessageEntityPerched;
import com.lycanitesmobs.core.network.MessageEntityPickedUp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.util.math.Vec3d;

import javax.vecmath.Vector3d;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtendedEntity implements IExtendedEntity {
    public static Map<Entity, ExtendedEntity> clientExtendedEntities = new HashMap<>();
    public static List<? extends String> FORCE_REMOVE_ENTITY_IDS;
    public static int FORCE_REMOVE_ENTITY_TICKS = 40;

    // Entity Instance:
	public LivingEntity entity;

	// Equipment:
	public int equipmentProjectileCooldown = 0;

    // Safe Position:
    /** The last coordinates the entity was at where it wasn't inside an opaque block. (Helps prevent suffocation). **/
    Vector3d lastSafePos;
    private boolean playerAllowFlyingSnapshot;
    private boolean playerIsFlyingSnapshot;

    // Last Attacked:
	public LivingEntity lastAttackedEntity;
	public int lastAttackedTime = 0;
	
	// Picked Up:
	public Entity pickedUpByEntity;

	// Percher
	public Entity perchedByEntity;
	public boolean perchedEntityNoclip = false;

    // Fear:
	public FearEntity fearEntity;

    // Force Remove:
    boolean forceRemoveChecked = false;
    boolean forceRemove = false;
    int forceRemoveTicks = FORCE_REMOVE_ENTITY_TICKS;
	
	// ==================================================
    //                   Get for Entity
    // ==================================================
	public static ExtendedEntity getForEntity(LivingEntity entity) {
		if(entity == null) {
			//LycanitesMobs.logWarning("", "Tried to access an ExtendedEntity from a null Entity.");
			return null;
		}

        // Client Side:
		if(entity.getEntityWorld().isRemote) {
            if(clientExtendedEntities.containsKey(entity)) {
                ExtendedEntity extendedEntity = clientExtendedEntities.get(entity);
                extendedEntity.setEntity(entity);
                return extendedEntity;
            }
            ExtendedEntity extendedEntity = new ExtendedEntity();
            extendedEntity.setEntity(entity);
            clientExtendedEntities.put(entity, extendedEntity);
            return extendedEntity;
        }

        // Server Side:
        IExtendedEntity iExtendedEntity = entity.getCapability(LycanitesMobs.EXTENDED_ENTITY, null).orElse(null);
        if(!(iExtendedEntity instanceof ExtendedEntity))
			return null;
        ExtendedEntity extendedEntity = (ExtendedEntity)iExtendedEntity;
        if(extendedEntity.getEntity() != entity)
            extendedEntity.setEntity(entity);
        return extendedEntity;
	}
	
	
	// ==================================================
    //                    Constructor
    // ==================================================
	public ExtendedEntity() {

	}


    // ==================================================
    //                      Entity
    // ==================================================
    /** Initially sets the entity. **/
    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

	public void setLastAttackedEntity(LivingEntity target) {
		this.lastAttackedEntity = target;
		this.lastAttackedTime = this.entity.ticksExisted;
	}
	
	
	// ==================================================
    //                      Update
    // ==================================================
	public void onUpdate() {
        if(this.entity == null)
            return;

        // Equipment:
		if(this.equipmentProjectileCooldown > 0)
			this.equipmentProjectileCooldown--;

        // Force Remove Entity:
		ExtendedEntity.FORCE_REMOVE_ENTITY_IDS = ConfigAdmin.INSTANCE.forceRemoveEntityIds.get();
		ExtendedEntity.FORCE_REMOVE_ENTITY_TICKS = 40;
        if (!this.entity.getEntityWorld().isRemote && FORCE_REMOVE_ENTITY_IDS != null && FORCE_REMOVE_ENTITY_IDS.size() > 0 && !this.forceRemoveChecked) {
            LycanitesMobs.logDebug("ForceRemoveEntity", "Forced entity removal, checking: " + this.entity.getName());
            for (String forceRemoveID : FORCE_REMOVE_ENTITY_IDS) {
                if (forceRemoveID.equalsIgnoreCase(this.entity.getType().getRegistryName().toString())) {
                    this.forceRemove = true;
                    break;
                }
            }
            this.forceRemoveChecked = true;
        }
        if (this.forceRemove && this.forceRemoveTicks-- <= 0)
            this.entity.remove();

        // Safe Position:
		if (this.lastSafePos == null) {
			this.lastSafePos = new Vector3d(this.entity.posX, this.entity.posY, this.entity.posZ);
		}
		if (!this.entity.getEntityWorld().getBlockState(this.entity.getPosition()).getMaterial().isSolid()) {
			this.lastSafePos.set(Math.floor(this.entity.posX) + 0.5D, this.entity.getPosition().getY(), Math.floor(this.entity.posZ) + 0.5D);
		}

        // Fear Entity:
        if (this.fearEntity != null && !this.fearEntity.isAlive())
            this.fearEntity = null;

        // Picked Up By Entity:
		try {
			this.updatePickedUpByEntity();
		}
		catch (Exception e) {}

		// Perched By Entity:
		try {
			this.updatedPerchedByEntity();
		}
		catch (Exception e) {}
	}
	
	
	// ==================================================
    //                       Death
    // ==================================================
	public void onDeath() {
		this.setPickedUpByEntity(null);
	}
	
	
	// ==================================================
    //                 Picked Up By Entity
    // ==================================================
    public void updatePickedUpByEntity() {
        if(this.pickedUpByEntity == null)
            return;

        // Check:
		if(!this.entity.getEntityWorld().isRemote) {
			if (!this.pickedUpByEntity.isAlive()) {
				this.setPickedUpByEntity(null);
				return;
			}
			if (this.pickedUpByEntity instanceof LivingEntity) {
				if (((LivingEntity) this.pickedUpByEntity).getHealth() <= 0) {
					this.setPickedUpByEntity(null);
					return;
				}
			}
			Effect weight = ObjectManager.getEffect("weight");
			if (weight != null) {
				if (this.entity.isPotionActive(weight)) {
					this.setPickedUpByEntity(null);
					return;
				}
			}
			if (this.entity.getDistance(this.pickedUpByEntity) > 32D) {
				this.setPickedUpByEntity(null);
				return;
			}
		}

        // Movement:
		if(this.pickedUpByEntity != null) {
			double[] pickupOffset = this.getPickedUpOffset();
			this.entity.setPosition(this.pickedUpByEntity.posX + pickupOffset[0], this.pickedUpByEntity.posY + pickupOffset[1], this.pickedUpByEntity.posZ + pickupOffset[2]);
			this.entity.setMotion(this.pickedUpByEntity.getMotion());
			this.entity.fallDistance = 0;
			if (!this.entity.getEntityWorld().isRemote && this.entity instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) this.entity;
				player.abilities.allowFlying = true;
				this.entity.noClip = true;
			}
		}
    }

	public void setPickedUpByEntity(Entity pickedUpByEntity) {
        if(this.pickedUpByEntity == pickedUpByEntity || this.entity == null) {
			return;
		}

		this.pickedUpByEntity = pickedUpByEntity;

        // Server Side:
		if(!this.entity.getEntityWorld().isRemote) {

            // Player Flying:
			if(this.entity instanceof PlayerEntity) {
				if(pickedUpByEntity != null) {
                    this.playerAllowFlyingSnapshot = ((PlayerEntity) this.entity).abilities.allowFlying;
                    this.playerIsFlyingSnapshot = ((PlayerEntity)this.entity).abilities.isFlying;
                }
				else {
                    ((PlayerEntity)this.entity).abilities.allowFlying = this.playerAllowFlyingSnapshot;
                    ((PlayerEntity)this.entity).abilities.isFlying = this.playerIsFlyingSnapshot;
                    this.entity.noClip = false;
                }
			}

            // Teleport To Initial Pickup Position:
            if(this.pickedUpByEntity != null && !(this.entity instanceof PlayerEntity)) {
                double[] pickupOffset = this.getPickedUpOffset();
                this.entity.teleportKeepLoaded(this.pickedUpByEntity.posX + pickupOffset[0], this.pickedUpByEntity.posY + pickupOffset[1], this.pickedUpByEntity.posZ + pickupOffset[2]);
            }

			MessageEntityPickedUp message = new MessageEntityPickedUp(this.entity, pickedUpByEntity);
			LycanitesMobs.packetHandler.sendToWorld(message, this.entity.getEntityWorld());
		}

        // Safe Drop Position:
        if(pickedUpByEntity == null) {
            if(this.lastSafePos != null) {
                this.entity.setPosition(this.lastSafePos.getX(), this.lastSafePos.getY(), this.lastSafePos.getZ());
            }
            this.entity.setMotion(0, 0, 0);
            this.entity.fallDistance = 0;
        }
	}

	public double[] getPickedUpOffset() {
        double[] pickupOffset = new double[] {0, 0, 0};
        if(this.pickedUpByEntity instanceof BaseCreatureEntity) {
            pickupOffset = ((BaseCreatureEntity) this.pickedUpByEntity).getPickupOffset(this.entity);
        }
        if(CreatureManager.getInstance().config.disablePickupOffsets && this.entity instanceof PlayerEntity) {
            return new double[] {0, 0, 0};
        }
        return pickupOffset;
    }

    public boolean isPickedUp() {
        return this.pickedUpByEntity != null;
    }
	
	public boolean isFeared() {
		return this.pickedUpByEntity instanceof FearEntity;
	}


	// ==================================================
	//                     Perched
	// ==================================================
	/**
	 * Sets the entity that is perching on this entity.
	 * @param perchedByEntity The entity to perch on this entity or null to clear.
	 */
	public void setPerchedByEntity(Entity perchedByEntity) {
		if(this.perchedByEntity != null) {
			this.perchedByEntity.noClip = this.perchedEntityNoclip;
			if(this.perchedByEntity instanceof BaseCreatureEntity) {
				((BaseCreatureEntity) this.perchedByEntity).setPerchTarget(null);
			}
		}

		this.perchedByEntity = perchedByEntity;
		if(perchedByEntity != null) {
			this.perchedEntityNoclip = perchedByEntity.noClip;
			perchedByEntity.noClip = true;
			if(perchedByEntity instanceof BaseCreatureEntity) {
				((BaseCreatureEntity)perchedByEntity).setPerchTarget(this.entity);
			}
		}

		if(!this.entity.getEntityWorld().isRemote) {
			MessageEntityPerched message = new MessageEntityPerched(this.entity, this.perchedByEntity);
			LycanitesMobs.packetHandler.sendToWorld(message, this.entity.getEntityWorld());
		}
	}

	/**
	 * Returns the entity currently perching on this entity if any.
	 * @return The entity perching on this entity or null.
	 */
	public Entity getPerchedByEntity() {
		return this.perchedByEntity;
	}

	/**
	 * Returns the xyz position that an entity should perch on this entity at.
	 * @return The perch position.
	 */
	public Vec3d getPerchPosition() {
		double entityWidth = this.entity.getSize(this.entity.getPose()).width;
		double entityHeight = this.entity.getSize(this.entity.getPose()).height;

		double angle = Math.toRadians(this.entity.rotationYaw) + 90;
		double xPerchPos = this.entity.posX;
		double zPerchPos = this.entity.posZ;
		double distance = entityWidth * 0.7D;
		if(distance != 0) {
			xPerchPos += distance * -Math.sin(angle);
			zPerchPos += distance * Math.cos(angle);
		}

		return new Vec3d(
				xPerchPos,
				this.entity.posY + entityHeight * 0.78D,
				zPerchPos
		);
	}

	/**
	 * Updates the position of an entity that is perching on this entity.
	 */
	public void updatedPerchedByEntity() {
		Entity perchedByEntity = this.getPerchedByEntity();
		if(perchedByEntity != null) {
			Vec3d perchPosition = this.getPerchPosition();
			perchedByEntity.setPosition(perchPosition.getX(), perchPosition.getY(), perchPosition.getZ());
			perchedByEntity.setMotion(this.entity.getMotion());
			perchedByEntity.rotationYaw = this.entity.rotationYaw;
			perchedByEntity.noClip = true;
		}
	}


    // ==================================================
    //                        NBT
    // ==================================================
    // ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
    public void readNBT(CompoundNBT nbtTagCompound) {

    }

    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    public void writeNBT(CompoundNBT nbtTagCompound) {

    }
}
