package com.lycanitesmobs.core.localisation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class LanguageLoader implements IResourceManagerReloadListener {
	public static LanguageLoader INSTANCE;

	protected Map<String, String> map = new HashMap<>();

	/** Returns the main Item Manager instance or creates it and returns it. **/
	public static LanguageLoader getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new LanguageLoader();
		}
		return INSTANCE;
	}


	/**
	 * Called when the Resource Manager is reloaded including the initial load up of the game.
	 * @param resourceManager The resource manager instance.
	 */
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		LanguageManager.getInstance().loadLanguage(Minecraft.getMinecraft().gameSettings.language, resourceManager);
	}
}
