package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;

import com.lycanitesmobs.client.model.template.ModelTemplateQuadruped;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMakaAlpha extends ModelTemplateQuadruped {

	public ModelMakaAlpha() {
		this(1.0F);
	}

	public ModelMakaAlpha(float shadowSize) {
		this.initModel("MakaAlpha", LycanitesMobs.modInfo, "entity/makaalpha");

		this.trophyScale = 0.6F;
		this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
	}
}