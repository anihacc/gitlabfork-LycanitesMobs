package com.lycanitesmobs.core.renderer;

import net.minecraft.util.math.Vec2f;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerScrolling extends LayerEffect {

    // ==================================================
    //                   Constructor
    // ==================================================
	public LayerScrolling(RenderCreature renderer, String textureSuffix, boolean glow, int blending, boolean subspecies, Vec2f scrollSpeed) {
		super(renderer, textureSuffix, glow, blending, subspecies);
		this.scrollSpeed = scrollSpeed;
	}
}
