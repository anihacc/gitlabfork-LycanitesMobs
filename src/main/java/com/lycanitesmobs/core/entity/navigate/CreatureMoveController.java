package com.lycanitesmobs.core.entity.navigate;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;

public class CreatureMoveController extends MovementController {

    protected BaseCreatureEntity entityCreature;
    /** Used by flight movement for changing course, makes for smoother movement. **/
    protected int courseChangeCooldown;

    public CreatureMoveController(BaseCreatureEntity baseCreatureEntity) {
        super(baseCreatureEntity);
        this.entityCreature = baseCreatureEntity;
    }

    /** Called on update to move the entity. **/
    @Override
    public void tick() {
        // Rider:
        if(this.isControlledByRider()) {
            return;
        }

        // Swimming:
        if(this.entityCreature.isStrongSwimmer() && this.entityCreature.canSwim()) {
            this.tickSwimming();
            return;
        }

        // Flying:
        if(this.entityCreature.isFlying() && !this.entityCreature.canSwim()) {
            this.tickFlying();
            return;
        }

        // Walking:
        this.tickWalking();
    }


    // ==================== Checks ====================
    /** Returns true if the entity is controlled by its rider. **/
    public boolean isControlledByRider() {
        // Mounted By Player:
        if(this.entityCreature != null && this.entityCreature.getControllingPassenger() instanceof PlayerEntity && this.entityCreature.canBeSteered()) {
            return true;
        }

        return false;
    }


    // ==================== Movements ====================
    /** Used by land entities for ground movement. **/
    public void tickWalking() {
        float moveZ;
        if (this.action == MovementController.Action.STRAFE) {
            float moveSpeed = (float)this.mob.getAttribute(Attributes.field_233821_d_).getValue();
            float scaledSpeed = (float)this.speed * moveSpeed;
            float moveForward = this.moveForward;
            float moveStrafe = this.moveStrafe;
            float velocity = MathHelper.sqrt(moveForward * moveForward + moveStrafe * moveStrafe);
            if (velocity < 1.0F) {
                velocity = 1.0F;
            }

            velocity = scaledSpeed / velocity;
            moveForward *= velocity;
            moveStrafe *= velocity;
            float yawSin = MathHelper.sin(this.mob.rotationYaw * 0.017453292F);
            float yawCos = MathHelper.cos(this.mob.rotationYaw * 0.017453292F);
            float moveX = moveForward * yawCos - moveStrafe * yawSin;
            moveZ = moveStrafe * yawCos + moveForward * yawSin;
            PathNavigator pathNavigator = this.mob.getNavigator();
            NodeProcessor nodeProcessor = pathNavigator.getNodeProcessor();
            if (nodeProcessor.getPathNodeType(this.mob.world, MathHelper.floor(this.mob.getPositionVec().getX() + (double) moveX), MathHelper.floor(this.mob.getPositionVec().getY()), MathHelper.floor(this.mob.getPositionVec().getZ() + (double) moveZ)) != PathNodeType.WALKABLE) {
                this.moveForward = 1.0F;
                this.moveStrafe = 0.0F;
                scaledSpeed = moveSpeed;
            }

            this.mob.setAIMoveSpeed(scaledSpeed);
            this.mob.setMoveForward(this.moveForward);
            this.mob.setMoveStrafing(this.moveStrafe);
            this.action = MovementController.Action.WAIT;
        }
        else if (this.action == MovementController.Action.MOVE_TO) {
            this.action = MovementController.Action.WAIT;
            double distanceX = this.posX - this.mob.getPositionVec().getX();
            double distanceZ = this.posZ - this.mob.getPositionVec().getZ();
            double distanceY = this.posY - this.mob.getPositionVec().getY();
            double distanceXZ = distanceX * distanceX + distanceZ * distanceZ;
            double distance = distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;
            if (distance < 2.500000277905201E-7D) {
                this.mob.setMoveForward(0.0F);
                return;
            }

            moveZ = (float)(MathHelper.atan2(distanceZ, distanceX) * 57.2957763671875D) - 90.0F;
            this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, moveZ, 90.0F);
            this.mob.setAIMoveSpeed((float)(this.speed * this.mob.getAttribute(Attributes.field_233821_d_).getValue()));

            // Jumping:
            BlockPos entityPos = new BlockPos(this.mob);
            BlockState blockState = this.mob.world.getBlockState(entityPos);
            VoxelShape collisionShape = blockState.getCollisionShape(this.mob.world, entityPos);
            double jumpRange = (double)Math.max(1.0F, this.mob.getSize(Pose.STANDING).width + 0.25F);
            if (distanceY > (double)this.mob.stepHeight && distanceXZ < jumpRange || !collisionShape.isEmpty() && this.mob.getPositionVec().getY() < collisionShape.getEnd(Direction.Axis.Y) + (double)entityPos.getY()) {
                this.mob.getJumpController().setJumping();
                this.action = MovementController.Action.JUMPING;
            }
        }
        else if (this.action == MovementController.Action.JUMPING) {
            this.mob.setAIMoveSpeed((float)(this.speed * this.mob.getAttribute(Attributes.field_233821_d_).getValue()));
            if (this.mob.onGround) {
                this.action = MovementController.Action.WAIT;
            }
        }
        else {
            this.mob.setMoveForward(0.0F);
        }
    }

    /** Used by strong swimmers for fast, smooth movement. **/
    public void tickSwimming() {
        if (this.action == MovementController.Action.MOVE_TO && !this.entityCreature.getNavigator().noPath()) {
            double x = this.posX - this.entityCreature.getPositionVec().getX();
            double y = this.posY - this.entityCreature.getPositionVec().getY();
            double z = this.posZ - this.entityCreature.getPositionVec().getZ();
            double distance = x * x + y * y + z * z;
            distance = (double) MathHelper.sqrt(distance);
            y = y / distance;
            float f = (float)(MathHelper.atan2(z, x) * (180D / Math.PI)) - 90.0F;
            this.entityCreature.rotationYaw = this.limitAngle(this.entityCreature.rotationYaw, f, 90.0F);
            this.entityCreature.renderYawOffset = this.entityCreature.rotationYaw;
            float f1 = (float)(this.speed * this.entityCreature.getAttribute(Attributes.field_233821_d_).getValue());
            this.entityCreature.setAIMoveSpeed(this.entityCreature.getAIMoveSpeed() + (f1 - this.entityCreature.getAIMoveSpeed()) * 0.125F);

            double d4 = Math.sin((double)(this.entityCreature.ticksExisted + this.entityCreature.getEntityId()) * 0.5D) * 0.05D;
            double d5 = Math.cos((double)(this.entityCreature.rotationYaw * 0.017453292F));
            double d6 = Math.sin((double)(this.entityCreature.rotationYaw * 0.017453292F));
            double motionX = d4 * d5;
            double motionZ = d4 * d6;
            d4 = Math.sin((double)(this.entityCreature.ticksExisted + this.entityCreature.getEntityId()) * 0.75D) * 0.05D;
            double motionY = d4 * (d6 + d5) * 0.25D;
            motionY += (double)this.entityCreature.getAIMoveSpeed() * y * 0.125D;
            this.entityCreature.setMotion(this.entityCreature.getMotion().add(motionX, motionY, motionZ));

            LookController lookController = this.entityCreature.getLookController();
            double d7 = this.entityCreature.getPositionVec().getX() + x / distance * 2.0D;
            double d8 = (double)this.entityCreature.getEyeHeight() + this.entityCreature.getPositionVec().getY() + y / distance;
            double d9 = this.entityCreature.getPositionVec().getZ() + z / distance * 2.0D;
            double d10 = lookController.getLookPosX();
            double d11 = lookController.getLookPosY();
            double d12 = lookController.getLookPosZ();

            if (!lookController.getIsLooking()) {
                d10 = d7;
                d11 = d8;
                d12 = d9;
            }

            this.entityCreature.getLookController().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
        }
        else {
            this.entityCreature.setAIMoveSpeed(0.0F);
        }
    }

    /** Used by flyers for swift, fast air movement. **/
    public void tickFlying() {
        if (this.action == MovementController.Action.MOVE_TO) {
            double xDistance = this.posX - this.entityCreature.getPositionVec().getX();
            double yDistance = this.posY - this.entityCreature.getPositionVec().getY();
            double zDistance = this.posZ - this.entityCreature.getPositionVec().getZ();
            double distance = xDistance * xDistance + yDistance * yDistance + zDistance * zDistance;

            if (this.courseChangeCooldown-- <= 0) {
                this.courseChangeCooldown += this.entityCreature.getRNG().nextInt(5) + 2;
                distance = (double)MathHelper.sqrt(distance);
                if(distance >= 1D) {
                    this.entityCreature.setAIMoveSpeed((float)this.entityCreature.getAttribute(Attributes.field_233821_d_).getValue());
                    double speed = (this.entityCreature.getAIMoveSpeed() / 2.4D) * this.getSpeed();
                    double motionX = xDistance / distance * speed;
                    double motionY = yDistance / distance * speed;
                    double motionZ = zDistance / distance * speed;
                    this.entityCreature.setMotion(this.entityCreature.getMotion().add(motionX, motionY, motionZ));
                }
                else {
                    this.action = MovementController.Action.WAIT;
                }
            }
        }

        // Look At Target or Movement Direction:
        if (this.entityCreature.getAttackTarget() != null) {
            LivingEntity entitylivingbase = this.entityCreature.getAttackTarget();
            double distanceX = entitylivingbase.getPositionVec().getX() - this.entityCreature.getPositionVec().getX();
            double distanceZ = entitylivingbase.getPositionVec().getZ() - this.entityCreature.getPositionVec().getZ();
            this.entityCreature.renderYawOffset = this.entityCreature.rotationYaw = -((float)MathHelper.atan2(distanceX, distanceZ)) * (180F / (float)Math.PI);
        }
        else if(this.action == MovementController.Action.MOVE_TO) {
            this.entityCreature.renderYawOffset = this.entityCreature.rotationYaw = -((float)MathHelper.atan2(this.entityCreature.getMotion().getX(), this.entityCreature.getMotion().getZ())) * (180F / (float)Math.PI);
        }
    }
}
