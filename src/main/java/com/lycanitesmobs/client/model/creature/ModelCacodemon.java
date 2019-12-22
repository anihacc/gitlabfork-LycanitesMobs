package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.client.model.ModelCreatureObjOld;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelCacodemon extends ModelCreatureObjOld {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelCacodemon() {
        this(1.0F);
    }
    
    public ModelCacodemon(float shadowSize) {
    	// Load Model:
    	this.initModel("cacodemon", LycanitesMobs.modInfo, "entity/cacodemon");

    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.0F, 0F);
    	setPartCenter("topmouth", 0F, 1.0F, 0F);
    	setPartCenter("bottommouth", 0F, 1.0F, 0F);
    	
    	this.lockHeadX = true;
    	this.lockHeadY = true;

        // Trophy:
        this.trophyScale = 0.5F;
    }
    
    
    // ==================================================
   	//                 Animate Part
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

		// Looking:
		if(partName.equals("head")) {
			rotX += Math.toDegrees(lookX / (180F / (float) Math.PI));
			rotY += Math.toDegrees(lookY / (180F / (float) Math.PI));
		}
		else {
			this.centerPartToPart(partName, "head");
			this.rotate((float)Math.toDegrees(lookX / (180F / (float)Math.PI)), 0, 0);
			this.rotate(0, (float)Math.toDegrees(lookY / (180F / (float)Math.PI)), 0);
			this.uncenterPartToPart(partName, "head");
		}
		
    	// Mouth:
		float mouthIdle = (float)Math.toDegrees(MathHelper.cos(loop * 0.05F) * 0.1F);
    	if(partName.equals("topmouth")) {
			rotate(-5F - mouthIdle, rotY, rotZ);
		}
		if(partName.equals("bottommouth")) {
			rotate(5F + mouthIdle, rotY, rotZ);
		}
    	
		// Attack:
    	if((entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown())
    			|| (entity instanceof AgeableCreatureEntity && ((AgeableCreatureEntity)entity).isInLove())) {
			if(partName.equals("topmouth"))
				rotate(-25F, rotY, rotZ);
			if(partName.equals("bottommouth"))
				rotate(25F, rotY, rotZ);
			rotate(25F / 2, rotY, rotZ);
		}
		
		// Sit:
    	if((entity instanceof TameableCreatureEntity && ((TameableCreatureEntity)entity).isSitting())) {
			if(partName.equals("topmouth"))
				rotate(5F, rotY, rotZ);
			if(partName.equals("bottommouth"))
				rotate(-5F, rotY, rotZ);
		}
		
    	// Apply Animations:
    	angle(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
