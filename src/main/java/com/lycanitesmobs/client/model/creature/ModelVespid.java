package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;

import com.lycanitesmobs.client.model.template.ModelTemplateInsect;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelVespid extends ModelTemplateInsect {

    public ModelVespid() {
        this(1.0F);
    }
    
    public ModelVespid(float shadowSize) {
    	this.initModel("vespid", LycanitesMobs.modInfo, "entity/vespid");
		this.mouthScaleX = 2;
		this.mouthScaleY = 2;
    }
}
