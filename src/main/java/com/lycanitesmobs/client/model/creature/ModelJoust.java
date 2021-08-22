package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateQuadruped;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelJoust extends ModelTemplateQuadruped {
	public ModelJoust() {
		this(1.0F);
	}

	public ModelJoust(float shadowSize) {
		this.initModel("joust", LycanitesMobs.modInfo, "entity/joust");
		this.trophyScale = 1.0F;
		this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};

		this.lookHeadScaleX = 0.25F;
		this.lookHeadScaleY = 0.25F;
		this.lookNeckScaleX = 0.5F;
		this.lookNeckScaleY = 0.5F;

		this.legAnimationScale = 0;
	}

	float maxLeg = 0F;
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		float posY = 0F;
		float rotX = 0F;
		float rotY = 0F;
		float rotZ = 0F;
		float rotation = 0F;
		float angleY = 0F;
		float angleZ = 0F;

		this.lookHeadScaleX = 0.25F;
		this.lookHeadScaleY = 0.25F;
		this.lookNeckScaleX = 0.5F;
		this.lookNeckScaleY = 0.5F;

		// Leg Angles:
		if(partName.equals("legleftfront") || partName.equals("legleftback")
				|| partName.equals("legrightfront") || partName.equals("legrightback")) {
			angleZ = 1F;
		}
		if(partName.equals("legleftfront")) {
			angleY = 35F / 360F;
		}
		if(partName.equals("legleftback")) {
			angleY = -35F / 360F;
		}
		if(partName.equals("legrightfront")) {
			angleY = -35F / 360F;
		}
		if(partName.equals("legrightback")) {
			angleY = 35F / 360F;
		}

		// Walking:
		float walkSwing = 0.3F;
		if (partName.equals("legrightfront") || partName.equals("legleftback")) {
			rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float) Math.PI) * walkSwing * distance);
		}
		else if (partName.equals("legleftfront") || partName.equals("legrightback")) {
			rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
		}
		else if (partName.equals("body")) {
			float bob = MathHelper.cos(time * 0.6662F + (float) Math.PI) * walkSwing * distance;
			if (bob < 0) {
				bob += -bob * 2;
			}
			posY += bob;
		}

		// Attack:
		if(entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown()) {
			if(partName.equals("mouth")) {
				this.rotate(30.0F, 0.0F, 0.0F);
			}
		}

		this.angle(rotation, 0, angleY, angleZ);
		this.rotate(rotX, rotY, rotZ);
		this.translate(0, posY, 0);
	}
}
