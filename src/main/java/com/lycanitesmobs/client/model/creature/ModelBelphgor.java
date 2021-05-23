package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBelphgor extends ModelTemplateBiped {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelBelphgor() {
        this(1.0F);
    }

    public ModelBelphgor(float shadowSize) {

		// Load Model:
		this.initModel("belphgor", LycanitesMobs.modInfo, "entity/belphgor");

		// Trophy:
		this.trophyScale = 1.2F;
		this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
    }
}
