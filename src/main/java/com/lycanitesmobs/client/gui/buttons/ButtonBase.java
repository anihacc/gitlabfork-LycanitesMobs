package com.lycanitesmobs.client.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class ButtonBase extends Button {
    public int buttonId;
    public int zLevel = 0;
    public FontRenderer fontRenderer;

    // ==================================================
    //                    Constructor
    // ==================================================
    public ButtonBase(int buttonId, int x, int y, int width, int height, String text, Button.IPressable pressable) {
        super(x, y, width, height, text, pressable);
        this.buttonId = buttonId;
        this.fontRenderer = Minecraft.getInstance().fontRenderer;
    }


    // ==================================================
    //                   Draw Texture
    // ==================================================
    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        this.drawTexturedModalRect(x, y, u, v, width, height, 1);
    }

    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int resolution) {
        float scaleX = 0.00390625F * resolution;
        float scaleY = scaleX;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.func_225582_a_((double)(x + 0), (double)(y + height), (double)this.zLevel) // pos()
                .func_225583_a_(((float)(u + 0) * scaleX), ((float)(v + height) * scaleY)).endVertex(); // tex()
        vertexbuffer.func_225582_a_((double)(x + width), (double)(y + height), (double)this.zLevel)
                .func_225583_a_(((float)(u + width) * scaleX), ((float)(v + height) * scaleY)).endVertex();
        vertexbuffer.func_225582_a_((double)(x + width), (double)(y + 0), (double)this.zLevel)
                .func_225583_a_(((float)(u + width) * scaleX), ((float)(v + 0) * scaleY)).endVertex();
        vertexbuffer.func_225582_a_((double)(x + 0), (double)(y + 0), (double)this.zLevel)
                .func_225583_a_(((float)(u + 0) * scaleX), ((float)(v + 0) * scaleY)).endVertex();
        tessellator.draw();
    }
}
