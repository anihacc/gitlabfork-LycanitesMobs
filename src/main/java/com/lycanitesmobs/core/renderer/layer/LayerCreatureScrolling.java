package com.lycanitesmobs.core.renderer.layer;

import com.lycanitesmobs.core.renderer.RenderCreature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.vecmath.Vector2f;

@OnlyIn(Dist.CLIENT)
public class LayerScrolling extends LayerEffect {

    // ==================================================
    //                   Constructor
    // ==================================================
	public LayerScrolling(RenderCreature renderer, String textureSuffix, boolean glow, int blending, boolean subspecies, Vector2f scrollSpeed) {
		super(renderer, textureSuffix, glow, blending, subspecies);
		this.scrollSpeed = scrollSpeed;
	}
}
