package com.lycanitesmobs.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class CustomFontRenderer extends FontRenderer {
	public CustomFontRenderer(Function<ResourceLocation, Font> font) {
		super(font);
	}

//	public CustomFontRenderer(TextureManager textureManager, Font font) {
//		super(textureManager, font);
//	}
}
