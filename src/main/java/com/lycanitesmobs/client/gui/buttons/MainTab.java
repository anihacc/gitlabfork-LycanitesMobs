package com.lycanitesmobs.client.gui.buttons;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.beastiary.IndexBeastiaryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class MainTab extends Tab {

	public MainTab(int id, IPressable pressable) {
        super(id, Tab.startX, Tab.startY, new Identifier(LycanitesMobs.MODID, "textures/items/soulgazer.png"), pressable);
    }

    public MainTab(int id, int x, int y, IPressable pressable) {
        super(id, x, y, new Identifier(LycanitesMobs.MODID, "textures/items/soulgazer.png"), pressable);
    }

    @Override
    public void onTabClicked () {
	    MinecraftClient.getInstance().displayGuiScreen(new IndexBeastiaryScreen(MinecraftClient.getInstance().player));
    }

    @Override
    public boolean shouldAddToList () {
        return true;
    }
}
