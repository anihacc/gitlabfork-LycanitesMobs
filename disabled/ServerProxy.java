package com.lycanitesmobs.core;

import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.Subspecies;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ServerProxy implements IProxy {

	/**
	 * On server starting setup.
	 */
	@Override
	public void setup() {

	}

	@Override
	public PlayerEntity getClientPlayer() {
		return null;
	}

	@Override
	public String getMinecraftDir() {
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public net.minecraft.client.gui.FontRenderer getFontRenderer() {
		return null;
	}

	@Override
	public void loadCreatureModel(CreatureInfo creatureInfo, String modelPath) {

	}

	@Override
	public void loadSubspeciesModel(Subspecies subspecies, String modelPath) {

	}
}
