package com.lycanitesmobs.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public abstract class BaseContainerScreen extends GuiContainer {

    // ==================================================
    //                    Constructor
    // ==================================================
    public BaseContainerScreen(Container container) {
        super(container);
    }


    // ==================================================
    //                   Draw Screen
    // ==================================================
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }


    // ==================================================
    //                   Draw Texture
    // ==================================================
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
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();

        this.mc.getTextureManager().bindTexture(texture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, z).tex(0, v).endVertex();
        buffer.pos(x + width, y + height, z).tex(u, v).endVertex();
        buffer.pos(x + width, y, z).tex(u, 0).endVertex();
        buffer.pos(x, y, z).tex(0, 0).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
    }

    @Override
    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        this.drawTexturedModalRect(x, y, u, v, width, height, 1);
    }

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
}
