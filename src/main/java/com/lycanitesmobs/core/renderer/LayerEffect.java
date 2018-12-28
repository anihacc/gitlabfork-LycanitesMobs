package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class LayerEffect extends LayerBase {

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
    public LayerEffect(RenderCreature renderer, String textureSuffix) {
        super(renderer);
        this.name = textureSuffix;
        this.textureSuffix = textureSuffix;
    }

	public LayerEffect(RenderCreature renderer, String textureSuffix, boolean glow, int blending, boolean subspecies) {
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
    public Vector4f getPartColor(String partName, EntityCreatureBase entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 1);
    }

    @Override
    public ResourceLocation getLayerTexture(EntityCreatureBase entity) {
		return entity.getTexture(this.textureSuffix);
    }

	@Override
	public void onRenderStart(Entity entity, boolean trophy) {
		// Glow In Dark:
		int i = entity.getBrightnessForRender();
		if(this.glow) {
			GlStateManager.disableLighting();
			i = 0xf000f0;
		}
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);

		// Blending:
    	if(this.blending == BLEND.ADD.id) {
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		}
		else if(this.blending == BLEND.SUB.id) {
			GlStateManager.blendFunc(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		}
	}

	@Override
	public void onRenderFinish(Entity entity, boolean trophy) {
    	if(this.glow) {
			GlStateManager.enableLighting();
		}
	}

	@Override
	public Vector2f getTextureOffset(String partName, EntityCreatureBase entity, boolean trophy, float loop) {
    	if(this.scrollSpeed == null) {
			this.scrollSpeed = new Vector2f(0, 0);
		}
		return new Vector2f(loop * this.scrollSpeed.x, loop * this.scrollSpeed.y);
	}
}
