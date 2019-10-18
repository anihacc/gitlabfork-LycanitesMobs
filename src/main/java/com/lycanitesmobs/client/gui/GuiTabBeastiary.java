package com.lycanitesmobs.client.gui;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.beastiary.GuiBeastiaryIndex;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiTabBeastiary extends GuiTab {
	
	public GuiTabBeastiary(int id) {
        super(id, GuiTab.startX, GuiTab.startY, new ResourceLocation(LycanitesMobs.modid, "textures/items/soulgazer.png"));
    }

    @Override
    public void onTabClicked () {
        GuiBeastiaryIndex.openToPlayer(Minecraft.getMinecraft().player);
    }

    @Override
    public boolean shouldAddToList () {
        return true;
    }
}
