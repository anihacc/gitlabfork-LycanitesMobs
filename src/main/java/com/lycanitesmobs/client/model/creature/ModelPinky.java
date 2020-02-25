package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPinky extends ModelTemplateBiped {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelPinky() {
        this(1.0F);
    }

    public ModelPinky(float shadowSize) {

        // Load Model:
        this.initModel("pinky", LycanitesMobs.modInfo, "entity/pinky");

        // Head/Neck:
        this.lookHeadScaleX = 0.5F;
        this.lookHeadScaleY = 0.5F;
        this.lookNeckScaleX = 0.5F;
        this.lookNeckScaleY = 0.5F;
        this.bigChildHead = true;

        // Trophy:
        this.trophyScale = 1.2F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
    }
}
