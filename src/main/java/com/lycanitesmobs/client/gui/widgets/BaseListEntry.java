package com.lycanitesmobs.client.gui.widgets;

import com.lycanitesmobs.ClientManager;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListEntry extends AbstractOptionList.Entry<BaseListEntry> {
	public int index;

	@Override
	public void render(int index, int top, int left, int bottom, int right, int mouseX, int mouseY, boolean p_render_8_, float partialTicks) {}

	/**
	 * Returns a list of child GUI elements such as buttons that should receive input events, etc.
	 * @return A list of child GUI elements.
	 */
	@Override
	public List<? extends IGuiEventListener> children() {
		return new ArrayList<>();
	}

	/**
	 * Called when the mouse is clicked on this entry.
	 * @param mouseX The mouse cursor X position.
	 * @param mouseY The mouse cursor Y position.
	 * @param mouseButton The button pressed.
	 * @return Returns true if the click should continue (allowing the list to scroll, etc) or false if it shouldn't.
	 */
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return true;
	}

	/**
	 * Called when the mouse is released on this entry.
	 * @param mouseX The mouse cursor X position.
	 * @param mouseY The mouse cursor Y position.
	 * @param mouseButton The button pressed.
	 * @return Returns true if the release should continue (allowing the list to scroll, etc) or false if it shouldn't.
	 */
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		this.onClicked();
		return true;
	}

	/**
	 * Called when this entry is clicked.
	 */
	protected abstract void onClicked();

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

		Minecraft.getInstance().getTextureManager().bindTexture(texture);
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
}
