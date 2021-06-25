package com.lycanitesmobs.client.model.creature;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class ModelBalayang extends ModelTemplateBiped {

	public ModelBalayang(float shadowSize) {
		this.initModel("balayang", LycanitesMobs.modInfo, "entity/balayang");
		this.trophyScale = 1.2F;
	}

	// ==================================================
	//                 Animate Part
	// ==================================================
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
		if (entity != null && (entity.onGround || entity.isInWater())) {

			if (partName.equals("wingleft01")) {
				float rotX = -40;
				float rotY = 20.5F;
				float rotZ = (float) Math.toDegrees(MathHelper.sin(loop * 0.4F * this.wingScale) * 0.6F);
				this.rotate(rotX, rotY, rotZ);
				return;
			}
			if (partName.equals("wingright01")) {
				float rotX = -40;
				float rotY = -20.5F;
				float rotZ = (float) Math.toDegrees(MathHelper.sin(loop * 0.4F * this.wingScale + (float) Math.PI) * 0.6F);
				this.rotate(rotX, rotY, rotZ);
				return;
			}
			if (partName.equals("wingleft02")) {
				float rotZ = (float) Math.toDegrees(MathHelper.sin(loop * 0.4F * this.wingScale) * 0.15F);
				this.rotate(0, 0, rotZ);
				return;
			}
			if (partName.equals("wingright02")) {
				float rotZ = (float) Math.toDegrees(MathHelper.sin(loop * 0.4F * this.wingScale + (float) Math.PI) * 0.15F);
				this.rotate(0, 0, rotZ);
				return;
			}
		}
	}
}