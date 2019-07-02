package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateQuadruped;


public class ModelQuillbeast extends ModelTemplateQuadruped {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelQuillbeast() {
        this(1.0F);
    }

    public ModelQuillbeast(float shadowSize) {
        // Load Model:
        this.initModel("quillbeast", LycanitesMobs.modInfo, "entity/quillbeast");

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
        this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
    }
}
