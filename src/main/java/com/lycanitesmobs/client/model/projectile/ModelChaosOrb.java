package com.lycanitesmobs.client.model.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelCreatureObj;
import com.lycanitesmobs.client.renderer.layer.LayerBase;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class ModelChaosOrb extends ModelCreatureObj {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelChaosOrb() {
        this(1.0F);
    }

    public ModelChaosOrb(float shadowSize) {

		// Load Model:
		this.initModel("chaosorb", LycanitesMobs.modInfo, "projectile/chaosorb");
    }


	// ==================================================
	//                 Animate Part
	// ==================================================
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
		this.rotate(0, 0, loop * 8);
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part and layer. **/
	public Vector4f getPartColor(String partName, Entity entity, LayerBase layer, boolean trophy, float loop) {
		float glowSpeed = 40;
		float glow = loop * glowSpeed % 360;
		float color = ((float)Math.cos(Math.toRadians(glow)) * 0.1f) + 0.9f;
		return new Vector4f(color, color, color, 1);
	}


	// ==================================================
	//                      Visuals
	// ==================================================
	@Override
	public void onRenderStart(LayerBase layer, Entity entity, boolean renderAsTrophy) {
		super.onRenderStart(layer, entity, renderAsTrophy);
		int i = 15728880;
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
	}

	@Override
	public void onRenderFinish(LayerBase layer, Entity entity, boolean renderAsTrophy) {
		super.onRenderFinish(layer, entity, renderAsTrophy);
		int i = entity.getBrightnessForRender();
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
	}
}
