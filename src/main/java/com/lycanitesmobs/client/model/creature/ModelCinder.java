package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateElemental;
import com.lycanitesmobs.client.renderer.layer.LayerBase;
import com.lycanitesmobs.client.renderer.layer.LayerEffect;
import com.lycanitesmobs.client.renderer.RenderCreature;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector2f;

@SideOnly(Side.CLIENT)
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
	public void addCustomLayers(RenderCreature renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerEffect(renderer, "eyes"));
	}


	// ==================================================
	//              Get Part Texture Offset
	// ==================================================
	@Override
	public Vector2f getBaseTextureOffset(String partName, Entity entity, boolean trophy, float loop) {
    	if(partName.contains("effect")) {
    		return super.getBaseTextureOffset(partName, entity, trophy, loop);
		}
		return new Vector2f(loop, 0);
	}


	// ==================================================
	//                   On Render
	// ==================================================
	@Override
	public void onRenderStart(LayerBase layer, Entity entity, boolean renderAsTrophy) {
		super.onRenderStart(layer, entity, renderAsTrophy);
		GlStateManager.disableLighting();
	}

	@Override
	public void onRenderFinish(LayerBase layer, Entity entity, boolean renderAsTrophy) {
		super.onRenderFinish(layer, entity, renderAsTrophy);
		GlStateManager.enableLighting();
	}
}
