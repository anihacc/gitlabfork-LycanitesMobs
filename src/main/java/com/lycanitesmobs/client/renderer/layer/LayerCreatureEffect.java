package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.client.renderer.CreatureRenderer;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerCreatureEffect extends LayerCreatureBase {

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

	public Vec2f scrollSpeed;


    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerCreatureEffect(CreatureRenderer renderer, String textureSuffix) {
        super(renderer);
        this.name = textureSuffix;
        this.textureSuffix = textureSuffix;
    }

	public LayerCreatureEffect(CreatureRenderer renderer, String textureSuffix, boolean glow, int blending, boolean subspecies) {
		super(renderer);
		this.name = textureSuffix;
		this.textureSuffix = textureSuffix;
		this.glow = glow;
		this.blending = blending;
		this.subspecies = subspecies;
	}

	public LayerCreatureEffect(CreatureRenderer renderer, String name, String textureSuffix, boolean glow, int blending, boolean subspecies) {
		super(renderer);
		this.name = name;
		this.textureSuffix = textureSuffix;
		this.glow = glow;
		this.blending = blending;
		this.subspecies = subspecies;
	}


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public Vector4f getPartColor(String partName, BaseCreatureEntity entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 1);
    }

    @Override
    public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
		return entity.getTexture(this.textureSuffix);
    }

	@Override
	public void onRenderStart(Entity entity, boolean trophy) {
		// Glow In Dark:
		int i = ClientManager.FULL_BRIGHT;
		if(this.glow) {
			RenderSystem.disableLighting();
			i = 0xf000f0;
		}
		int j = i % 65536;
		int k = i / 65536;
		RenderSystem.glMultiTexCoord2f(ClientManager.GL_TEXTURE1, (float) j, (float) k);

		// Blending:
    	if(this.blending == BLEND.ADD.id) {
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		}
		else if(this.blending == BLEND.SUB.id) {
			RenderSystem.blendFunc(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		}
	}

	@Override
	public void onRenderFinish(Entity entity, boolean trophy) {
    	if(this.glow) {
			RenderSystem.enableLighting();
		}
	}

	@Override
	public Vec2f getTextureOffset(String partName, BaseCreatureEntity entity, boolean trophy, float loop) {
    	if(this.scrollSpeed == null) {
			this.scrollSpeed = new Vec2f(0, 0);
		}
		return new Vec2f(loop * this.scrollSpeed.x, loop * this.scrollSpeed.y);
	}
}
