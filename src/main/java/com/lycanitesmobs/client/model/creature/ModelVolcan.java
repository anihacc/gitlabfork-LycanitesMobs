package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateElemental;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureGlow;
import com.lycanitesmobs.client.renderer.RenderCreature;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelVolcan extends ModelTemplateElemental {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelVolcan() {
        this(1.0F);
    }

    public ModelVolcan(float shadowSize) {

		// Load Model:
		this.initModel("volcan", LycanitesMobs.modInfo, "entity/volcan");

		// Trophy:
		this.trophyScale = 1.2F;
		this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
		this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
    }


	// ==================================================
	//             Add Custom Render Layers
	// ==================================================
	@Override
	public void addCustomLayers(RenderCreature renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerCreatureGlow(renderer));
	}


	// ==================================================
	//                 Animate Part
	// ==================================================
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		// Hands:
		if(partName.contains("hand")) {
			float angleX = 0;
			float angleY = -90f;
			float angleZ = 90f;
			if(partName.contains("right")) {
				angleY = -angleY;
				angleZ = -angleZ;
			}
			this.angle(loop * 10F, angleX, angleY, angleZ);
		}

		// Effects:
		if(partName.equals("effect01") || partName.equals("effect03")) {
			this.rotate(0, loop * 8, 0);
		}
		if(partName.equals("effect02") || partName.equals("effect04")) {
			this.rotate(0, loop * -8, 0);
		}
	}


	// ==================================================
	//                   On Render
	// ==================================================
	@Override
	public void onRenderStart(LayerCreatureBase layer, Entity entity, boolean renderAsTrophy) {
		super.onRenderStart(layer, entity, renderAsTrophy);
		GlStateManager.disableLighting();
	}

	@Override
	public void onRenderFinish(LayerCreatureBase layer, Entity entity, boolean renderAsTrophy) {
		super.onRenderFinish(layer, entity, renderAsTrophy);
		GlStateManager.enableLighting();
	}
}
