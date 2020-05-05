package com.lycanitesmobs.client.gui.buttons;

import com.lycanitesmobs.client.KeyHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Identifier;

public class MountTab extends Tab {
	
	public MountTab(int id, IPressable pressable) {
        super(id, Tab.startX, Tab.startY, new Identifier("textures/items/saddle.png"), pressable);
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
