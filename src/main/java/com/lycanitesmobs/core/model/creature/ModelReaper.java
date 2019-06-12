package com.lycanitesmobs.core.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelObjOld;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@OnlyIn(Dist.CLIENT)
public class ModelReaper extends ModelObjOld {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelReaper() {
        this(1.0F);
    }
    
    public ModelReaper(float shadowSize) {
    	// Load Model:
    	this.initModel("phantom", LycanitesMobs.modInfo, "entity/reaper");

    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.8F, 0F);
    	setPartCenter("body", 0F, 1.0F, 0F);
    	setPartCenter("armleft", 0.3F, 1.4F, 0F);
    	setPartCenter("armright", -0.3F, 1.4F, 0F);

        // Tropy:
        this.trophyScale = 1.2F;
        this.trophyOffset = new float[] {0.0F, 0.0F, 0.0F};
    }
    
    
    // ==================================================
   	//                 Animate Part
   	// ==================================================
    float maxLeg = 0F;
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
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
		float bob = -MathHelper.sin(loop * 0.1F) * 0.3F;
		posY += bob;
				
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).isAttackOnCooldown()) {
	    	if(partName.equals("armleft"))
	    		rotate(0.0F, -25.0F, 0.0F);
	    	if(partName.equals("armright"))
	    		rotate(0.0F, 25.0F, 0.0F);
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
