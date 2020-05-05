package com.lycanitesmobs.core;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.TextureManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Identifier;

public class EffectBase extends StatusEffect {
	public String name;
	
	// ==================================================
	//                    Constructor
	// ==================================================
	public EffectBase(String name, boolean badEffect, int color) {
		super(badEffect ? StatusEffectType.HARMFUL : StatusEffectType.BENEFICIAL, color);
		this.name = name;
		TextureManager.addTexture("effect." + name, LycanitesMobs.modInfo, "textures/mob_effect/" + name + ".png");
	}
	
	
	// ==================================================
	//                    Effects
	// ==================================================
	@Override
	public boolean isInstant() {
        return false;
    }
	
	
	// ==================================================
	//                    Visuals
	// ==================================================
	@Environment(EnvType.CLIENT)
	@Override
	public void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, int x, int y, float z) {
		Identifier texture = TextureManager.getTexture("effect." + this.name);
		if(texture == null) {
			return;
		}

		MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
		gui.blit(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void renderHUDEffect(EffectInstance effect, AbstractGui gui, int x, int y, float z, float alpha) {
		Identifier texture = TextureManager.getTexture("effect." + this.name);
		if(texture == null) {
			return;
		}

		MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
		gui.blit(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
	}
}
