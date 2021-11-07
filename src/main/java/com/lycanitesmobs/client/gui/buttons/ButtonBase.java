package com.lycanitesmobs.client.gui.buttons;

import com.lycanitesmobs.client.gui.DrawHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

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
