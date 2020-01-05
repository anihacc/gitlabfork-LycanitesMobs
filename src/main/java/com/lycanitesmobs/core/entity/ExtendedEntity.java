package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.capabilities.IExtendedEntity;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.network.MessageEntityPerched;
import com.lycanitesmobs.core.network.MessageEntityPickedUp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.Vec3d;

import javax.vecmath.Vector3d;
import java.util.HashMap;
import java.util.Map;

public class ExtendedEntity implements IExtendedEntity {
    public static Map<Entity, ExtendedEntity> clientExtendedEntities = new HashMap<>();
    public static String[] FORCE_REMOVE_ENTITY_IDS;
    public static int FORCE_REMOVE_ENTITY_TICKS = 40;

    // Entity Instance:
	public EntityLivingBase entity;

	// Projectiles:
	protected Map<String, Integer> projectileCooldownsPrimary = new HashMap<>();
	protected Map<String, Integer> projectileCooldownsSecondary = new HashMap<>();

    // Safe Position:
    /** The last coordinates the entity was at where it wasn't inside an opaque block. (Helps prevent suffocation). **/
    Vector3d lastSafePos;
    private boolean playerAllowFlyingSnapshot;
    private boolean playerIsFlyingSnapshot;

	// Last Attacked:
	public EntityLivingBase lastAttackedEntity;
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
	public static ExtendedEntity getForEntity(EntityLivingBase entity) {
		if(entity == null) {
			//LycanitesMobs.logWarning("", "Tried to access an ExtendedEntity from a null Entity.");
			return null;
		}

        // Client Side:
        if(entity.getEntityWorld() != null && entity.getEntityWorld().isRemote) {
            if(clientExtendedEntities.containsKey(entity)) {
                ExtendedEntity extendedEntity = clientExtendedEntities.get(entity);
                extendedEntity.setEntity(entity);
                return extendedEntity;
            }
            ExtendedEntity extendedEntity = new ExtendedEntity();
            extendedEntity.setEntity(entity);
            clientExtendedEntities.put(entity, extendedEntity);
        }

        // Server Side:
        IExtendedEntity iExtendedEntity = null;
        try {
            iExtendedEntity = entity.getCapability(LycanitesMobs.EXTENDED_ENTITY, null);
        }
        catch(Exception e) {}
        if(iExtendedEntity == null || !(iExtendedEntity instanceof ExtendedEntity)) {
			return null;
		}
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
    public void setEntity(EntityLivingBase entity) {
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return this.entity;
    }

    public void setLastAttackedEntity(EntityLivingBase target) {
    	this.lastAttackedEntity = target;
    	this.lastAttackedTime = this.entity.ticksExisted;
	}
	
	
	// ==================================================
    //                      Update
    // ==================================================
	public void onUpdate() {
        if(this.entity == null)
            return;

        // Projectiles:
		for(String cooldownName : this.projectileCooldownsPrimary.keySet()) {
			int cooldownValue = this.projectileCooldownsPrimary.get(cooldownName);
			if(cooldownValue > 0) {
				this.projectileCooldownsPrimary.put(cooldownName, cooldownValue - 1);
			}
		}
		for(String cooldownName : this.projectileCooldownsSecondary.keySet()) {
			int cooldownValue = this.projectileCooldownsSecondary.get(cooldownName);
			if(cooldownValue > 0) {
				this.projectileCooldownsSecondary.put(cooldownName, cooldownValue - 1);
			}
		}

        // Force Remove Entity:
        if (!this.entity.getEntityWorld().isRemote && FORCE_REMOVE_ENTITY_IDS != null && FORCE_REMOVE_ENTITY_IDS.length > 0 && !this.forceRemoveChecked) {
            LycanitesMobs.logDebug("ForceRemoveEntity", "Forced entity removal, checking: " + this.entity.getName());
            for (String forceRemoveID : FORCE_REMOVE_ENTITY_IDS) {
                if (forceRemoveID.equalsIgnoreCase(this.entity.getName())) {
                    this.forceRemove = true;
                    break;
                }
            }
            this.forceRemoveChecked = true;
        }
        if (this.forceRemove && this.forceRemoveTicks-- <= 0)
            this.entity.setDead();

        // Safe Position:
		if (this.lastSafePos == null) {
			this.lastSafePos = new Vector3d(this.entity.posX, this.entity.posY, this.entity.posZ);
		}
		if (!this.entity.getEntityWorld().getBlockState(this.entity.getPosition()).getMaterial().isSolid()) {
			this.lastSafePos.set(Math.floor(this.entity.posX) + 0.5D, this.entity.getPosition().getY(), Math.floor(this.entity.posZ) + 0.5D);
		}

        // Fear Entity:
        if (this.fearEntity != null && !this.fearEntity.isEntityAlive())
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
			if (!this.pickedUpByEntity.isEntityAlive()) {
				this.setPickedUpByEntity(null);
				return;
			}
			if (this.pickedUpByEntity instanceof EntityLivingBase) {
				if (((EntityLivingBase) this.pickedUpByEntity).getHealth() <= 0) {
					this.setPickedUpByEntity(null);
					return;
				}
			}
			Potion weight = ObjectManager.getEffect("weight");
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
			this.entity.motionX = this.pickedUpByEntity.motionX;
			this.entity.motionY = this.pickedUpByEntity.motionY;
			this.entity.motionZ = this.pickedUpByEntity.motionZ;
			this.entity.fallDistance = 0;
			if (!this.entity.getEntityWorld().isRemote && this.entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) this.entity;
				player.capabilities.isFlying = true;
				this.entity.noClip = true;
			}
		}
    }

	public void setPickedUpByEntity(Entity pickedUpByEntity) {
        if(this.pickedUpByEntity == pickedUpByEntity || this.entity == null) {
			return;
		}

		if(this.entity.getRidingEntity() != null) {
			this.entity.dismountRidingEntity();
		}
		this.pickedUpByEntity = pickedUpByEntity;

        // Server Side:
		if(!this.entity.getEntityWorld().isRemote) {

            // Player Flying:
			if(this.entity instanceof EntityPlayer) {
				if(pickedUpByEntity != null) {
                    this.playerAllowFlyingSnapshot = ((EntityPlayer) this.entity).capabilities.allowFlying;
                    this.playerIsFlyingSnapshot = ((EntityPlayer)this.entity).capabilities.isFlying;
                }
				else {
                    //((EntityPlayer)this.entity).capabilities.allowFlying = this.playerAllowFlyingSnapshot;
                    ((EntityPlayer)this.entity).capabilities.isFlying = this.playerIsFlyingSnapshot;
                    this.entity.noClip = false;
                }
			}

            // Teleport To Initial Pickup Position:
            if(this.pickedUpByEntity != null && !(this.entity instanceof EntityPlayer)) {
                double[] pickupOffset = this.getPickedUpOffset();
                this.entity.attemptTeleport(this.pickedUpByEntity.posX + pickupOffset[0], this.pickedUpByEntity.posY + pickupOffset[1], this.pickedUpByEntity.posZ + pickupOffset[2]);
            }

			MessageEntityPickedUp message = new MessageEntityPickedUp(this.entity, pickedUpByEntity);
			LycanitesMobs.packetHandler.sendToDimension(message, this.entity.dimension);
		}

        // Safe Drop Position:
        if(pickedUpByEntity == null) {
            if(this.lastSafePos != null) {
                this.entity.setPosition(this.lastSafePos.getX(), this.lastSafePos.getY(), this.lastSafePos.getZ());
            }
            this.entity.motionX = 0;
            this.entity.motionY = 0;
            this.entity.motionZ = 0;
            this.entity.fallDistance = 0;
        }
	}

	public double[] getPickedUpOffset() {
        double[] pickupOffset = new double[] {0, 0, 0};
        if(this.pickedUpByEntity instanceof BaseCreatureEntity) {
            pickupOffset = ((BaseCreatureEntity) this.pickedUpByEntity).getPickupOffset(this.entity);
        }
        if(CreatureManager.getInstance().config.disablePickupOffsets && this.entity instanceof EntityPlayer) {
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
	//                    Perched On
	// ==================================================
	/**
	 * Sets the entity that is perching on this entity.
	 * @param perchedByEntity The entity to perch on this entity or null to clear.
	 */
	public void setPerchedByEntity(Entity perchedByEntity) {
		if(this.perchedByEntity != null) {
			this.perchedByEntity.noClip = this.perchedEntityNoclip;
			if(this.perchedByEntity instanceof BaseCreatureEntity) {
				((BaseCreatureEntity)this.perchedByEntity).setPerchTarget(null);
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
			LycanitesMobs.packetHandler.sendToDimension(message, this.entity.dimension);
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
		double angle = Math.toRadians(this.entity.rotationYaw) + 90;
		double xPerchPos = this.entity.posX;
		double zPerchPos = this.entity.posZ;
		double distance = this.entity.width * 0.7D;
		if(distance != 0) {
			xPerchPos += distance * -Math.sin(angle);
			zPerchPos += distance * Math.cos(angle);
		}

		return new Vec3d(
				xPerchPos,
				this.entity.posY + this.entity.height * 0.78D,
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
			perchedByEntity.setPosition(perchPosition.x, perchPosition.y, perchPosition.z);
			perchedByEntity.motionX = this.entity.motionX;
			perchedByEntity.motionY = this.entity.motionY;
			perchedByEntity.motionZ = this.entity.motionZ;
			perchedByEntity.rotationYaw = this.entity.rotationYaw;
			perchedByEntity.noClip = true;
		}
	}


	// ==================================================
	//                     Projectiles
	// ==================================================
	/**
	 * Returns the projectile firing cooldown of the provided type and projectile name.
	 * @param type The type of cooldown, should be 1 for primary and 2 for secondary.
	 * @param projectileName The name of the projectile to get the firing cooldown of.
	 * @return The current firing cooldown.
	 */
	public int getProjectileCooldown(int type, String projectileName) {
		if(type == 1) {
			if(!this.projectileCooldownsPrimary.containsKey(projectileName)) {
				return 0;
			}
			return this.projectileCooldownsPrimary.get(projectileName);
		}

		if(!this.projectileCooldownsSecondary.containsKey(projectileName)) {
			return 0;
		}
		return this.projectileCooldownsSecondary.get(projectileName);
	}

	/**
	 * Set the projectile firing cooldown of the provided type and projectile name.
	 * @param type The type of cooldown, should be 1 for primary and 2 for secondary.
	 * @param projectileName The name of the projectile to set the firing cooldown of.
	 * @param cooldown The cooldown (in ticks) to set.
	 */
	public void setProjectileCooldown(int type, String projectileName, int cooldown) {
		if(type == 1) {
			this.projectileCooldownsPrimary.put(projectileName, cooldown);
		}
		this.projectileCooldownsSecondary.put(projectileName, cooldown);
	}


    // ==================================================
    //                        NBT
    // ==================================================
    // ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
    public void readNBT(NBTTagCompound nbtTagCompound) {

    }

    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    public void writeNBT(NBTTagCompound nbtTagCompound) {

    }
}
