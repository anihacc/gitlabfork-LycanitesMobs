package com.lycanitesmobs;

import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.Subspecies;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

public class CommonProxy {
	
	// ========== Register Event Handlers ==========
    public void registerEvents() {

	}
    
	
    // ========== Get Minecraft Directory ==========
    public File getMinecraftDir() {
    	return new File(".");
    }
	
	
	// ========== Client Only ==========
	public void initLanguageManager() {}
    public void registerRenders(ModInfo groupInfo) {}
	public void registerTextures() {}
	public void registerModels(ModInfo groupInfo) {}
    public EntityPlayer getClientPlayer() { return null; }


    // ========== Renders ==========
    public void addBlockRender(ModInfo group, Block block) {}
    public void addItemRender(ModInfo group, Item item) {}


    // ========== Creatures ==========
	public void loadCreatureModel(CreatureInfo creature, String modelClassName) throws ClassNotFoundException {}
	public void loadSubspeciesModel(Subspecies subspecies, String modelClassName) throws ClassNotFoundException {}


	/**
	 * Returns the Font Renderer used by Lycnaites Mobs.
	 * @return A sexy Font Renderer, thanks for the heads up CedKilleur!
	 */
	@SideOnly(Side.CLIENT)
	public net.minecraft.client.gui.FontRenderer getFontRenderer() {
		return null;
	}
}
