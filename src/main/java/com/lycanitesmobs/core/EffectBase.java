package com.lycanitesmobs.core;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.TextureManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.RenderProperties;
import net.minecraftforge.common.extensions.IForgeMobEffect;

public class EffectBase extends MobEffect implements IForgeMobEffect {
	public String name;

	public EffectBase(String name, boolean badEffect, int color) {
		super(badEffect ? MobEffectCategory.HARMFUL : MobEffectCategory.BENEFICIAL, color);
		this.name = name;
		this.setRegistryName(LycanitesMobs.MODID, name);
		TextureManager.addTexture("effect." + name, LycanitesMobs.modInfo, "textures/mob_effect/" + name + ".png");
	}

	@Override
	public boolean isInstantenous() {
        return false;
    }

	@OnlyIn(Dist.CLIENT)
	@Override
	public void renderInventoryEffect(MobEffectInstance effect, EffectRenderingInventoryScreen<?> gui, PoseStack mStack, int x, int y, float z) {
		ResourceLocation texture = TextureManager.getTexture("effect." + this.name);
		if(texture == null) {
			return;
		}

		Minecraft.getInstance().getTextureManager().bindForSetup(texture);
		GuiComponent.blit(mStack, x + 6, y + 7, 0, 0, 18, 18, 18, 18);
		super.renderInventoryEffect(effect, gui, mStack, x, y, z);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void renderHUDEffect(MobEffectInstance effect, GuiComponent gui, PoseStack mStack, int x, int y, float z, float alpha) {
		ResourceLocation texture = TextureManager.getTexture("effect." + this.name);
		if(texture == null) {
			return;
		}

		Minecraft.getInstance().getTextureManager().bindForSetup(texture);
		GuiComponent.blit(mStack, x + 3, y + 3, 0, 0, 18, 18, 18, 18);
	}
}
