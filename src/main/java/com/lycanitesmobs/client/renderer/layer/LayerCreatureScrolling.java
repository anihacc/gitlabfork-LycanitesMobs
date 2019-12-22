package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.renderer.CreatureRenderer;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerCreatureScrolling extends LayerCreatureEffect {

    // ==================================================
    //                   Constructor
    // ==================================================
	public LayerCreatureScrolling(CreatureRenderer renderer, String textureSuffix, boolean glow, int blending, boolean subspecies, Vec2f scrollSpeed) {
		super(renderer, textureSuffix, glow, blending, subspecies);
		this.scrollSpeed = scrollSpeed;
	}
}
