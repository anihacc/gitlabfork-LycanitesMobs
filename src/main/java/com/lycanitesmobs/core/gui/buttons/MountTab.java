package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.KeyHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;

public class GuiTabMount extends GuiTab {
	
	public GuiTabMount(int id, IPressable pressable) {
        super(id, GuiTab.startX, GuiTab.startY, new ResourceLocation("textures/items/saddle.png"), pressable);
    }

    @Override
    public void onTabClicked () {
        KeyBinding.setKeyBindState(KeyHandler.instance.mountInventory.getKey(), true); //TODO Add a better way that works!
    }

    @Override
    public boolean shouldAddToList () {
    	return false;
        /*boolean ridingMount = Minecraft.getMinecraft().player.ridingEntity instanceof EntityCreatureRideable;
        if(ridingMount)
        	this.icon = ((EntityCreatureRideable)Minecraft.getMinecraft().player.ridingEntity).creatureInfo.getSprite();
        return ridingMount;*/
    }
}
