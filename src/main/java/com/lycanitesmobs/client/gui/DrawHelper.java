package com.lycanitesmobs.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class DrawHelper {
	protected Minecraft minecraft;
	protected Font fontRenderer;

	public DrawHelper(Minecraft minecraft, Font fontRenderer) {
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
	public Font getFontRenderer() {
		return this.fontRenderer;
	}

	/**
	 * Draws text .
	 * @param matrixStack The matrix stack to draw with.
	 * @param text The string to output.
	 * @param x The x position.
	 * @param y The y position.
	 * @param color The color of the text.
	 * @param shadow If true, a drop shadow will be drawn under the text.
	 */
	public void drawString(PoseStack matrixStack, String text, int x, int y, int color, boolean shadow) {
		if (shadow) {
			this.getFontRenderer().drawShadow(matrixStack, text, x, y, color);
			return;
		}
		this.getFontRenderer().draw(matrixStack, text, x, y, color);
	}

	public void drawString(PoseStack matrixStack, String text, int x, int y, int color) {
		this.drawString(matrixStack, text, x, y, color, false);
	}

	/**
	 * Draws text centered at the x position.
	 * @param matrixStack The matrix stack to draw with.
	 * @param text The string to output.
	 * @param x The x position.
	 * @param y The y position.
	 * @param color The color of the text.
	 * @param shadow If true, a drop shadow will be drawn under the text.
	 */
	public void drawStringCentered(PoseStack matrixStack, String text, int x, int y, int color, boolean shadow) {
		int textWidth = this.getStringWidth(text);
		int textOffset = (int)Math.floor((double)textWidth / 2);
		this.drawString(matrixStack, text, x - textOffset, y, color, shadow);
	}

	/**
	 * Draws text that is split onto new lines when it exceeds wrapWidth.
	 * @param matrixStack The matrix stack to draw with.
	 * @param text The string to output.
	 * @param x The x position.
	 * @param y The y position.
	 * @param wrapWidth The width to wrap text at.
	 * @param color The color of the text.
	 * @param shadow If true, a drop shadow wil be drawn under the text.
	 */
	public void drawStringWrapped(PoseStack matrixStack, String text, int x, int y, int wrapWidth, int color, boolean shadow) {
		TextComponent textComponent = new TextComponent(text);
		List<FormattedCharSequence> textLines = this.getFontRenderer().split(textComponent, wrapWidth);
		int lineY = y;
		for(FormattedCharSequence textLine : textLines) {
			if (shadow) {
				this.getFontRenderer().drawShadow(matrixStack, textLine, (float)x, (float)lineY, color);
				lineY += 10;
				continue;
			}
			this.getFontRenderer().draw(matrixStack, textLine, (float)x, (float)lineY, color);
			lineY += 10;
		}
	}

	/**
	 * Determines the width of the provided text.
	 * @param text The text to determine the render width of.
	 * @return The width (in pixels) the the provided text would have.
	 */
	public int getStringWidth(String text) {
		return this.getFontRenderer().width(text);
	}

	/**
	 * Determines the height of the provided text once wordwrapped.
	 * @param text The text to determine the render height of.
	 * @param wrapWidth The maximum width the text is allowed to have before wrapping at.
	 * @return The height (in pixels) the the provided text would have with word wrapping.
	 */
	public int getWordWrappedHeight(String text, int wrapWidth) {
		return this.getFontRenderer().wordWrapHeight(text, wrapWidth);
	}

	/**
	 * Draws a texture.
	 * @param matrixStack The matrix stack to draw with.
	 * @param texture The texture resource location.
	 * @param x The x position to draw at.
	 * @param y The y position to draw at.
	 * @param z The z position to draw at.
	 * @param u The texture ending u coord.
	 * @param v The texture ending v coord.
	 * @param width The width of the texture.
	 * @param height The height of the texture.
	 */
	public void drawTexture(PoseStack matrixStack, ResourceLocation texture, float x, float y, float z, float u, float v, float width, float height) {
		this.preDraw();

		this.getMinecraft().getTextureManager().bindForSetup(texture);
		Matrix3f normal = matrixStack.last().normal();
		Matrix4f matrix = matrixStack.last().pose();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buffer.vertex(matrix, x, y + height, z).uv(0, v).endVertex();
		buffer.vertex(matrix, x + width, y + height, z).uv(u, v).endVertex();
		buffer.vertex(matrix, x + width, y, z).uv(u, 0).endVertex();
		buffer.vertex(matrix, x, y, z).uv(0, 0).endVertex();
		tessellator.end();

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
	public void drawTextureTiled(PoseStack matrixStack, ResourceLocation texture, float x, float y, float z, float u, float v, float width, float height, float resolution) {
		this.preDraw();

		this.getMinecraft().getTextureManager().bindForSetup(texture);
		float scaleX = 0.00390625F * resolution;
		float scaleY = scaleX;
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buffer.vertex(x + 0, y + height, z)
				.uv(((u + 0) * scaleX), (v + height) * scaleY).endVertex();
		buffer.vertex(x + width, y + height, z)
				.uv(((u + width) * scaleX), ((v + height) * scaleY)).endVertex();
		buffer.vertex(x + width, y + 0, z)
				.uv(((u + width) * scaleX), ((v + 0) * scaleY)).endVertex();
		buffer.vertex(x + 0, y + 0, z)
				.uv(((u + 0) * scaleX), ((v + 0) * scaleY)).endVertex();
		tessellator.end();

		this.postDraw();
	}

	/**
	 * Draws a bar.
	 * @param matrixStack The matrix stack to draw with.
	 * @param texture The texture resource location.
	 * @param x The x position to draw at.
	 * @param y The y position to draw at.
	 * @param z The z position to draw at.
	 * @param width The width of each bar segment.
	 * @param height The height of each bar segment.
	 * @param segments How many segments to draw.
	 * @param segmentLimit How many segments to draw up to before squishing them. If negative the bar is draw backwards.
	 */
	public void drawBar(PoseStack matrixStack, ResourceLocation texture, int x, int y, float z, float width, float height, int segments, int segmentLimit) {
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
			this.drawTexture(matrixStack, texture, x + (width * currentSegment), y, z, 1, 1, width, height);
		}
	}

	public void preDraw() {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
	}

	public void postDraw() {
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}

	@Deprecated // TODO: Migrate all guis to use DrawTexture instead.
	public void drawTexturedModalRect(PoseStack matrixStack, int x, int y, int u, int v, int width, int height) {
		this.drawTexturedModalRect(matrixStack, x, y, u, v, width, height, 1);
	}

	@Deprecated // TODO: Migrate all guis to use DrawTexture instead. The resolution scaling stuff was removed in 1.14.
	public void drawTexturedModalRect(PoseStack matrixStack, int x, int y, int u, int v, int width, int height, int resolution) {
		this.preDraw();

		float scaleX = 0.00390625F * resolution;
		float scaleY = scaleX;

		Matrix3f normal = matrixStack.last().normal();
		Matrix4f matrix = matrixStack.last().pose();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buffer.vertex(matrix, (float)(x), (float)(y + height), 0)
				.uv(((float)u * scaleX), ((float)(v + height) * scaleY)).endVertex();
		buffer.vertex(matrix, (float)(x + width), (float)(y + height), 0)
				.uv(((float)(u + width) * scaleX), ((float)(v + height) * scaleY)).endVertex();
		buffer.vertex(matrix, (float)(x + width), (float)(y), 0)
				.uv(((float)(u + width) * scaleX), ((float)(v) * scaleY)).endVertex();
		buffer.vertex(matrix, (float)x, (float)(y), 0)
				.uv(((float)u * scaleX), ((float)(v) * scaleY)).endVertex();
		tessellator.end();

		this.postDraw();
	}
}
