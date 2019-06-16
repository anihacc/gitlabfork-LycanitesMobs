package com.lycanitesmobs.core.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.model.template.ModelTemplateBiped;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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
