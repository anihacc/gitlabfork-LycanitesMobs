package com.lycanitesmobs.core.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelObjOld;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@OnlyIn(Dist.CLIENT)
public class ModelEttin extends ModelObjOld {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelEttin() {
        this(1.0F);
    }
    
    public ModelEttin(float shadowSize) {
    	// Load Model:
    	this.initModel("Ettin", LycanitesMobs.modInfo, "entity/ettin");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("lefthead", 0.35F, 3.1F, 0.0F);
    	setPartCenter("righthead", -0.35F, 3.1F, 0.0F);
    	setPartCenter("body", 0F, 3.1F, 0.0F);
    	setPartCenter("leftarm", 0.8F, 2.75F, 0.0F);
    	setPartCenter("rightarm", -0.8F, 2.75F, 0.0F);
    	setPartCenter("leftleg", 0.35F, 1.25F, 0.0F);
    	setPartCenter("rightleg", -0.35F, 1.25F, 0.0F);
    	
    	// Trophy:
        this.trophyScale = 1.0F;
        this.trophyOffset = new float[] {0.0F, -0.2F, -0.2F};
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
    	if(partName.equals("leftarm")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("rightarm")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	
    	// Walking:
    	float walkSwing = 0.6F;
    	if(partName.equals("leftarm"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 2.0F * distance * 0.5F);
    	if(partName.equals("rightarm"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 2.0F * distance * 0.5F);
    	if(partName.equals("leftleg"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.4F * distance);
    	if(partName.equals("rightleg"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.4F * distance);
    	
    	// Attack:
    	if(entity instanceof EntityCreatureBase) {
    		if(((EntityCreatureBase)entity).isAttackOnCooldown()) {
    			if(partName.equals("leftarm"))
    				if(((EntityCreatureBase)entity).getAttackPhase() == 0)
    					rotX += 30;
    				else
    					rotX -= 30;
    			if(partName.equals("rightarm"))
    				if(((EntityCreatureBase)entity).getAttackPhase() == 0)
    					rotX -= 30;
    				else
    					rotX += 30;
    		}
    	}
    	
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
