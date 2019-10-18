package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.client.renderer.IItemModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class LayerItem {
	public IItemModelRenderer renderer;
	public String name;

	public String textureSuffix;

	public boolean glow = false;

	public enum BLEND {
		NORMAL(0), ADD(1), SUB(2);
		public final int id;
		BLEND(int value) { this.id = value; }
		public int getValue() { return id; }
	}
	public int blending = 0;

	public Vector2f scrollSpeed;

	public Vector4f colorFadeSpeed;


    // ==================================================
    //                   Constructor
    // ==================================================
	public LayerItem(IItemModelRenderer renderer, String name) {
		this.renderer = renderer;
		this.name = name;
	}


    // ==================================================
    //                      Visuals
    // ==================================================
    public Vector4f getPartColor(String partName, ItemStack itemStack, float loop) {
		if(this.colorFadeSpeed != null) {
			float red = 1;
			if(this.colorFadeSpeed.getX() != 0)
				red = (loop % this.colorFadeSpeed.getX() / this.colorFadeSpeed.getX()) * 2;
			if(red > 1)
				red = -(red - 1);

			float green = 1;
			if(this.colorFadeSpeed.getY() != 0)
				green = (loop % this.colorFadeSpeed.getY() / this.colorFadeSpeed.getY()) * 2;
			if(green > 1)
				green = -(green - 1);

			float blue = 1;
			if(this.colorFadeSpeed.getZ() != 0)
				blue = (loop % this.colorFadeSpeed.getZ() / this.colorFadeSpeed.getZ()) * 2;
			if(blue > 1)
				blue = -(blue - 1);

			float alpha = 1;
			if(this.colorFadeSpeed.getW() != 0)
				alpha = (loop % this.colorFadeSpeed.getW() / this.colorFadeSpeed.getW()) * 2;
			if(alpha > 1)
				alpha = -(alpha - 1);

			return new Vector4f(red, green, blue, alpha);
		}

        return new Vector4f(1, 1, 1, 1);
    }

    public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
		return entity.getTexture(this.textureSuffix);
    }

	public void onRenderStart(ItemStack itemStack) {
		// Glow In Dark:
		if(this.glow) {
			GlStateManager.disableLighting();
			int i = 0xf000f0;
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
		}

		// Blending:
    	if(this.blending == BLEND.ADD.id) {
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		}
		else if(this.blending == BLEND.SUB.id) {
			GlStateManager.blendFunc(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		}
	}

	public void onRenderFinish(ItemStack itemStack) {
    	if(this.glow) {
			GlStateManager.enableLighting();
		}
	}

	public Vector2f getTextureOffset(String partName, ItemStack itemStack, float loop) {
    	if(this.scrollSpeed == null) {
			this.scrollSpeed = new Vector2f(0, 0);
		}
		return new Vector2f(loop * this.scrollSpeed.x, loop * this.scrollSpeed.y);
	}
}
