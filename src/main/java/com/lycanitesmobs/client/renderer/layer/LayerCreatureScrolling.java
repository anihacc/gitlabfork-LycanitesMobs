package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.renderer.CreatureRenderer;
import net.minecraft.util.math.Vec2f;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class LayerCreatureScrolling extends LayerCreatureEffect {
	public LayerCreatureScrolling(CreatureRenderer renderer, String textureSuffix, boolean glow, int blending, boolean subspecies, Vec2f scrollSpeed) {
		super(renderer, textureSuffix, glow, blending, subspecies);
		this.scrollSpeed = scrollSpeed;
	}
}
