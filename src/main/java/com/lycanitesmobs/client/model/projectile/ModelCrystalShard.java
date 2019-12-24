package com.lycanitesmobs.client.model.projectile;

import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelProjectileObj;
import com.lycanitesmobs.client.renderer.layer.LayerProjectileBase;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.client.renderer.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelCrystalShard extends ModelProjectileObj {
    public ModelCrystalShard() {
        this(1.0F);
    }

    public ModelCrystalShard(float shadowSize) {

		// Load Model:
		this.initModel("crystalshard", LycanitesMobs.modInfo, "projectile/crystalshard");
    }

	@Override
	public void animatePart(String partName, BaseProjectileEntity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
		this.rotate(0, 0, loop * 8);
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
