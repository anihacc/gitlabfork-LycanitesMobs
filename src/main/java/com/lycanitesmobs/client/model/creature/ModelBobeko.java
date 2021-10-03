package com.lycanitesmobs.client.model.creature;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;
import com.lycanitesmobs.client.model.template.ModelTemplateQuadruped;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBobeko extends ModelTemplateQuadruped {
	public ModelBobeko() {
		this(1.0F);
	}

	public ModelBobeko(float shadowSize) {
		this.initModel("bobeko", LycanitesMobs.modInfo, "entity/bobeko");

		this.bigChildHead = true;
	}

	float maxLeg = 0F;
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
		float posX = 0F;
		float posY = 0F;
		float posZ = 0F;
		float rotX = 0F;
		float rotY = 0F;
		float rotZ = 0F;

		if(partName.equals("earleft")) {
			rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.18F) * 0.05F + 0.05F);
			rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
		}
		if(partName.equals("earright")) {
			rotZ += Math.toDegrees(MathHelper.cos(loop * 0.18F) * 0.05F + 0.05F);
			rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
		}

		// Apply Animations:
		translate(posX, posY, posZ);
		rotate(rotX, rotY, rotZ);
	}
}
