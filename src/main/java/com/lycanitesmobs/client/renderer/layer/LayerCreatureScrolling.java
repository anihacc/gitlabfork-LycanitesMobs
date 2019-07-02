package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.renderer.CreatureRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.vecmath.Vector2f;

@OnlyIn(Dist.CLIENT)
public class LayerCreatureScrolling extends LayerCreatureEffect {

    // ==================================================
    //                   Constructor
    // ==================================================
	public LayerCreatureScrolling(CreatureRenderer renderer, String textureSuffix, boolean glow, int blending, boolean subspecies, Vector2f scrollSpeed) {
		super(renderer, textureSuffix, glow, blending, subspecies);
		this.scrollSpeed = scrollSpeed;
	}
}
