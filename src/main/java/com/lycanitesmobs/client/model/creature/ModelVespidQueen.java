package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;
import com.lycanitesmobs.client.model.template.ModelTemplateInsect;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelVespidQueen extends ModelTemplateInsect {

    public ModelVespidQueen() {
        this(1.0F);
    }
    
    public ModelVespidQueen(float shadowSize) {
    	this.initModel("vespidqueen", LycanitesMobs.modInfo, "entity/vespidqueen");
		this.mouthScaleX = 2;
		this.mouthScaleY = 2;
    }
}
