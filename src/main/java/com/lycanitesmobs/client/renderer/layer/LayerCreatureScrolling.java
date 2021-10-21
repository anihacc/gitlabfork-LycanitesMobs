package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.renderer.CreatureRenderer;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerCreatureScrolling extends LayerCreatureEffect {
	public LayerCreatureScrolling(CreatureRenderer renderer, String textureSuffix, boolean glow, int blending, boolean subspecies, Vec2 scrollSpeed) {
		super(renderer, textureSuffix, glow, blending, subspecies);
		this.scrollSpeed = scrollSpeed;
	}
}
