package com.lycanitesmobs.core.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.model.template.ModelTemplateBiped;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBehemoth extends ModelTemplateBiped {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelBehemoth() {
        this(1.0F);
    }

    public ModelBehemoth(float shadowSize) {

        // Load Model:
        this.initModel("behemoth", LycanitesMobs.modInfo, "entity/behemoth");

        // Trophy:
        this.trophyScale = 1.2F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
    }
}
