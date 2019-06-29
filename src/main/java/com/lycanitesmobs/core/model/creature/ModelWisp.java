package com.lycanitesmobs.core.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.core.renderer.CreatureRenderer;
import com.lycanitesmobs.core.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.core.renderer.layer.LayerCreatureEffect;
import com.lycanitesmobs.core.renderer.layer.LayerCreatureScrolling;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

@OnlyIn(Dist.CLIENT)
public class ModelWisp extends ModelTemplateElemental {
	LayerCreatureEffect ballLayer;
	LayerCreatureEffect ballGlowLayer;
	LayerCreatureEffect hairLayer;

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelWisp() {
        this(1.0F);
    }

    public ModelWisp(float shadowSize) {

		// Load Model:
		this.initModel("wisp", LycanitesMobs.modInfo, "entity/wisp");

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
		this.ballLayer = new LayerCreatureEffect(renderer, "ball", true, LayerCreatureEffect.BLEND.NORMAL.id, true);
		renderer.addLayer(this.ballLayer);
		this.ballGlowLayer = new LayerCreatureEffect(renderer, "ball", true, LayerCreatureEffect.BLEND.ADD.id, true);
		renderer.addLayer(this.ballGlowLayer);
		this.hairLayer = new LayerCreatureScrolling(renderer, "hair", true, LayerCreatureEffect.BLEND.NORMAL.id, true, new Vector2f(0, 4));
		renderer.addLayer(this.hairLayer);
	}


	// ==================================================
	//                 Animate Part
	// ==================================================
	float maxLeg = 0F;
	@Override
	public void animatePart(String partName, LivingEntity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		// Hair:
		if(partName.equals("haircenter")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.1F), 0, 0);
		}
		else if(partName.equals("hairleft")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos(loop * 0.05F) * 0.1F), (float)Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.2F - 0.2F), 0);
		}
		else if(partName.equals("hairright")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos(loop * 0.05F) * 0.1F), -(float)Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.2F - 0.2F), 0);
		}
		else if(partName.contains("fringeleft")) {
			this.rotate(
					-(float)Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F),
					0,
					-(float)Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F)
			);
		}
		else if(partName.contains("fringeright")) {
			this.rotate(
					(float)Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F),
					0,
					(float)Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F)
			);
		}

		// Arms:
		else if(partName.equals("armleft")) {
			this.rotate(0, 0, (float)Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F));
		}
		else if(partName.equals("armright")) {
			this.rotate(0, 0, -(float)Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F));
		}
		if(entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown()) {
			if (partName.equals("armleft"))
				rotate(0F, 25.0F, 25.0F);
			if (partName.equals("armright"))
				rotate(0F, -25.0F, -25.0F);
		}
	}


	// ==================================================
	//                Can Render Part
	// ==================================================
	@Override
	public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
		if(partName.contains("ball") && entity instanceof BaseCreatureEntity && ((BaseCreatureEntity) entity).isAttackOnCooldown()) {
			return false;
		}
		if(partName.equals("ball01")) {
			return layer == this.ballLayer;
		}
		if(partName.equals("ball02") || partName.equals("ball03")) {
			return layer == this.ballGlowLayer;
		}
		if(partName.contains("fringe")) {
			return layer == this.hairLayer;
		}
		if(partName.contains("hair")) {
			return layer == this.hairLayer;
		}
		return layer == null;
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part and layer. **/
	@Override
	public Vector4f getPartColor(String partName, Entity entity, LayerCreatureBase layer, boolean trophy, float loop) {
		if(layer == this.ballLayer || layer ==  this.ballGlowLayer) {
			float glowSpeed = 40;
			float glow = loop * glowSpeed % 360;
			float color = ((float)Math.cos(Math.toRadians(glow)) * 0.1f) + 0.9f;
			return new Vector4f(color, color, color, 1);
		}

		return super.getPartColor(partName, entity, layer, trophy, loop);
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
