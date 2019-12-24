package com.lycanitesmobs.client.model.projectile;

import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelProjectileObj;
import com.lycanitesmobs.client.renderer.ProjectileModelRenderer;
import com.lycanitesmobs.client.renderer.layer.LayerProjectileBase;
import com.lycanitesmobs.client.renderer.layer.LayerProjectileEffect;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.client.renderer.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelLightBall extends ModelProjectileObj {
	LayerProjectileBase ballGlowLayer;
    public ModelLightBall() {
        this(1.0F);
    }

    public ModelLightBall(float shadowSize) {

		// Load Model:
		this.initModel("lightball", LycanitesMobs.modInfo, "projectile/lightball");
    }

	@Override
	public void addCustomLayers(ProjectileModelRenderer renderer) {
		super.addCustomLayers(renderer);
		this.ballGlowLayer = new LayerProjectileEffect(renderer, "", true, LayerProjectileEffect.BLEND.ADD.id, true);
		renderer.addLayer(this.ballGlowLayer);
	}

	@Override
	public void animatePart(String partName, BaseProjectileEntity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
		this.rotate(loop * 8, 0, 0);
	}

	@Override
	public boolean canRenderPart(String partName, BaseProjectileEntity entity, LayerProjectileBase layer) {
		if(partName.equals("ball02") || partName.equals("ball03")) {
			return layer == this.ballGlowLayer;
		}
		return layer == null;
	}

	/** Returns the coloring to be used for this part and layer. **/
	@Override
	public Vector4f getPartColor(String partName, BaseProjectileEntity entity, LayerProjectileBase layer, float loop) {
		float glowSpeed = 40;
		float glow = loop * glowSpeed % 360;
		float color = ((float)Math.cos(Math.toRadians(glow)) * 0.1f) + 0.9f;
		return new Vector4f(color, color, color, 1);
	}

	@Override
	public int getBrightness(String partName, LayerProjectileBase layer, BaseProjectileEntity entity, int brightness) {
		return ClientManager.FULL_BRIGHT;
	}
}
