package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateElemental;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import com.lycanitesmobs.client.renderer.CreatureRenderer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.vecmath.Vector4f;

@OnlyIn(Dist.CLIENT)
public class ModelZephyr extends ModelTemplateElemental {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelZephyr() {
        this(1.0F);
    }

    public ModelZephyr(float shadowSize) {
    	// Load Model:
    	this.initModel("zephyr", LycanitesMobs.modInfo, "entity/zephyr");
    	
    	// Trophy:
        this.trophyScale = 1.2F;
        this.trophyOffset = new float[] {0.0F, -0.2F, 0.2F};
    }


	// ==================================================
	//             Add Custom Render Layers
	// ==================================================
	@Override
	public void addCustomLayers(CreatureRenderer renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerCreatureEffect(renderer, "glow", true, LayerCreatureEffect.BLEND.ADD.id, true));
		renderer.addLayer(new LayerCreatureEffect(renderer, "pulse01", true, LayerCreatureEffect.BLEND.ADD.id, true));
		renderer.addLayer(new LayerCreatureEffect(renderer, "pulse02", true, LayerCreatureEffect.BLEND.ADD.id, true));
		renderer.addLayer(new LayerCreatureEffect(renderer, "pulse03", true, LayerCreatureEffect.BLEND.ADD.id, true));
	}
    
    
    // ==================================================
   	//                 Animate Part
   	// ==================================================
    @Override
    public void animatePart(String partName, LivingEntity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		if("effectouter".equals(partName) || "effectinner".equals(partName)) {
			this.rotate(-15, 0, 0);
		}

		if(partName.equals("armeffectleft") || partName.equals("armeffectright")) {
			float angleX = 35f;
			float angleY = -45f;
			float angleZ = 140f;
			if(partName.equals("armeffectright")) {
				angleX = -angleX;
			}
			this.angle(loop * 50F, angleX, angleY, angleZ);
		}

		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
    }


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part and layer. **/
	public Vector4f getPartColor(String partName, Entity entity, LayerCreatureBase layer, boolean trophy, float loop) {
		if(layer == null) {
			return super.getPartColor(partName, entity, layer, trophy, loop);
		}

		float alphaTime = 15;
		if("pulse02".equals(layer.name)) {
			alphaTime = 20;
		}
		if("pulse03".equals(layer.name)) {
			alphaTime = 25;
		}

		float alpha = (loop % alphaTime / alphaTime) * 2;
		if(alpha > 1) {
			alpha = -(alpha - 1);
		}

		return new Vector4f(1, 1, 1, alpha);
	}
}