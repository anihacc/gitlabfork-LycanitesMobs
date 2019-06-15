package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.core.container.ContainerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public abstract class GuiBaseContainer extends ContainerScreen<ContainerBase> implements Button.IPressable {
    public int zLevel = 0;
    public PlayerInventory playerInventory; // Here until parent is full deobfuscated.
    public FontRenderer fontRenderer;

    // ==================================================
    //                    Constructor
    // ==================================================
    public GuiBaseContainer(ContainerBase container, PlayerInventory playerInventory, ITextComponent containerName) {
        super(container, playerInventory, containerName);
        this.playerInventory = playerInventory;
        this.fontRenderer = Minecraft.getInstance().fontRenderer;
    }


    // ==================================================
    //                   Draw Screen
    // ==================================================
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
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
