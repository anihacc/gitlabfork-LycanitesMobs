package com.lycanitesmobs.client.model.template;

import com.lycanitesmobs.client.model.ModelCreatureObj;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class ModelTemplateBiped extends ModelCreatureObj {

    // ==================================================
    //                 Animate Part
    // ==================================================
    @Override
    public void animatePart(String partName, LivingEntity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
        super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
        float posX = 0F;
        float posY = 0F;
        float posZ = 0F;
        float angleX = 0F;
        float angleY = 0F;
        float angleZ = 0F;
        float rotation = 0F;
        float rotX = 0F;
        float rotY = 0F;
        float rotZ = 0F;

        BaseCreatureEntity creatureEntity = null;
        if(entity instanceof BaseCreatureEntity)
            creatureEntity = (BaseCreatureEntity)entity;

        // Idle:
        if(partName.equals("mouth")) {
            this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F - 0.05F), 0.0F, 0.0F);
        }
        if(partName.equals("neck")) {
            this.rotate((float) -Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F - 0.05F), 0.0F, 0.0F);
        }
        if(partName.contains("armleft")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
            rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
        }
        if(partName.contains("armright")) {
            rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
            rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
        }
        if(partName.equals("tail")) {
            rotX = (float)-Math.toDegrees(MathHelper.cos((loop + time) * 0.1F) * 0.05F - 0.05F);
            rotY = (float)-Math.toDegrees(MathHelper.cos((loop + time) * 0.09F) * 0.05F - 0.05F);
        }
        if(entity == null || entity.onGround || entity.isInWater()) {
            if(partName.equals("wingleft")) {
                rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
                rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
            }
            if(partName.equals("wingright")) {
                rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
                rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
            }
        }
        if(partName.contains("tail")) {
            float sine = 0;
            if(partName.equals("tail.002") || partName.equals("tail02")) {
                sine = 1;
            }
            else if(partName.equals("tail.003") || partName.equals("tail03")) {
                sine = 2;
            }
            else if(partName.equals("tail.004")) {
                sine = 3;
            }
            else if(partName.equals("tail.005")) {
                sine = 4;
            }
            else if(partName.equals("tail.006")) {
                sine = 5;
            }
            else if(partName.equals("tail.007")) {
                sine = 6;
            }
            sine = (MathHelper.sin(sine / 6) - 0.5F);
            float tailRotX = (float)-Math.toDegrees(MathHelper.cos((loop + time) * 0.1F) * 0.05F - 0.05F);
            float tailRotY = (float)-Math.toDegrees(MathHelper.cos((loop + time) * sine * 0.1F) * 0.4F);
            rotY += Math.toDegrees(MathHelper.cos(time * 0.25F) * distance);
            this.rotate(tailRotX, tailRotY, 0);
        }

		// Fingers:
		if(partName.contains("finger")) {
			if(partName.contains("thumb")) {
				this.rotate(-(float) Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.2F - 0.2F), 0, 0);
			}
			else {
				this.rotate((float) Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.2F - 0.2F), 0, 0);
			}
		}

        // Walking:
        if(entity == null || entity.onGround || entity.isInWater()) {
            float walkSwing = 0.6F;
            if(partName.contains("armleft") || partName.equals("wingright")) {
                rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.0F * distance * 0.5F);
                rotZ -= Math.toDegrees(MathHelper.cos(time * walkSwing) * 0.5F * distance * 0.5F);
            }
            if(partName.contains("armright") || partName.equals("wingleft")) {
                rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.0F * distance * 0.5F);
                rotZ += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 0.5F * distance * 0.5F);
            }

            if(partName.equals("legleft"))
                rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.4F * distance);
            if(partName.equals("legright"))
                rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.4F * distance);

            if(partName.contains("legleft0"))
                rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 0.6F * distance);
            if(partName.contains("legright0"))
                rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 0.6F * distance);
        }

        // Jumping/Flying:
        if(entity != null && !entity.onGround && !entity.isInWater()) {
            if(partName.equals("wingleft")) {
                rotX = 20;
                rotX -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
                rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
            }
            if(partName.equals("wingright")) {
                rotX = 20;
                rotX -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
                rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.4F + (float)Math.PI) * 0.6F);
            }
            if(entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isFlying()) {
                if (partName.equals("body")) {
                    float bob = MathHelper.sin(loop * 0.4F) * 0.15F;
                    posY += bob;
                }
            }
        }

        // Attack:
        if(creatureEntity != null && creatureEntity.hasAttackTarget()) {
            if (partName.equals("mouth")) {
                rotX += 20.0F;
            }
            if (partName.contains("armleft") || partName.contains("armright")) {
                rotX -= 80.0F * this.getAttackProgress();
            }
        }

        // Riding:
        if(entity.isPassenger()) {
            if (partName.equals("legleft"))
                rotX -= 50;
            if (partName.equals("legright"))
                rotX -= 50;
        }

        // Apply Animations:
        this.angle(rotation, angleX, angleY, angleZ);
        this.rotate(rotX, rotY, rotZ);
        this.translate(posX, posY, posZ);
    }
}
