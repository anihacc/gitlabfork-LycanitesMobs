package com.lycanitesmobs.client.model.creature;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class ModelBalayang extends ModelTemplateBiped {

	public ModelBalayang() {
		this(1.0F);
	}

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
			// Looking:
			float rotX = 0;
			float rotY = 0;
			if(partName.equals("eye")) {
				rotX += (Math.toDegrees(lookX / (180F / (float) Math.PI)) * 0.25F);
				rotY += (Math.toDegrees(lookY / (180F / (float) Math.PI))) * 0.25F;
			}
			if (partName.equals("wingleft01")) {
				rotX = -40;
				rotY = 20.5F;
				float rotZ = (float) Math.toDegrees(MathHelper.sin(loop * 0.4F * this.wingScale) * 0.6F);
				this.rotate(rotX, rotY, rotZ);
				return;
			}
			if (partName.equals("wingright01")) {
				rotX = -40;
				rotY = -20.5F;
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