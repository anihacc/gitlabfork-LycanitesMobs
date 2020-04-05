package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelArchvile extends ModelTemplateBiped {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelArchvile() {
        this(1.0F);
    }

    public ModelArchvile(float shadowSize) {

        // Load Model:
        this.initModel("archvile", LycanitesMobs.modInfo, "entity/archvile");
    }
}
