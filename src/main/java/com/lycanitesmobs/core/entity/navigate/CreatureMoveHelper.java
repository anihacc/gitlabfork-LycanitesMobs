package com.lycanitesmobs.core.entity.navigate;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class CreatureMoveHelper extends MovementController {

    protected EntityCreatureBase entityCreature;
    /** Used by flight movement for changing course, makes for smoother movement. **/
    protected int courseChangeCooldown;

    public CreatureMoveHelper(EntityCreatureBase entityCreatureBase) {
        super(entityCreatureBase);
        this.entityCreature = entityCreatureBase;
    }

    /** Called on update to move the entity. **/
    @Override
    public void tick() {
        // Rider:
        if(this.isControlledByRider()) {
            return;
        }

        // Swimming:
        if(this.entityCreature.isStrongSwimmer() && this.entityCreature.isInWater()) {
            this.onUpdateSwimming();
            return;
        }

        // Flying:
        if(this.entityCreature.isFlying() && !this.entityCreature.isInWater()) {
            this.onUpdateFlying();
            return;
        }

        // Walking:
        super.tick();
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
    /** Used by strong swimmers for fast, smooth movement. **/
    public void onUpdateSwimming() {
        if (this.field_188491_h == MovementController.Action.MOVE_TO && !this.entityCreature.getNavigator().noPath()) {
            double x = this.posX - this.entityCreature.posX;
            double y = this.posY - this.entityCreature.posY;
            double z = this.posZ - this.entityCreature.posZ;
            double distance = x * x + y * y + z * z;
            distance = (double) MathHelper.sqrt(distance);
            y = y / distance;
            float f = (float)(MathHelper.atan2(z, x) * (180D / Math.PI)) - 90.0F;
            this.entityCreature.rotationYaw = this.limitAngle(this.entityCreature.rotationYaw, f, 90.0F);
            this.entityCreature.renderYawOffset = this.entityCreature.rotationYaw;
            float f1 = (float)(this.speed * this.entityCreature.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            this.entityCreature.setAIMoveSpeed(this.entityCreature.getAIMoveSpeed() + (f1 - this.entityCreature.getAIMoveSpeed()) * 0.125F);

            double d4 = Math.sin((double)(this.entityCreature.ticksExisted + this.entityCreature.getEntityId()) * 0.5D) * 0.05D;
            double d5 = Math.cos((double)(this.entityCreature.rotationYaw * 0.017453292F));
            double d6 = Math.sin((double)(this.entityCreature.rotationYaw * 0.017453292F));
            double motionX = d4 * d5;
            double motionZ = d4 * d6;
            d4 = Math.sin((double)(this.entityCreature.ticksExisted + this.entityCreature.getEntityId()) * 0.75D) * 0.05D;
            double motionY = d4 * (d6 + d5) * 0.25D;
            motionY += (double)this.entityCreature.getAIMoveSpeed() * y * 0.1D;
            this.entityCreature.setMotion(this.entityCreature.getMotion().add(motionX, motionY, motionZ));

            LookController lookController = this.entityCreature.getLookHelper();
            double d7 = this.entityCreature.posX + x / distance * 2.0D;
            double d8 = (double)this.entityCreature.getEyeHeight() + this.entityCreature.posY + y / distance;
            double d9 = this.entityCreature.posZ + z / distance * 2.0D;
            double d10 = lookController.getLookPosX();
            double d11 = lookController.getLookPosY();
            double d12 = lookController.getLookPosZ();

            if (!lookController.getIsLooking()) {
                d10 = d7;
                d11 = d8;
                d12 = d9;
            }

            this.entityCreature.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
        }
        else {
            this.entityCreature.setAIMoveSpeed(0.0F);
        }
    }

    /** Used by flyers for swift, fast air movement. **/
    public void onUpdateFlying() {
        if (this.field_188491_h == MovementController.Action.MOVE_TO) {
            double xDistance = this.posX - this.entityCreature.posX;
            double yDistance = this.posY - this.entityCreature.posY;
            double zDistance = this.posZ - this.entityCreature.posZ;
            double distance = xDistance * xDistance + yDistance * yDistance + zDistance * zDistance;

            if (this.courseChangeCooldown-- <= 0) {
                this.courseChangeCooldown += this.entityCreature.getRNG().nextInt(5) + 2;
                distance = (double)MathHelper.sqrt(distance);
                if(distance >= 1D) {
                    this.entityCreature.setAIMoveSpeed((float)this.entityCreature.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
                    double speed = (this.entityCreature.getAIMoveSpeed() / 2.4D) * this.getSpeed();
                    double motionX = xDistance / distance * speed;
                    double motionY = yDistance / distance * speed;
                    double motionZ = zDistance / distance * speed;
                    this.entityCreature.setMotion(this.entityCreature.getMotion().add(motionX, motionY, motionZ));
                }
                else {
                    this.field_188491_h = MovementController.Action.WAIT;
                }
            }
        }

        // Look At Target or Movement Direction:
        if (this.entityCreature.getAttackTarget() != null) {
            LivingEntity entitylivingbase = this.entityCreature.getAttackTarget();
            double distanceX = entitylivingbase.posX - this.entityCreature.posX;
            double distanceZ = entitylivingbase.posZ - this.entityCreature.posZ;
            this.entityCreature.renderYawOffset = this.entityCreature.rotationYaw = -((float)MathHelper.atan2(distanceX, distanceZ)) * (180F / (float)Math.PI);
        }
        else if(this.field_188491_h == MovementController.Action.MOVE_TO) {
            this.entityCreature.renderYawOffset = this.entityCreature.rotationYaw = -((float)MathHelper.atan2(this.entityCreature.getMotion().getX(), this.entityCreature.getMotion().getZ())) * (180F / (float)Math.PI);
        }
    }
}
