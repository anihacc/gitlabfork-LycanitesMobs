package com.lycanitesmobs.client.gui;

import com.lycanitesmobs.client.gui.buttons.ButtonBase;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;

public abstract class BaseContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements Button.OnPress {
    public DrawHelper drawHelper;

    public BaseContainerScreen(T container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name);
    }

    @Override
    protected void init() {
        this.drawHelper = new DrawHelper(this.minecraft, this.minecraft.font);
        super.init();
        this.initWidgets();
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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        this.renderWidgets(matrixStack, mouseX, mouseY, partialTicks); // Renders buttons.
        super.render(matrixStack, mouseX, mouseY, partialTicks); // Renders slots.
        this.renderForeground(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    /**
     * Draws the background image.
     * @param mouseX The x position of the mouse cursor.
     * @param mouseY The y position of the mouse cursor.
     * @param partialTicks Ticks for animation.
     */
    protected abstract void renderBackground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks);
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {} // Overridden as required but ignored.

    /**
     * Updates widgets like buttons and other controls for this screen. Super renders the button list, called after this.
     * @param mouseX The x position of the mouse cursor.
     * @param mouseY The y position of the mouse cursor.
     * @param partialTicks Ticks for animation.
     */
    protected void renderWidgets(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for(int i = 0; i < this.renderables.size(); ++i) {
            this.renderables.get(i).render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    /**
     * Draws foreground elements.
     * @param mouseX The x position of the mouse cursor.
     * @param mouseY The y position of the mouse cursor.
     * @param partialTicks Ticks for animation.
     */
    protected abstract void renderForeground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks);

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
}
