package com.lycanitesmobs.client.gui.buttons;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.DrawHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.chat.Component;

import net.minecraft.client.gui.components.Button.OnPress;

public class ButtonBase extends Button {
    public DrawHelper drawHelper;
    public int buttonId;

    // ==================================================
    //                    Constructor
    // ==================================================
    public ButtonBase(int buttonId, int x, int y, int width, int height, Component text, OnPress pressable) {
        super(x, y, width, height, text, pressable);
        Minecraft minecraft = Minecraft.getInstance();
        this.drawHelper = new DrawHelper(minecraft, minecraft.font);
        this.buttonId = buttonId;
    }
}
