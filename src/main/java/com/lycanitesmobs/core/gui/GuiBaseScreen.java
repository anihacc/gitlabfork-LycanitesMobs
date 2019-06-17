package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.ClientManager;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

public abstract class GuiBaseScreen extends Screen implements Button.IPressable {
	public float zLevel = 0;

	/**
	 * Constructor.
	 */
    public GuiBaseScreen(ITextComponent screenName) {
        super(screenName);
    }


	/**
	 * Returns the font renderer.
	 * @return The font renderer to use.
	 */
	public FontRenderer getFontRenderer() {
		return ClientManager.getInstance().getFontRenderer();
	}


	/**
	 * Draws text that is split onto new lines when it exceeds wrapWidth.
	 * @param str The string to output.
	 * @param x The x position.
	 * @param y The y position.
	 * @param wrapWidth The width to wrap text at.
	 * @param textColor The color of the text.
	 * @param shadow If true, a drop shadow wil be drawn under the text.
	 */
    public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor, boolean shadow) {
		if(shadow) {
			GlStateManager.translatef(0.5f,0.5f, 0);
			this.getFontRenderer().drawSplitString(str, x, y, wrapWidth, 0x444444);
			GlStateManager.translatef(-0.5f,-0.5f, 0);
		}
		this.getFontRenderer().drawSplitString(str, x,  y, wrapWidth, textColor);
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
		GlStateManager.enableBlend();
		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(false);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableAlphaTest();

		this.getMinecraft().getTextureManager().bindTexture(texture);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, z).tex(0, v).endVertex();
		buffer.pos(x + width, y + height, z).tex(u, v).endVertex();
		buffer.pos(x + width, y, z).tex(u, 0).endVertex();
		buffer.pos(x, y, z).tex(0, 0).endVertex();
		tessellator.draw();

		GlStateManager.enableAlphaTest();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepthTest();
		GlStateManager.disableBlend();
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
	public void drawTexturedTiled(ResourceLocation texture, float x, float y, float z, float u, float v, float width, float height, float resolution) {
		GlStateManager.enableBlend();
		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(false);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableAlphaTest();

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
	}


	/**
	 *
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


	/**
	 * Old scaled texture drawing. Texture must be equal width and height and bound.
	 * @param x The x position to draw at.
	 * @param y The y position to draw at.
	 * @param u The texture ending u coord.
	 * @param v The texture ending v coord.
	 * @param width The width of the texture.
	 * @param height The height of the texture.
	 */
	@Deprecated
    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        this.drawTexturedModalRect(x, y, u, v, width, height, 1);
    }


	/**
	 * Old scaled texture drawing. Texture must be equal width and height and bound.
	 * @param x The x position to draw at.
	 * @param y The y position to draw at.
	 * @param u The texture ending u coord.
	 * @param v The texture ending v coord.
	 * @param width The width of the texture.
	 * @param height The height of the texture.
	 * @param resolution The resolution (width or height) of the texture.
	 */
    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int resolution) {
        float scaleX = 0.00390625F * resolution;
        float scaleY = scaleX;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(u + 0) * scaleX), (double)((float)(v + height) * scaleY)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(u + width) * scaleX), (double)((float)(v + height) * scaleY)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(u + width) * scaleX), (double)((float)(v + 0) * scaleY)).endVertex();
        vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(u + 0) * scaleX), (double)((float)(v + 0) * scaleY)).endVertex();
        tessellator.draw();
    }


	// ==================================================
	//                     Actions
	// ==================================================
	@Override
	public void onPress(Button guiButton) {
		if(!(guiButton instanceof ButtonBase)) {
			return;
		}
		this.actionPerformed(((ButtonBase)guiButton).buttonId);
	}

	/**
	 * Called when a Button Base is pressed providing the press button's id.
	 * @param buttonId The id of the button pressed.
	 */
	public void actionPerformed(byte buttonId) {}
}
