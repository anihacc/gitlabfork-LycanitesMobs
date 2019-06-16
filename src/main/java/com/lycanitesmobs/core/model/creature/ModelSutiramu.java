package com.lycanitesmobs.core.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.template.ModelTemplateArachnid;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class ModelSutiramu extends ModelTemplateArachnid {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelSutiramu() {
        this(1.0F);
    }

    public ModelSutiramu(float shadowSize) {
        // Load Model:
        this.initModel("sutiramu", LycanitesMobs.modInfo, "entity/sutiramu");

        // Looking:
        this.lookHeadScaleX = 0f;
        this.lookHeadScaleY = 0f;

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
        this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
        this.bodyIsTrophy = true;
    }
}
