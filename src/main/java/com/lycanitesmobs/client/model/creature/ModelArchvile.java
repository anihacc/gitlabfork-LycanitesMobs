package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
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
}
