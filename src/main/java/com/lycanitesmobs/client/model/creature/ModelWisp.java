package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.client.model.template.ModelTemplateElemental;
import com.lycanitesmobs.client.renderer.layer.LayerBase;
import com.lycanitesmobs.client.renderer.layer.LayerEffect;
import com.lycanitesmobs.client.renderer.layer.LayerScrolling;
import com.lycanitesmobs.client.renderer.RenderCreature;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class ModelWisp extends ModelTemplateElemental {

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
	public void addCustomLayers(RenderCreature renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerEffect(renderer, "ball", true, LayerEffect.BLEND.NORMAL.id, true));
		renderer.addLayer(new LayerEffect(renderer, "ball_glow",  "ball", true, LayerEffect.BLEND.ADD.id, true));
		renderer.addLayer(new LayerScrolling(renderer, "hair", true, LayerEffect.BLEND.NORMAL.id, true, new Vector2f(0, 4)));
	}


	// ==================================================
	//                 Animate Part
	// ==================================================
	float maxLeg = 0F;
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
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
	public boolean canRenderPart(String partName, Entity entity, LayerBase layer, boolean trophy) {
		if(partName.contains("ball") && entity instanceof BaseCreatureEntity && ((BaseCreatureEntity) entity).isAttackOnCooldown()) {
			return false;
		}
		if(partName.equals("ball01")) {
			return layer != null && "ball".equals(layer.name);
		}
		if(partName.equals("ball02") || partName.equals("ball03")) {
			return layer != null && "ball_glow".equals(layer.name);
		}
		if(partName.contains("fringe")) {
			return layer != null && "hair".equals(layer.name);
		}
		if(partName.contains("hair")) {
			return layer != null && "hair".equals(layer.name);
		}
		return layer == null;
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part and layer. **/
	@Override
	public Vector4f getPartColor(String partName, Entity entity, LayerBase layer, boolean trophy, float loop) {
		if(layer != null && ("ball".equals(layer.name) || "ball_glow".equals(layer.name))) {
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
