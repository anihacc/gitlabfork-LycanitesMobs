package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import com.lycanitesmobs.client.renderer.CreatureRenderer;
import com.lycanitesmobs.client.renderer.CustomRenderStates;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelArchvile extends ModelTemplateBiped {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelArchvile() {
        this(1.0F);
    }

    public ModelArchvile(float shadowSize) {

        // Load Model:
        this.initModel("archvile", LycanitesMobs.modInfo, "entity/archvile");
    }

    @Override
    public void addCustomLayers(CreatureRenderer renderer) {
        super.addCustomLayers(renderer);
        renderer.addLayer(new LayerCreatureEffect(renderer, "glow", true, CustomRenderStates.BLEND.ADD.id, true));
    }
}
