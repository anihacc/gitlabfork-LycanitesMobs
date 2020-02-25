package com.lycanitesmobs.client.gui.buttons;

import com.lycanitesmobs.client.KeyHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;

public class MountTab extends Tab {
	
	public MountTab(int id) {
        super(id, Tab.startX, Tab.startY, new ResourceLocation("textures/items/saddle.png"));
    }

    @Override
    public void onTabClicked () {
        KeyBinding.setKeyBindState(KeyHandler.instance.mountInventory.getKeyCode(), true); //TODO Add a better way that works!
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
