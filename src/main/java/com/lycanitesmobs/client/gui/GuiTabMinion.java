package com.lycanitesmobs.client.gui;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.beastiary.GuiBeastiarySummoning;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiTabMinion extends GuiTab {
	
	public GuiTabMinion(int id) {
        super(id, GuiTab.startX, GuiTab.startY, new ResourceLocation(LycanitesMobs.modid, "textures/items/summoningstaff.png"));
    }

    @Override
    public void onTabClicked () {
    	if(ExtendedPlayer.getForPlayer(Minecraft.getMinecraft().player) != null)
    		GuiBeastiarySummoning.openToPlayer(Minecraft.getMinecraft().player);
    }

    @Override
    public boolean shouldAddToList () {
        return true;
    }
}
