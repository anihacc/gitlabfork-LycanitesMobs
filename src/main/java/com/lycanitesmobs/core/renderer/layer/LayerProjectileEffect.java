package com.lycanitesmobs.core.renderer.layer;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.renderer.RenderProjectileModel;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

@OnlyIn(Dist.CLIENT)
public class LayerProjectileEffect extends LayerProjectileBase {

	public String textureSuffix;

	public boolean subspecies = true;

	public boolean glow = false;

	public enum BLEND {
		NORMAL(0), ADD(1), SUB(2);
		public final int id;
		BLEND(int value) { this.id = value; }
		public int getValue() { return id; }
	}
	public int blending = 0;

	public Vector2f scrollSpeed;


    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerProjectileEffect(RenderProjectileModel renderer, String textureSuffix) {
        super(renderer);
        this.name = textureSuffix;
        this.textureSuffix = textureSuffix;
    }

	public LayerProjectileEffect(RenderProjectileModel renderer, String textureSuffix, boolean glow, int blending, boolean subspecies) {
		super(renderer);
		this.name = textureSuffix;
		this.textureSuffix = textureSuffix;
		this.glow = glow;
		this.blending = blending;
		this.subspecies = subspecies;
	}


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public Vector4f getPartColor(String partName, EntityProjectileBase entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 1);
    }

    @Override
    public ResourceLocation getLayerTexture(EntityProjectileBase entity) {
		return super.getLayerTexture(entity);
    }

	@Override
	public void onRenderStart(Entity entity) {
		// Glow In Dark:
		int i = entity.getBrightnessForRender();
		if(this.glow) {
			GlStateManager.disableLighting();
			i = 0xf000f0;
		}
		int j = i % 65536;
		int k = i / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) j, (float) k);

		// Blending:
    	if(this.blending == BLEND.ADD.id) {
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		}
		else if(this.blending == BLEND.SUB.id) {
			GlStateManager.blendFunc(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		}
	}

	@Override
	public void onRenderFinish(Entity entity) {
    	if(this.glow) {
			GlStateManager.enableLighting();
		}
	}

	@Override
	public Vector2f getTextureOffset(String partName, EntityProjectileBase entity, boolean trophy, float loop) {
    	if(this.scrollSpeed == null) {
			this.scrollSpeed = new Vector2f(0, 0);
		}
		return new Vector2f(loop * this.scrollSpeed.x, loop * this.scrollSpeed.y);
	}
}
