package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.gui.beastiary.GuiBeastiaryIndex;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiTabMain extends GuiTab {

	public GuiTabMain(int id, IPressable pressable) {
        super(id, GuiTab.startX, GuiTab.startY, new ResourceLocation(LycanitesMobs.MODID, "textures/items/soulgazer.png"), pressable);
    }

    public GuiTabMain(int id, int x, int y, IPressable pressable) {
        super(id, x, y, new ResourceLocation(LycanitesMobs.MODID, "textures/items/soulgazer.png"), pressable);
    }

    @Override
    public void onTabClicked () {
	    Minecraft.getInstance().displayGuiScreen(new GuiBeastiaryIndex(Minecraft.getInstance().player));
    }

    @Override
    public boolean shouldAddToList () {
        return true;
    }
}
