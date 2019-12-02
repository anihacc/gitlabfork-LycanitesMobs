package com.lycanitesmobs.client.model.creature;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
