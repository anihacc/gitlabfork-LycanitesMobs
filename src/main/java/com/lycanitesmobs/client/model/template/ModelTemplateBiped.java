package com.lycanitesmobs.client.model.template;

import com.lycanitesmobs.client.model.ModelObj;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

public class ModelTemplateBiped extends ModelObj {

    // ==================================================
    //                 Animate Part
    // ==================================================
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
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

        // Idle:
        if(partName.equals("mouth")) {
            this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
        }
        if(partName.equals("neck")) {
            this.rotate((float) -Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
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
        }

        // Attack:
        if(partName.equals("mouth")) {
            rotX += 20.0F * this.getAttackProgress();
        }
        if(partName.equals("armleft") || partName.equals("armright")) {
            rotX -= 80.0F * this.getAttackProgress();
        }

        // Apply Animations:
        this.angle(rotation, angleX, angleY, angleZ);
        this.rotate(rotX, rotY, rotZ);
        this.translate(posX, posY, posZ);
    }
}
