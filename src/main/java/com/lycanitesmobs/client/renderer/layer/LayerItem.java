package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.renderer.IItemModelRenderer;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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

	public Vec2f scrollSpeed;

	public Vector4f colorFadeSpeed;


	/**
	 * Constructor
	 * @param renderer The renderer that is rendering this layer.
	 */
	public LayerItem(IItemModelRenderer renderer, String name) {
		this.renderer = renderer;
		this.name = name;
	}

	/**
	 *  Returns the color that this layer should render the provided part at.
	 * @param partName The name of the model part.
	 * @param itemStack The item stack to render.
	 * @param loop The animation loop to use.
	 * @return The part color.
	 */
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

    public ResourceLocation getLayerTexture(ItemStack itemStack) {
		return null;
    }

	/**
	 *  Returns the brightness that this layer should use.
	 * @param partName The name of the model part.
	 * @param itemStack The item stack to render.
	 * @param brightness The base brightness.
	 * @return The brightness to render at.
	 */
	public int getBrightness(String partName, ItemStack itemStack, int brightness) {
		if(this.glow) {
			return 240;
		}
		return brightness;
	}

	public Vec2f getTextureOffset(String partName, ItemStack itemStack, float loop) {
    	if(this.scrollSpeed == null) {
			this.scrollSpeed = new Vec2f(0, 0);
		}
		return new Vec2f(loop * this.scrollSpeed.x, loop * this.scrollSpeed.y);
	}
}
