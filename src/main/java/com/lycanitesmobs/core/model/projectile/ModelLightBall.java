package com.lycanitesmobs.core.model.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.model.ModelProjectileObj;
import com.lycanitesmobs.core.renderer.RenderProjectileModel;
import com.lycanitesmobs.core.renderer.layer.LayerProjectileBase;
import com.lycanitesmobs.core.renderer.layer.LayerProjectileEffect;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.vecmath.Vector4f;

@OnlyIn(Dist.CLIENT)
public class ModelLightBall extends ModelProjectileObj {
	LayerProjectileBase ballGlowLayer;

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelLightBall() {
        this(1.0F);
    }

    public ModelLightBall(float shadowSize) {

		// Load Model:
		this.initModel("lightball", LycanitesMobs.modInfo, "projectile/lightball");
    }


	// ==================================================
	//             Add Custom Render Layers
	// ==================================================
	@Override
	public void addCustomLayers(RenderProjectileModel renderer) {
		super.addCustomLayers(renderer);
		this.ballGlowLayer = new LayerProjectileEffect(renderer, "", true, LayerProjectileEffect.BLEND.ADD.id, true);
		renderer.addLayer(this.ballGlowLayer);
	}


	// ==================================================
	//                 Animate Part
	// ==================================================
	@Override
	public void animatePart(String partName, BaseProjectileEntity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
		this.rotate(loop * 8, 0, 0);
	}


	// ==================================================
	//                Can Render Part
	// ==================================================
	@Override
	public boolean canRenderPart(String partName, BaseProjectileEntity entity, LayerProjectileBase layer, boolean trophy) {
		if(partName.equals("ball02") || partName.equals("ball03")) {
			return layer == this.ballGlowLayer;
		}
		return layer == null;
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
		GlStateManager.disableLighting();
	}

	@Override
	public void onRenderFinish(LayerProjectileBase layer, BaseProjectileEntity entity) {
		super.onRenderFinish(layer, entity);
		int i = entity.getBrightnessForRender();
		int j = i % 65536;
		int k = i / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) j, (float) k);
		GlStateManager.enableLighting();
	}
}
