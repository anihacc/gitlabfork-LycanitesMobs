package com.lycanitesmobs.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class DrawHelper {
	protected Minecraft minecraft;
	protected FontRenderer fontRenderer;

	public DrawHelper(Minecraft minecraft, FontRenderer fontRenderer) {
		this.minecraft = minecraft;
		this.fontRenderer = fontRenderer;
	}

	/**
	 * Returns the Minecraft client instance.
	 * @return The Minecraft client.
	 */
	public Minecraft getMinecraft() {
		return this.minecraft;
	}

	/**
	 * Returns the font renderer.
	 * @return The font renderer to use.
	 */
	public FontRenderer getFontRenderer() {
		if (this.fontRenderer == null) {
			this.fontRenderer = this.minecraft.fontRenderer;
		}
		return this.fontRenderer;
	}

	/**
	 * Determines the width of the provided text.
	 * @param text The text to determine the render width of.
	 * @return The width (in pixels) the the provided text would have.
	 */
	public int getStringWidth(String text) {
		return this.getFontRenderer().getStringWidth(text);
	}

	/**
	 * Determines the height of the provided text once wordwrapped.
	 * @param text The text to determine the render height of.
	 * @param wrapWidth The maximum width the text is allowed to have before wrapping at.
	 * @return The height (in pixels) the the provided text would have with word wrapping.
	 */
	public int getWordWrappedHeight(String text, int wrapWidth) {
		return this.getFontRenderer().getWordWrappedHeight(text, wrapWidth);
	}

	/**
	 * Draws a texture.
	 * @param texture The texture resource location.
	 * @param x The x position to draw at.
	 * @param y The y position to draw at.
	 * @param z The z position to draw at.
	 * @param u The texture ending u coord.
	 * @param v The texture ending v coord.
	 * @param width The width of the texture.
	 * @param height The height of the texture.
	 */
	public void drawTexture(ResourceLocation texture, float x, float y, float z, float u, float v, float width, float height) {
		this.preDraw();

		this.getMinecraft().getTextureManager().bindTexture(texture);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, z).tex(0, v).endVertex();
		buffer.pos(x + width, y + height, z).tex(u, v).endVertex();
		buffer.pos(x + width, y, z).tex(u, 0).endVertex();
		buffer.pos(x, y, z).tex(0, 0).endVertex();
		tessellator.draw();

		this.postDraw();
	}

	/**
	 * Tiled texture drawing. Texture must be equal width and height and bound.
	 * @param texture The texture resource location.
	 * @param x The x position to draw at.
	 * @param y The y position to draw at.
	 * @param z The z position to draw at.
	 * @param u The texture ending u coord.
	 * @param v The texture ending v coord.
	 * @param width The width of the texture.
	 * @param height The height of the texture.
	 * @param resolution The resolution (width or height) of the texture.
	 */
	public void drawTextureTiled(ResourceLocation texture, float x, float y, float z, float u, float v, float width, float height, float resolution) {
		this.preDraw();

		this.getMinecraft().getTextureManager().bindTexture(texture);
		float scaleX = 0.00390625F * resolution;
		float scaleY = scaleX;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos((double)(x + 0), (double)(y + height), z).tex((double)((u + 0) * scaleX), (double)((v + height) * scaleY)).endVertex();
		buffer.pos((double)(x + width), (double)(y + height), z).tex((double)((u + width) * scaleX), (double)((v + height) * scaleY)).endVertex();
		buffer.pos((double)(x + width), (double)(y + 0), z).tex((double)((u + width) * scaleX), (double)((v + 0) * scaleY)).endVertex();
		buffer.pos((double)(x + 0), (double)(y + 0), z).tex((double)((u + 0) * scaleX), (double)((v + 0) * scaleY)).endVertex();
		tessellator.draw();

		this.postDraw();
	}

	/**
	 * Draws a bar.
	 * @param texture The texture resource location.
	 * @param x The x position to draw at.
	 * @param y The y position to draw at.
	 * @param z The z position to draw at.
	 * @param width The width of each bar segment.
	 * @param height The height of each bar segment.
	 * @param segments How many segments to draw.
	 * @param segmentLimit How many segments to draw up to before squishing them. If negative the bar is draw backwards.
	 */
	public void drawBar(ResourceLocation texture, int x, int y, float z, float width, float height, int segments, int segmentLimit) {
		boolean reverse = segmentLimit < 0;
		if(reverse) {
			segmentLimit = -segmentLimit;
		}
		// TODO segmentLimit
		for (int i = 0; i < segments; i++) {
			int currentSegment = i;
			if(reverse) {
				currentSegment = segmentLimit - i - 1;
			}
			this.drawTexture(texture, x + (width * currentSegment), y, z, 1, 1, width, height);
		}
	}

	public void preDraw() {
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableAlpha();
	}

	public void postDraw() {
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.enableAlpha();
	}

	public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
		this.drawTexturedModalRect(x, y, u, v, width, height, 1);
	}

	public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int resolution) {
		this.preDraw();

		float scaleX = 0.00390625F * resolution;
		float scaleY = scaleX;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos((float)(x), (float)(y + height), 0)
				.tex(((float)u * scaleX), ((float)(v + height) * scaleY)).endVertex();
		buffer.pos((float)(x + width), (float)(y + height), 0)
				.tex(((float)(u + width) * scaleX), ((float)(v + height) * scaleY)).endVertex();
		buffer.pos((float)(x + width), (float)(y), 0)
				.tex(((float)(u + width) * scaleX), ((float)(v) * scaleY)).endVertex();
		buffer.pos((float)x, (float)(y), 0)
				.tex(((float)u * scaleX), ((float)(v) * scaleY)).endVertex();
		tessellator.draw();

		this.postDraw();
	}
}
