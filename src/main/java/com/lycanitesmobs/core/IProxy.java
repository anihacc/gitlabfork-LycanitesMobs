package com.lycanitesmobs.core;

import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.Subspecies;
import net.minecraft.entity.player.PlayerEntity;

public interface IProxy {
	PlayerEntity getClientPlayer();

	String getMinecraftDir();

	net.minecraft.client.gui.FontRenderer getFontRenderer();

	void loadCreatureModel(CreatureInfo creatureInfo, String modelPath);

	void loadSubspeciesModel(Subspecies subspecies, String modelPath);
}
