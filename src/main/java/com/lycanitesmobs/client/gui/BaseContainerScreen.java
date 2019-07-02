package com.lycanitesmobs.client.gui;

import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.client.gui.buttons.ButtonBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

public abstract class BaseContainerScreen<T extends Container> extends ContainerScreen<T> implements Button.IPressable {
    public int zLevel = 0;
    public FontRenderer fontRenderer;

    public BaseContainerScreen(T container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);
        this.fontRenderer = Minecraft.getInstance().fontRenderer;
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        this.minecraft = minecraft;
        super.init(minecraft, width, height);
        this.initWidgets();
    }

    /**
     * Secondary init method called by main init method.
     */
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
        this.renderWidgets(mouseX, mouseY, partialTicks); // Renders buttons.
        super.render(mouseX, mouseY, partialTicks); // Renders slots.
        this.renderForeground(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    /**
     * Draws the background image.
     * @param mouseX The x position of the mouse cursor.
     * @param mouseY The y position of the mouse cursor.
     * @param partialTicks Ticks for animation.
     */
    protected abstract void renderBackground(int mouseX, int mouseY, float partialTicks);
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {} // Overridden as required but ignored.

    /**
     * Updates widgets like buttons and other controls for this screen. Super renders the button list, called after this.
     * @param mouseX The x position of the mouse cursor.
     * @param mouseY The y position of the mouse cursor.
     * @param partialTicks Ticks for animation.
     */
    protected void renderWidgets(int mouseX, int mouseY, float partialTicks) {
        for(int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).render(mouseX, mouseY, partialTicks);
        }
    }

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

    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        this.drawTexturedModalRect(x, y, u, v, width, height, 1);
    }

    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int resolution) {
        float scaleX = 0.00390625F * resolution;
        float scaleY = scaleX;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(u + 0) * scaleX), (double)((float)(v + height) * scaleY)).endVertex();
        bufferBuilder.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(u + width) * scaleX), (double)((float)(v + height) * scaleY)).endVertex();
        bufferBuilder.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(u + width) * scaleX), (double)((float)(v + 0) * scaleY)).endVertex();
        bufferBuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(u + 0) * scaleX), (double)((float)(v + 0) * scaleY)).endVertex();
        tessellator.draw();
    }
}
