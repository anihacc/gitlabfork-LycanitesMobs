package com.lycanitesmobs.client.model.creature;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.CreatureObjModelOld;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBalayang extends ModelTemplateBiped {

	public ModelBalayang() {
		this(1.0F);
	}

	public ModelBalayang(float shadowSize) {
		this.initModel("balayang", LycanitesMobs.modInfo, "entity/balayang");
		this.trophyScale = 1.2F;
	}

	@Override
	public void animatePart(String partName, LivingEntity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		BaseCreatureEntity creatureEntity = null;
		if(entity instanceof BaseCreatureEntity)
			creatureEntity = (BaseCreatureEntity)entity;

		if(entity != null && !entity.isOnGround() && !entity.isInWater() && (creatureEntity == null || !creatureEntity.hasPerchTarget())) {
			if (partName.equals("wingleft01")) {
				float rotX = -40;
				float rotY = 20.5F;
				float rotZ = (float)Math.toDegrees(MathHelper.sin(loop * 0.4F * this.wingScale) * 0.6F);
				this.rotate(rotX, rotY, rotZ);
				return;
			}
			if (partName.equals("wingright01")) {
				float rotX = -40;
				float rotY = -20.5F;
				float rotZ = (float)Math.toDegrees(MathHelper.sin(loop * 0.4F * this.wingScale + (float) Math.PI) * 0.6F);
				this.rotate(rotX, rotY, rotZ);
				return;
			}
			if (partName.equals("wingleft02")) {
				float rotZ = (float)Math.toDegrees(MathHelper.sin(loop * 0.4F * this.wingScale) * 0.15F);
				this.rotate(0, 0, rotZ);
				return;
			}
			if (partName.equals("wingright02")) {
				float rotZ = (float)Math.toDegrees(MathHelper.sin(loop * 0.4F * this.wingScale + (float) Math.PI) * 0.15F);
				this.rotate(0, 0, rotZ);
				return;
			}
		}

		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
	}
}
