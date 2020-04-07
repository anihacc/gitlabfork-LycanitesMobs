package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelCacodemon extends ModelTemplateBiped {

    public ModelCacodemon() {
        this(1.0F);
    }
    
    public ModelCacodemon(float shadowSize) {
    	this.initModel("cacodemon", LycanitesMobs.modInfo, "entity/cacodemon");
		this.flightBobScale = 0.1F;
		this.trophyScale = 0.5F;
    }

    float maxLeg = 0F;
    @Override
    public void animatePart(String partName, LivingEntity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		// Looking:
		float rotX = 0;
		float rotY = 0;
		if(partName.toLowerCase().equals("body")) {
			rotX += (Math.toDegrees(lookX / (180F / (float)Math.PI)) * 0.5F);
			rotY += (Math.toDegrees(lookY / (180F / (float)Math.PI))) * 0.5F;
		}
		if(partName.equals("eye")) {
			rotX += (Math.toDegrees(lookX / (180F / (float)Math.PI)) * this.lookNeckScaleX);
			rotY += (Math.toDegrees(lookY / (180F / (float)Math.PI))) * this.lookNeckScaleY;
		}
		this.rotate(rotX, rotY, 0);
    }
}
