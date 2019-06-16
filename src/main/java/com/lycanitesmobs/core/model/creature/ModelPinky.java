package com.lycanitesmobs.core.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.model.template.ModelTemplateBiped;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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

		// Trophy:
		this.trophyScale = 1.2F;
		this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
    }
}
