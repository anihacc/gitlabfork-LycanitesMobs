package com.lycanitesmobs.core.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.template.ModelTemplateBiped;
import com.lycanitesmobs.core.renderer.layer.LayerCreatureEffect;
import com.lycanitesmobs.core.renderer.RenderCreature;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelShade extends ModelTemplateBiped {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelShade() {
        this(1.0F);
    }

    public ModelShade(float shadowSize) {
    	// Load Model:
    	this.initModel("shade", LycanitesMobs.modInfo, "entity/shade");

    	// Looking:
		this.lookHeadScaleX = 0.8F;
		this.lookHeadScaleY = 0.8F;
		this.lookNeckScaleX = 0.2F;
		this.lookNeckScaleY = 0.2F;

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, -0.2F, 0.0F};
    }


	// ==================================================
	//             Add Custom Render Layers
	// ==================================================
	@Override
	public void addCustomLayers(RenderCreature renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerCreatureEffect(renderer, "eyes", true, LayerCreatureEffect.BLEND.ADD.id, true));
	}
    
    
    // ==================================================
   	//                    Animate Part
   	// ==================================================
    @Override
    public void animatePart(String partName, LivingEntity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	if(entity instanceof EntityCreatureBase && entity.getControllingPassenger() != null) {
			time = time * 0.25F;
			distance = distance * 0.8F;
		}
    	super.animatePart(partName, entity, time, distance * 0.5F, loop, lookY, lookX, scale);

		if(partName.equals("mouth")) {
			this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.1F), 0.0F, 0.0F);
		}

		else if(partName.contains("tail")) {
			float sine = 0;
			if(partName.equals("tail.002")) {
				sine = 1;
			}
			else if(partName.equals("tail.003")) {
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
			float rotX = (float)-Math.toDegrees(MathHelper.cos((loop + time) * 0.1F) * 0.05F - 0.05F);
			float rotY = (float)-Math.toDegrees(MathHelper.cos((loop + time) * sine * 0.1F) * 0.4F);
			rotY += Math.toDegrees(MathHelper.cos(time * 0.25F) * distance);
			this.rotate(rotX, rotY, 0);
		}
    }
}
