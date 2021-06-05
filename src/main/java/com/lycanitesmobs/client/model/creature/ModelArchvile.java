package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
    public void addCustomLayers(RenderCreature renderer) {
        super.addCustomLayers(renderer);
        renderer.addLayer(new LayerCreatureEffect(renderer, "glow", true, LayerCreatureEffect.BLEND.ADD.id, true));
    }
}