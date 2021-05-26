package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBehemophet extends ModelTemplateBiped {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelBehemophet() {
        this(1.0F);
    }

    public ModelBehemophet(float shadowSize) {

		// Load Model:
		this.initModel("behemophet", LycanitesMobs.modInfo, "entity/behemophet");

		// Trophy:
		this.trophyScale = 1.2F;
		this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
    }
}
