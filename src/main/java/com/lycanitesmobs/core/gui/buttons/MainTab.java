package com.lycanitesmobs.core.gui.buttons;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.gui.beastiary.IndexBeastiaryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class MainTab extends Tab {

	public MainTab(int id, IPressable pressable) {
        super(id, Tab.startX, Tab.startY, new ResourceLocation(LycanitesMobs.MODID, "textures/items/soulgazer.png"), pressable);
    }

    public MainTab(int id, int x, int y, IPressable pressable) {
        super(id, x, y, new ResourceLocation(LycanitesMobs.MODID, "textures/items/soulgazer.png"), pressable);
    }

    @Override
    public void onTabClicked () {
	    Minecraft.getInstance().displayGuiScreen(new IndexBeastiaryScreen(Minecraft.getInstance().player));
    }

    @Override
    public boolean shouldAddToList () {
        return true;
    }
}
