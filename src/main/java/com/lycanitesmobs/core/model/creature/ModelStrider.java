package com.lycanitesmobs.core.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelCreatureObjOld;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelStrider extends ModelCreatureObjOld {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelStrider() {
        this(1.0F);
    }

    public ModelStrider(float shadowSize) {
    	// Load Model:
    	this.initModel("strider", LycanitesMobs.modInfo, "entity/strider");

    	// Set Rotation Centers:
    	setPartCenter("body", 0F, 8.0F, 0F);

        setPartCenter("armleft", 0.4F, 9F, -1.8F);
        setPartCenter("armright", -0.4F, 9F, -1.8F);

    	setPartCenter("legleft", 1.4F, 7.8F, 0F);
        setPartCenter("legright", -1.4F, 7.8F, 0F);
    	setPartCenter("legback", 0F, 8F, 0.8F);

        this.lockHeadX = true;
        this.lockHeadY = true;

        // Trophy:
        this.trophyScale = 0.4F;
        this.trophyOffset = new float[] {0.0F, -0.2F, 0.0F};
        this.bodyIsTrophy = true;
    }
    
    
    // ==================================================
   	//                    Animate Part
   	// ==================================================
    float maxLeg = 0F;
    @Override
    public void animatePart(String partName, LivingEntity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
    	float pi = (float)Math.PI;
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
        if(partName.equals("armleft")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
            rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
        }
        if(partName.equals("armright")) {
            rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
            rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
        }
		
    	// Walking:
    	float walkSwing = 0.15F;
    	if(partName.equals("legleft"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * (distance / 2));
    	if(partName.equals("legright"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * (distance / 2));
        if(partName.equals("legback"))
            rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);

        // Attack:
        if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).isAttackOnCooldown()) {
            if(partName.equals("armleft"))
                rotate(-25.0F, 0.0F, 0.0F);
            if(partName.equals("armright"))
                rotate(-25.0F, 0.0F, 0.0F);
        }

        // Pickup:
        if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).hasPickupEntity()) {
            if (partName.equals("armleft") || partName.equals("armright")) {
                rotX += 20D;
            }
        }
    	
    	// Apply Animations:
		this.rotate(rotation, angleX, angleY, angleZ);
    	this.rotate(rotX, rotY, rotZ);
    	this.translate(posX, posY, posZ);
    }


    // ==================================================
    //              Rotate and Translate
    // ==================================================
    /*@Override
    public void childScale(String partName) {
        if(partName.equals("head"))
            translate(-(getPartCenter(partName)[0] / 2), -(getPartCenter(partName)[1] / 2), -(getPartCenter(partName)[2] / 2));
        else
            super.childScale(partName);
    }*/
}
