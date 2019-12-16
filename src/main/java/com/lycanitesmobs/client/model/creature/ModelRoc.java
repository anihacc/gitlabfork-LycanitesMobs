package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelCreatureObj;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRoc extends ModelCreatureObj {

	// ==================================================
	//                    Constructors
	// ==================================================
	public ModelRoc() {
		this(1.0F);
	}

	public ModelRoc(float shadowSize) {
		// Load Model:
		this.initModel("roc", LycanitesMobs.modInfo, "entity/roc");

		// Trophy:
		this.trophyScale = 1.0F;
		this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
	}


	// ==================================================
	//                 Animate Part
	// ==================================================
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
		float posX = 0F;
		float posY = 0F;
		float posZ = 0F;
		float angleX = 0F;
		float angleY = 0F;
		float angleZ = 0F;
		float rotation = 0F;
		float rotX = 0F;
		float rotY = 0F;
		float rotZ = 0F;

		// Idle:
		if(partName.equals("mouth")) {
			this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.05F), 0.0F, 0.0F);
		}
		if(entity != null && !entity.isInWater()) {
			if (partName.equals("wingleft")) {
				rotX = 20;
				rotX -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
				rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
			}
			if (partName.equals("wingright")) {
				rotX = 20;
				rotX -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
				rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.4F + (float) Math.PI) * 0.6F);
			}
		}

		// Tail:
		if(entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).hasPickupEntity()) {
			if (partName.contains("tail0")) {
				rotX -= 5;
				rotX -= Math.toDegrees(MathHelper.sin(loop * 0.2F) * 0.02F);
			}
			if (partName.equals("tailclaw")) {
				rotX -= 25;
			}
		}
		else {
			if (partName.contains("tail0")) {
				rotX += 10;
				rotX -= Math.toDegrees(MathHelper.sin(loop * 0.1F) * 0.1F);
			}
			if (partName.equals("tailclaw")) {
				rotX += Math.toDegrees(MathHelper.sin(loop * 0.1F) * 0.4F);
			}
		}

		if(partName.equals("body")) {
			float bob = -MathHelper.sin(loop * 0.2F) * 0.1F;
			if (bob < 0)
				bob = -bob;
			posY += bob;
		}

		// Apply Animations:
		this.translate(posX, posY, posZ);
		this.angle(rotation, angleX, angleY, angleZ);
		this.rotate(rotX, rotY, rotZ);
	}
}
