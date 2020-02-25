package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;

import com.lycanitesmobs.client.model.template.ModelTemplateInsect;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTrite extends ModelTemplateInsect {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelTrite() {
        this(1.0F);
    }
    
    public ModelTrite(float shadowSize) {
    	// Load Model:
        this.initModel("trite", LycanitesMobs.modInfo, "entity/trite");

		// Scales:
		this.mouthScaleX = 1.0F;
		this.mouthScaleY = 0.1F;
    	
    	// Trophy:
        this.trophyScale = 1.0F;
    }
    
    
    // ==================================================
   	//                 Animate Part
   	// ==================================================
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

    	// Walking Bob:
		if(partName.equals("body")) {
			float bob = MathHelper.cos(time * 0.6662F + (float) Math.PI) * 0.3F * distance;
			if (bob < 0)
				bob += -bob * 2;
			translate(0, bob, 0);
		}
    }
}
