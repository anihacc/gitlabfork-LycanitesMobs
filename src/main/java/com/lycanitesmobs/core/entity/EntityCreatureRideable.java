package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityCreatureRideable extends EntityCreatureTameable {

    public Entity lastRiddenByEntity = null;

	// Jumping:
	public boolean mountJumping = false;
	public float jumpPower = 0.0F;

	public boolean abilityToggled = false;
	public boolean inventoryToggled = false;
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public EntityCreatureRideable(World world) {
		super(world);
		this.hasJumpSound = true;
	}
	
	// ========== Init ==========
    @Override
    protected void registerData() {
        super.registerData();
    }
    
    
    // ==================================================
    //                       Update
    // ==================================================
    @Override
    public void livingTick() {
    	super.livingTick();

        if(this.lastRiddenByEntity != this.getControllingPassenger()) {
            if(this.lastRiddenByEntity != null)
                this.onDismounted(this.lastRiddenByEntity);
            this.lastRiddenByEntity = this.getControllingPassenger();
        }

		if(this.hasRiderTarget()) {
			if(this.getControllingPassenger() instanceof LivingEntity) {
				LivingEntity riderLiving = (LivingEntity)this.getControllingPassenger();

				// Run Mount Rider Effects:
				this.riderEffects(riderLiving);

				// Protect Rider from Potion Effects:
				for(Object possibleEffect : riderLiving.getActiveEffectInstances().toArray(new Object[0])) {
					if(possibleEffect instanceof EffectInstance) {
						EffectInstance effectInstance = (EffectInstance)possibleEffect;
						if(!this.isPotionApplicable(effectInstance))
							riderLiving.removeEffectInstance(effectInstance.getPotion());
					}
				}
			}
    	}
    	else {
    		this.abilityToggled = false;
			this.inventoryToggled = false;
    	}
    }
    
    public void riderEffects(LivingEntity rider) {
        if(!rider.canBreatheUnderwater() && this.canBreatheUnderwater() && rider.isInWater())
            rider.setAir(300);
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    public void mountAbility(Entity rider) {}

    public void onDismounted(Entity entity) {
		if(this.isSitting()) {
			int homeY = MathHelper.floor(this.posY);
			if(!this.isCurrentlyFlying()) {
				homeY = this.getGroundY(this.getPosition());
			}
			this.setHomePosition(MathHelper.floor(this.posX), homeY, MathHelper.floor(this.posZ));
		}
	}
	
    
	// ==================================================
  	//                     Movement
  	// ==================================================
    @Override
    public boolean canBePushed() {
        if(this.getControllingPassenger() != null)
            return false;
        return super.canBePushed();
    }

    @Override
    public boolean canBeSteered() {
	    if(this.getEntityWorld().isRemote)
	        return true;
        Entity entity = this.getControllingPassenger();
        return entity == this.getOwner();
    }
    
    @Override
    protected boolean isMovementBlocked() {
    	// This will disable AI, we don't want this though!
        //return this.hasRiderTarget() && this.isSaddled() ? true : false;
    	return super.isMovementBlocked();
    }
    
    @Override
    public void updatePassenger(Entity passenger) {
        if(this.isPassenger(passenger)) {
        	double zOffset = this.getMountedZOffset();
        	if(zOffset == 0) {
        		zOffset = 0.00001D;
			}
			Vec3d mountOffset = this.getFacingPositionDouble(0, 0, 0, zOffset, this.rotationYaw);
            this.getControllingPassenger().setPosition(this.posX + mountOffset.x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + mountOffset.z);
        }
    }

    private void mount(Entity entity) {
    	entity.rotationYaw = this.rotationYaw;
    	entity.rotationPitch = this.rotationPitch;
        if(!this.getEntityWorld().isRemote)
        	entity.startRiding(this);
    }
    
    // ========== Move with Heading ==========
    @Override
    public void travel(Vec3d direction) {
        // Check if Mounted:
        if (!this.isTamed() || !this.hasSaddle() || !this.hasRiderTarget() || !(this.getControllingPassenger() instanceof LivingEntity) || !this.riderControl()) {
            super.travel(direction);
            return;
        }
        this.moveMountedWithHeading(direction.getX(), direction.getY(), direction.getZ());
    }

    public void moveMountedWithHeading(double strafe, double up, double forward) {
        // Apply Rider Movement:
        if(this.getControllingPassenger() instanceof LivingEntity) {
            LivingEntity rider = (LivingEntity) this.getControllingPassenger();
            this.prevRotationYaw = this.rotationYaw = rider.rotationYaw;
            this.rotationPitch = rider.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
            strafe = rider.moveStrafing * 0.5F;
            forward = rider.moveForward;
        }

        // Swimming / Flying Controls:
        double verticalMotion = 0;
        if(this.isInWater() || this.isInLava() || this.isFlying()) {
            if (this.getControllingPassenger() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) this.getControllingPassenger();
                ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
                if (playerExt != null && playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.JUMP)) {
                    verticalMotion = this.creatureStats.getSpeed() * 20;
                }
                else if(player.rotationPitch > 0 && forward != 0.0F) {
                    verticalMotion = this.creatureStats.getSpeed() * 20 * -(player.rotationPitch / 90);
                }
                else {
                    verticalMotion = 0;
                }
            }
        }

        else {
            // Jumping Controls:
            if (!this.isMountJumping()) {
                if (this.getControllingPassenger() instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) this.getControllingPassenger();
                    ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
                    if (playerExt != null && playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.JUMP)) {
                        this.startJumping();
                    }
                }
            }

            // Jumping Behaviour:
            if (this.getJumpPower() > 0.0F && !this.isMountJumping() && this.canPassengerSteer()) {
                this.setMotion(this.getMotion().add(0, this.getMountJumpHeight() * (double) this.getJumpPower(), 0));
                if (this.isPotionActive(Effects.field_76430_j)) // Jump Boost
					this.setMotion(this.getMotion().add(0, ((float) (this.getActiveEffectInstance(Effects.field_76430_j).getAmplifier() + 1) * 0.1F), 0));
                this.setMountJumping(true);
                this.isAirBorne = true;
                if (forward > 0.0F) {
                    float f2 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F);
                    float f3 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
					this.setMotion(this.getMotion().add(-0.4F * f2 * this.jumpPower, 0, 0.4F * f3 * this.jumpPower));
                }
                if (!this.getEntityWorld().isRemote)
                    this.playJumpSound();
                this.setJumpPower(0);
                net.minecraftforge.common.ForgeHooks.onLivingJump(this);
            }
            this.jumpMovementFactor = (float) (this.getAIMoveSpeed() * this.getGlideScale());
        }

		// Ability Controls:
		if(this.getControllingPassenger() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)this.getControllingPassenger();
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			if(playerExt != null) {

				// Mount Ability:
				if (playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.MOUNT_ABILITY)) {
					this.mountAbility(player);
					this.abilityToggled = true;
				}
				else {
					this.abilityToggled = false;
				}

				// Player Inventory:
				if (playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.MOUNT_INVENTORY)) {
					if (!this.inventoryToggled)
						this.openGUI(player);
					this.inventoryToggled = true;
				}
				else {
					this.inventoryToggled = false;
				}
			}
		}

        // Apply Movement:
        if(this.canPassengerSteer()) {
            this.setAIMoveSpeed((float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            if(!this.useDirectNavigator()) {
                if(this.isFlying() && !this.isInWater() && !this.isInLava()) {
                    this.moveRelative(0.1F, new Vec3d(strafe, 0, forward));
                    this.move(MoverType.SELF, new Vec3d(this.getMotion().x, verticalMotion / 16, this.getMotion().z));
                    this.setMotion(this.getMotion().mul(0.8999999761581421D, 0.8999999761581421D, 0.8999999761581421D));
                }
                else if(this.isInWater() || this.isInLava()) {
					if(!this.isStrongSwimmer()) {
						verticalMotion *= 0.25f;
						strafe *= 0.25f;
						forward *= 0.25f;
					}
					this.moveRelative(0.1F, new Vec3d(strafe, 0, forward));
                    this.move(MoverType.SELF, this.getMotion().add(0, verticalMotion / 16, 0));
					this.setMotion(this.getMotion().mul(0.8999999761581421D, 0.8999999761581421D, 0.8999999761581421D));
                }
                else
                    super.travel(new Vec3d(strafe, up, forward));
            }
            else
                this.directNavigator.flightMovement(strafe, forward);
        }

        // Clear Jumping:
        if(this.onGround || this.isInWater() || this.isInLava()) {
            this.setJumpPower(0);
            this.setMountJumping(false);
        }

        // Animate Limbs:
        this.prevLimbSwingAmount = this.limbSwingAmount;
        double d0 = this.posX - this.prevPosX;
        double d1 = this.posZ - this.prevPosZ;
        float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
        if (f4 > 1.0F)
            f4 = 1.0F;
        this.limbSwingAmount += (f4 - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    // ========== Jumping Start ==========
    public void startJumping() {
    	this.setJumpPower();
    }
    
    // ========== Jumping ==========
    public double getMountJumpHeight() {
    	return 0.75D;
    }
    
    public boolean isMountJumping() {
    	return this.mountJumping;
    }
    
    public void setMountJumping(boolean set) {
    	this.mountJumping = set;
    }

    // ========== Jump Power ==========
    public void setJumpPower(int power) {
    	if(power < 0)
    		power = 0;
    	if(power > 99)
    		power = 99;
    	if(power < 90)
            this.jumpPower = 1.0F * ((float)power / 89.0F);
    	else
        	this.jumpPower = 1.0F + (1.0F * ((float)(power - 89) / 10.0F));
    }
    
    public void setJumpPower() {
    	this.setJumpPower(89);
    }
    
    public float getJumpPower() {
    	return this.jumpPower;
    }
    
    // ========== Gliding ==========
    public double getGlideScale() {
    	return 0.1F;
    }
    
    // ========== Rider Control ==========
    public boolean riderControl() {
    	return true;
    }
	
    
	// ==================================================
  	//                     Interact
  	// ==================================================
    // ========== Get Interact Commands ==========
    @Override
    public HashMap<Integer, String> getInteractCommands(PlayerEntity player, ItemStack itemStack) {
    	HashMap<Integer, String> commands = new HashMap<Integer, String>();
    	commands.putAll(super.getInteractCommands(player, itemStack));
    	
    	// Mount:
        boolean mountingAllowed = CreatureManager.getInstance().config.mountingEnabled;
        if(mountingAllowed && this.isFlying())
            mountingAllowed = CreatureManager.getInstance().config.mountingFlightEnabled;
    	if(this.canBeMounted(player) && !player.isSneaking() && !this.getEntityWorld().isRemote && mountingAllowed)
    		commands.put(COMMAND_PIORITIES.MAIN.id, "Mount");
    	
    	return commands;
    }
    
    // ========== Perform Command ==========
    @Override
    public void performCommand(String command, PlayerEntity player, ItemStack itemStack) {
    	
    	// Mount:
    	if(command.equals("Mount")) {
    		this.playMountSound();
            this.clearMovement();
            this.setAttackTarget(null);
            this.mount(player);
    	}
    	
    	super.performCommand(command, player, itemStack);
    }
    
    
    // ==================================================
    //                       Targets
    // ==================================================
    // ========== Teams ==========
    @Override
    public Team getTeam() {
        if(this.hasRiderTarget()) {
            LivingEntity rider = this.getRider();
            if(rider != null)
                return rider.getTeam();
        }
        return super.getTeam();
    }
    
    @Override
    public boolean isOnSameTeam(Entity target) {
        if(this.hasRiderTarget()) {
            LivingEntity rider = this.getRider();
            if(target == rider)
                return true;
            if(rider != null)
                return rider.isOnSameTeam(target);
        }
        return super.isOnSameTeam(target);
    }
    
    
    // ==================================================
    //                     Abilities
    // ==================================================
    public boolean canBeMounted(Entity entity) {
    	if(this.getControllingPassenger() != null)
    		return false;
    	
    	// Can Be Mounted By A Player:
    	if(this.isTamed() && entity instanceof PlayerEntity) {
    		PlayerEntity player = (PlayerEntity)entity;
    		if(player == this.getOwner())
    			return this.hasSaddle() && !this.isChild();
    	}
    	
    	// Can Be Mounted By Mobs:
    	else if(!this.isTamed() && !(entity instanceof PlayerEntity)) {
    		return !this.isChild();
    	}
    	
    	return false;
    }
    
    
	// ==================================================
  	//                     Equipment
  	// ==================================================
    public boolean hasSaddle() {
    	ItemStack saddleStack = this.inventory.getEquipmentStack("saddle");
    	return saddleStack != null && !saddleStack.isEmpty();
    }

	
	// ==================================================
  	//                    Immunities
  	// ==================================================
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damage) {
        Entity entity = damageSource.getTrueSource();
        return this.getControllingPassenger() != null && this.isRidingOrBeingRiddenBy(entity) ? false : super.attackEntityFrom(damageSource, damage);
    }
    
    @Override
    public float getFallResistance() {
    	return 2;
    }
    
    
    // ==================================================
   	//                       Sounds
   	// ==================================================
    // ========== Mount ==========
    public void playMountSound() {
    	this.playSound(AssetManager.getSound(this.creatureInfo.getName() + "_mount"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
}
