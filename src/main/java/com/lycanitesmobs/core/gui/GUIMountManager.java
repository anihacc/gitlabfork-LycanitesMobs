package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.core.network.MessageGUIRequest;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GUIMountManager extends GUIBaseManager {

	// ==================================================
	//                      Opener
	// ==================================================
	public static void openToPlayer(EntityPlayer player) {
		if(player != null && player.worldObj != null) {
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.PLAYER.id, player.worldObj, GuiHandler.PlayerGuiType.MOUNT_MANAGER.id, 0, 0);
			MessageGUIRequest message = new MessageGUIRequest(GuiHandler.PlayerGuiType.MOUNT_MANAGER.id);
			LycanitesMobs.packetHandler.sendToServer(message);
		}
	}

	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIMountManager(EntityPlayer player) {
		super(player, "mount");
	}


	// ==================================================
	//                     Get Texture
	// ==================================================
	@Override
	protected ResourceLocation getTexture() {
		return AssetManager.getTexture("GUIMount");
	}
}