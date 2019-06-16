package com.lycanitesmobs.core;

import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.Subspecies;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IProxy {

	void setup();

	PlayerEntity getClientPlayer();

	String getMinecraftDir();

	@OnlyIn(Dist.CLIENT)
	net.minecraft.client.gui.FontRenderer getFontRenderer();

	void loadCreatureModel(CreatureInfo creatureInfo, String modelPath);

	void loadSubspeciesModel(Subspecies subspecies, String modelPath);
}
