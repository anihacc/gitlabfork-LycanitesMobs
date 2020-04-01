package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.goals.actions.PlayerControlGoal;
import com.lycanitesmobs.core.entity.goals.targeting.CopyRiderAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeRiderGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class RideableCreatureEntity extends TameableCreatureEntity {

    public Entity lastRiddenByEntity = null;

	// Jumping:
	public boolean mountJumping = false;
	public float jumpPower = 0.0F;

	public boolean abilityToggled = false;
	public boolean inventoryToggled = false;
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public RideableCreatureEntity(EntityType<? extends RideableCreatureEntity> entityType, World world) {
		super(entityType, world);
		this.hasJumpSound = true;
	}


	// ========== Init AI ==========
	@Override
	protected void registerGoals() {
		// Greater Actions:
		this.goalSelector.addGoal(this.nextPriorityGoalIndex++, new PlayerControlGoal(this));

		super.registerGoals();

		// Lesser Targeting:
		this.targetSelector.addGoal(this.nextReactTargetIndex++, new RevengeRiderGoal(this));
		this.targetSelector.addGoal(this.nextReactTargetIndex++, new CopyRiderAttackTargetGoal(this));
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
			// Rider Buffs:
			if(this.getControllingPassenger() instanceof LivingEntity) {
				LivingEntity riderLiving = (LivingEntity)this.getControllingPassenger();

				// Run Mount Rider Effects:
				this.riderEffects(riderLiving);

				// Protect Rider from Effects:
				if(!this.canBurn()) {
					riderLiving.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, (5 * 20) + 5, 1));
				}
				for(Object possibleEffect : riderLiving.getActivePotionEffects().toArray(new Object[0])) {
					if(possibleEffect instanceof EffectInstance) {
						EffectInstance effectInstance = (EffectInstance)possibleEffect;
						if(!this.isPotionApplicable(effectInstance))
							riderLiving.removePotionEffect(effectInstance.getPotion());
					}
				}
			}

			// Mount Melee:
			if(!this.getEntityWorld().isRemote && this.hasAttackTarget() && this.updateTick % 20 == 0) {
				LivingEntity mountedAttackTarget = this.getAttackTarget();
				if(mountedAttackTarget != null && this.canAttack(mountedAttackTarget) && this.getDistanceSq(mountedAttackTarget.getPositionVec().getX(), mountedAttackTarget.getBoundingBox().minY, mountedAttackTarget.getPositionVec().getZ()) <= this.getMeleeAttackRange(mountedAttackTarget, 1)) {
					this.attackMelee(this.getAttackTarget(), 1);
				}
			}

			// Dismounting:
			if (!this.getEntityWorld().isRemote && this.getControllingPassenger() instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) this.getControllingPassenger();
				ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
				if(playerExt != null && playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.MOUNT_DISMOUNT)) {
					player.stopRiding();
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
        for(EffectInstance effectInstance : rider.getActivePotionEffects().toArray(new EffectInstance[rider.getActivePotionEffects().size()])) {
        	if(!this.isPotionApplicable(effectInstance) && ObjectLists.inEffectList("debuffs", effectInstance.getPotion())) {
        		rider.removePotionEffect(effectInstance.getPotion());
			}
		}
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    public void mountAbility(Entity rider) {}

    public void onDismounted(Entity entity) {
		if(this.isSitting()) {
			int homeY = MathHelper.floor(this.getPositionVec().getY());
			if(!this.isFlying()) {
				homeY = this.getGroundY(this.getPosition());
			}
			this.setHomePosition(MathHelper.floor(this.getPositionVec().getX()), homeY, MathHelper.floor(this.getPositionVec().getZ()));
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
            this.getControllingPassenger().setPosition(this.getPositionVec().getX() + mountOffset.x, this.getPositionVec().getY() + this.getMountedYOffset() + passenger.getYOffset(), this.getPositionVec().getZ() + mountOffset.z);
        }
    }

    private void mount(Entity entity) {
    	entity.rotationYaw = this.rotationYaw;
    	entity.rotationPitch = this.rotationPitch;
        if(!this.getEntityWorld().isRemote)
        	entity.startRiding(this);
    }

    public float getStafeSpeed() {
		return 0.5F;
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
            strafe = rider.moveStrafing * this.getStafeSpeed();
            forward = rider.moveForward;
        }

        // Swimming / Flying Controls:
        double verticalMotion = 0;
        if(this.isInWater() || this.isInLava() || this.isFlying()) {
            if (this.getControllingPassenger() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) this.getControllingPassenger();
                ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
                if(playerExt != null && playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.JUMP)) {
                    verticalMotion = this.creatureStats.getSpeed() * 20;
                }
				else if(playerExt != null && playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.DESCEND)) {
                    verticalMotion = -this.creatureStats.getSpeed() * 20;
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
                if (this.isPotionActive(Effects.JUMP_BOOST))
					this.setMotion(this.getMotion().add(0, ((float) (this.getActivePotionEffect(Effects.JUMP_BOOST).getAmplifier() + 1) * 0.1F), 0));
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
				else if(this.isInWater()) {
					if(!this.canBreatheUnderwater()) {
						verticalMotion *= 0.25f;
						strafe *= 0.25f;
						forward *= 0.25f;
					}
					this.moveRelative(0.1F, new Vec3d(strafe, 0, forward));
					this.move(MoverType.SELF, this.getMotion().add(0, verticalMotion / 16, 0));
					this.setMotion(this.getMotion().mul(0.8999999761581421D, 0.8999999761581421D, 0.8999999761581421D));
				}
				else if(this.isInLava()) {
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
        double d0 = this.getPositionVec().getX() - this.prevPosX;
        double d1 = this.getPositionVec().getZ() - this.prevPosZ;
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
    public HashMap<Integer, String> getInteractCommands(PlayerEntity player, @Nonnull ItemStack itemStack) {
    	HashMap<Integer, String> commands = new HashMap<Integer, String>();
    	commands.putAll(super.getInteractCommands(player, itemStack));
    	
    	// Mount:
        boolean mountingAllowed = CreatureManager.getInstance().config.mountingEnabled;
        if(mountingAllowed && this.isFlying())
            mountingAllowed = CreatureManager.getInstance().config.mountingFlightEnabled;
    	if(this.canBeMounted(player) && !player.isShiftKeyDown() && !this.getEntityWorld().isRemote && mountingAllowed) // isSneaking()
    		commands.put(COMMAND_PIORITIES.MAIN.id, "Mount");
    	
    	return commands;
    }
    
    // ========== Perform Command ==========
    @Override
    public boolean performCommand(String command, PlayerEntity player, ItemStack itemStack) {
    	
    	// Mount:
    	if(command.equals("Mount")) {
    		this.playMountSound();
            this.clearMovement();
            this.setAttackTarget(null);
            this.mount(player);
            return true;
    	}
    	
    	return super.performCommand(command, player, itemStack);
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
            if(rider != null && target != null)
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
    public boolean attackEntityFrom(DamageSource damageSource, float damageAmount) {
        Entity entity = damageSource.getTrueSource();
        return this.getControllingPassenger() != null && this.isRidingOrBeingRiddenBy(entity) ? false : super.attackEntityFrom(damageSource, damageAmount);
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
    	this.playSound(ObjectManager.getSound(this.creatureInfo.getName() + "_mount"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
}
