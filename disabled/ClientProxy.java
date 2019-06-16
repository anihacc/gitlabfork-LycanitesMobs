package com.lycanitesmobs.core;

import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.Subspecies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientProxy implements IProxy {

	/**
	 * On client starting setup.
	 */
	@Override
	public void setup() {

	}

	@Override
	public PlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	@Override
	public String getMinecraftDir() {
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public FontRenderer getFontRenderer() {
		return null;
	}

	@Override
	public void loadCreatureModel(CreatureInfo creatureInfo, String modelPath) {

	}

	@Override
	public void loadSubspeciesModel(Subspecies subspecies, String modelPath) {

	}
}
