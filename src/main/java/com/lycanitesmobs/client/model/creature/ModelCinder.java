package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateElemental;
import com.lycanitesmobs.client.renderer.CreatureRenderer;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelCinder extends ModelTemplateElemental {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelCinder() {
        this(1.0F);
    }
    
    public ModelCinder(float shadowSize) {

		// Load Model:
		this.initModel("cinder", LycanitesMobs.modInfo, "entity/cinder");

		// Trophy:
		this.trophyScale = 1.2F;
		this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
    }


	// ==================================================
	//             Add Custom Render Layers
	// ==================================================
	@Override
	public void addCustomLayers(CreatureRenderer renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerCreatureEffect(renderer, "eyes"));
	}


	// ==================================================
	//              Get Part Texture Offset
	// ==================================================
	@Override
	public Vec2f getBaseTextureOffset(String partName, Entity entity, boolean trophy, float loop) {
    	if(partName.contains("effect")) {
    		return super.getBaseTextureOffset(partName, entity, trophy, loop);
		}
		return new Vec2f(loop, 0);
	}


	// ==================================================
	//                   On Render
	// ==================================================
	@Override
	public void onRenderStart(LayerCreatureBase layer, Entity entity, boolean renderAsTrophy) {
		super.onRenderStart(layer, entity, renderAsTrophy);
		RenderSystem.disableLighting();
	}

	@Override
	public void onRenderFinish(LayerCreatureBase layer, Entity entity, boolean renderAsTrophy) {
		super.onRenderFinish(layer, entity, renderAsTrophy);
		RenderSystem.enableLighting();
	}
}
