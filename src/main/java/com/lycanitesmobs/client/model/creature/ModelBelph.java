package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBelph extends ModelTemplateBiped {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelBelph() {
        this(1.0F);
    }

    public ModelBelph(float shadowSize) {

        // Load Model:
        this.initModel("belph", LycanitesMobs.modInfo, "entity/belph");

        // Trophy:
        this.trophyScale = 1.2F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
    }
}
