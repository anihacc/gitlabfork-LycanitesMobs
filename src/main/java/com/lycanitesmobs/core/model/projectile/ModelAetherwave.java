package com.lycanitesmobs.core.model.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.model.ModelProjectileObj;
import com.lycanitesmobs.core.renderer.layer.LayerProjectileBase;
import com.mojang.blaze3d.platform.GLX;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.vecmath.Vector4f;

@OnlyIn(Dist.CLIENT)
public class ModelAetherwave extends ModelProjectileObj {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelAetherwave() {
        this(1.0F);
    }

    public ModelAetherwave(float shadowSize) {

		// Load Model:
		this.initModel("aetherwave", LycanitesMobs.modInfo, "projectile/aetherwave");
    }


	// ==================================================
	//                 Animate Part
	// ==================================================
	@Override
	public void animatePart(String partName, BaseProjectileEntity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part and layer. **/
	@Override
	public Vector4f getPartColor(String partName, BaseProjectileEntity entity, LayerProjectileBase layer, boolean trophy, float loop) {
		float glowSpeed = 40;
		float glow = loop * glowSpeed % 360;
		float color = ((float)Math.cos(Math.toRadians(glow)) * 0.1f) + 0.9f;
		return new Vector4f(color, color, color, 1);
	}


	// ==================================================
	//                      Visuals
	// ==================================================
	@Override
	public void onRenderStart(LayerProjectileBase layer, BaseProjectileEntity entity) {
		super.onRenderStart(layer, entity);
		int i = 15728880;
		int j = i % 65536;
		int k = i / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) j, (float) k);
	}

	@Override
	public void onRenderFinish(LayerProjectileBase layer, BaseProjectileEntity entity) {
		super.onRenderFinish(layer, entity);
		int i = entity.getBrightnessForRender();
		int j = i % 65536;
		int k = i / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) j, (float) k);
	}
}
