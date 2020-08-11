package com.lycanitesmobs.client.gui;

import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.buttons.ButtonBase;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import java.net.URI;

public abstract class BaseScreen extends Screen implements Button.IPressable {
	public MainWindow scaledResolution;
	public float zLevel = 0;

	public BaseScreen(ITextComponent screenName) {
        super(screenName);
    }

    @Override
	public void init(Minecraft minecraft, int width, int height) {
    	this.field_230706_i_ = minecraft;
    	super.init(minecraft, width, height);
		this.initWidgets();
	}

	/**
	 * Secondary init method called by main init method.
	 */
	@Override
	protected void init() {
		super.init();
	}

	/**
	 * Initialises all buttons and other widgets that this Screen uses.
	 */
	protected abstract void initWidgets();

	/**
	 * Draws and updates the GUI.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 * @param partialTicks Ticks for animation.
	 */
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(mouseX, mouseY, partialTicks);
		this.renderWidgets(mouseX, mouseY, partialTicks);
		super.render(mouseX, mouseY, partialTicks); // Renders buttons.
		this.renderForeground(mouseX, mouseY, partialTicks);
	}

	/**
	 * Draws the background image.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 * @param partialTicks Ticks for animation.
	 */
	protected abstract void renderBackground(int mouseX, int mouseY, float partialTicks);

	/**
	 * Updates widgets like buttons and other controls for this screen. Super renders the button list, called after this.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 * @param partialTicks Ticks for animation.
	 */
	protected void renderWidgets(int mouseX, int mouseY, float partialTicks) {}

	/**
	 * Draws foreground elements.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 * @param partialTicks Ticks for animation.
	 */
	protected abstract void renderForeground(int mouseX, int mouseY, float partialTicks);

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
	public abstract void actionPerformed(int buttonId);

	/**
	 * Returns the font renderer.
	 * @return The font renderer to use.
	 */
	public FontRenderer getFontRenderer() {
		return ClientManager.getInstance().getFontRenderer();
	}

	/**
	 * Returns a scaled x coordinate.
	 * @param x The x float to scale where 1.0 is the entire GUI width.
	 * @return A scaled x position.
	 */
	public int getScaledX(float x) {
		if(this.scaledResolution == null) {
			this.scaledResolution = this.field_230706_i_.getMainWindow();
		}

		// Aspect Ratio:
		float targetAspect = 0.5625f; // 16:9
		float scaledHeight = scaledResolution.getScaledHeight();
		float scaledWidth = scaledResolution.getScaledWidth();
		float currentAspect = (scaledHeight * x) / (scaledWidth * x);

		// Wider Than target:
		if(currentAspect < targetAspect) {
			scaledWidth = scaledHeight + (scaledHeight * targetAspect);
		}

		// Taller Than target:
		else if(currentAspect > targetAspect) {
			scaledHeight = scaledWidth + (scaledWidth * targetAspect);
		}

		float guiWidth = scaledWidth * x;
		return Math.round(Math.max(x, guiWidth));
	}

	/**
	 * Returns a scaled y coordinate based on the scaled width with an aspect ratio applied to it.
	 * @param y The y float to scale where 1.0 is the entire GUI height.
	 * @return A scaled y position.
	 */
	public int getScaledY(float y) {
		float baseHeight = Math.round((float)this.getScaledX(y) * 0.5625f);
		return Math.round(baseHeight * y);
	}

	/**
	 * Opens a URI in the users default web browser.
	 * @param uri The URI link to open.
	 */
	protected void openURI(URI uri) {
		try {
			Util.getOSType().openURI(uri);
		} catch (Exception e) {
			LycanitesMobs.logWarning("", "Unable to open link: " + uri.toString());
			e.printStackTrace();
		}
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
			RenderSystem.translatef(0.5f,0.5f, 0);
			this.getFontRenderer().drawSplitString(str, x, y, wrapWidth, 0x444444);
			RenderSystem.translatef(-0.5f,-0.5f, 0);
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
		RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableAlphaTest();

		this.getMinecraft().getTextureManager().bindTexture(texture);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, z).tex(0, v).endVertex(); // pos().tex()
		buffer.pos(x + width, y + height, z).tex(u, v).endVertex();
		buffer.pos(x + width, y, z).tex(u, 0).endVertex();
		buffer.pos(x, y, z).tex(0, 0).endVertex();
		tessellator.draw();

		RenderSystem.enableAlphaTest();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
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
		RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableAlphaTest();

		this.getMinecraft().getTextureManager().bindTexture(texture);
		float scaleX = 0.00390625F * resolution;
		float scaleY = scaleX;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos((double)(x + 0), (double)(y + height), z) // pos()
				.tex(((u + 0) * scaleX), (v + height) * scaleY).endVertex(); // tex()
		buffer.pos((double)(x + width), (double)(y + height), z)
				.tex(((u + width) * scaleX), ((v + height) * scaleY)).endVertex();
		buffer.pos((double)(x + width), (double)(y + 0), z)
				.tex(((u + width) * scaleX), ((v + 0) * scaleY)).endVertex();
		buffer.pos((double)(x + 0), (double)(y + 0), z)
				.tex(((u + 0) * scaleX), ((v + 0) * scaleY)).endVertex();
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
        vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel) // pos()
				.tex(((float)(u + 0) * scaleX), ((float)(v + height) * scaleY)).endVertex(); // tex()
        vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel)
				.tex(((float)(u + width) * scaleX), ((float)(v + height) * scaleY)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel)
				.tex(((float)(u + width) * scaleX), ((float)(v + 0) * scaleY)).endVertex();
        vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel)
				.tex(((float)(u + 0) * scaleX), ((float)(v + 0) * scaleY)).endVertex();
        tessellator.draw();
    }
}
