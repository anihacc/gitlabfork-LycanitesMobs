package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.client.model.ModelCreatureObjOld;
import com.lycanitesmobs.client.renderer.CreatureRenderer;

import com.lycanitesmobs.client.renderer.layer.specific.LayerYaleWool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelYale extends ModelCreatureObjOld {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelYale() {
        this(1.0F);
    }
    
    public ModelYale(float shadowSize) {
    	// Load Model:
    	this.initModel("yale", LycanitesMobs.modInfo, "entity/yale");

    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 0.85F, 1.0F);
    	setPartCenter("body", 0F, 1.0F, 0F);
    	setPartCenter("fur", 0F, 1.0F, 0F);
    	setPartCenter("armleft", 0.25F, 0.55F, 0.8F);
    	setPartCenter("armright", -0.25F, 0.55F, 0.8F);
    	setPartCenter("legleftfront", 0.2F, 1.2F, 0.85F);
    	setPartCenter("legrightfront", -0.2F, 1.2F, 0.85F);
    	setPartCenter("legleftback", 0.3F, 0.4F, -0.7F);
    	setPartCenter("legrightback", -0.3F, 0.4F, -0.7F);
    	
    	// Trophy:
        this.trophyScale = 1.0F;
        this.trophyOffset = new float[] {0.0F, -0.15F, -0.4F};
    }


    // ==================================================
    //             Add Custom Render Layers
    // ==================================================
    @Override
    public void addCustomLayers(CreatureRenderer renderer) {
        super.addCustomLayers(renderer);
        renderer.addLayer(new LayerYaleWool(renderer));
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
    	
    	// Idle:
    	if(partName.equals("armleft")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.2F);
    	}
    	if(partName.equals("armright")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.2F);
    	}
    	
    	// Walking:
    	if(entity == null || entity.onGround || entity.isInWater()) {
	    	float walkSwing = 0.6F;
	    	if(partName.equals("armleft")) {
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.0F * distance * 0.5F);
				rotZ -= Math.toDegrees(MathHelper.cos(time * walkSwing) * 0.5F * distance * 0.5F);
	    	}
	    	if(partName.equals("armright")) {
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.0F * distance * 0.5F);
				rotZ += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 0.5F * distance * 0.5F);
	    	}
	    	if(partName.equals("legleftfront") || partName.equals("legrightback"))
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.4F * distance);
	    	if(partName.equals("legrightfront") || partName.equals("legleftback"))
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.4F * distance);
    	}
		
		// Attack:
		if(entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown()) {
	    	if(partName.equals("armleft") || partName.equals("armright"))
	    		rotX += 20.0F;
		}
		
		// Jump:
		if(entity != null && !entity.onGround && !entity.isInWater()) {
	    	if(partName.equals("armleft")) {
		        rotZ -= 10;
		        rotX -= 50;
	    	}
	    	if(partName.equals("armright")) {
		        rotZ += 10;
		        rotX -= 50;
	    	}
	    	if(partName.equals("legleftfront") || partName.equals("legrightfront"))
	    		rotX += 50;
	    	if(partName.equals("legleftback") || partName.equals("legrightback"))
	    		rotX -= 50;
		}
    	
    	// Apply Animations:
		this.rotate(rotation, angleX, angleY, angleZ);
    	this.rotate(rotX, rotY, rotZ);
    	this.translate(posX, posY, posZ);
    }

    @Override
	public boolean canBaseRenderPart(String partName, Entity entity, boolean trophy) {
    	if("fur".equals(partName))
    		return false;
		return super.canBaseRenderPart(partName, entity, trophy);
	}
}
