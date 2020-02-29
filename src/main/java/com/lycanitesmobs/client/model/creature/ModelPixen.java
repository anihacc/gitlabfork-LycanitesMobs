package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPixen extends ModelTemplateBiped {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelPixen() {
        this(1.0F);
    }

    public ModelPixen(float shadowSize) {
    	// Load Model:
    	this.initModel("pixen", LycanitesMobs.modInfo, "entity/pixen");

        // Scales:
        this.trophyScale = 1.8F;
        this.trophyOffset = new float[] {0.0F, -0.05F, -0.1F};
        this.wingScale = 4;
    }

    @Override
    public void addCustomLayers(RenderCreature renderer) {
        super.addCustomLayers(renderer);
        renderer.addLayer(new LayerEffect(renderer, "glow", true, LayerEffect.BLEND.ADD.id, true));
    }
}
