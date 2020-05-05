package com.lycanitesmobs.client.model.creature;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class ModelAfrit extends ModelTemplateBiped {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelAfrit() {
        this(1.0F);
    }
    
    public ModelAfrit(float shadowSize) {
    	// Load Model:
    	this.initModel("afrit", LycanitesMobs.modInfo, "entity/afrit");

        // Trophy:
        this.trophyScale = 1.8F;
    }
}
