package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.ModelManager;
import com.lycanitesmobs.client.model.ModelCreatureBase;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityWraamon;
import com.lycanitesmobs.core.info.CreatureManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.InvocationTargetException;

@OnlyIn(Dist.CLIENT)
public class CreatureRenderer extends MobRenderer<BaseCreatureEntity, ModelCreatureBase> {
	public boolean multipass = true;
	protected ModelCreatureBase defaultModel;

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public CreatureRenderer(String entityID, EntityRendererManager renderManager, float shadowSize) {
    	super(renderManager, ModelManager.getInstance().getCreatureModel(CreatureManager.getInstance().getCreature(entityID), null), shadowSize);
		
    	this.defaultModel = this.entityModel;
		ModelCreatureBase modelCreatureBase = this.entityModel;
		if(modelCreatureBase == null)
			return;
		modelCreatureBase.addCustomLayers(this);
    }


	// ==================================================
	//                     Render
	// ==================================================
	@Override
	public void func_225623_a_(BaseCreatureEntity entity, float somethingA, float yaw, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int brightness) {
    	if(entity.isTamed()) {
			//LycanitesMobs.logDebug("", "Render: " + somethingA + " " + somethingB + " " + brightness);
		}
		this.entityModel = this.defaultModel;
		try {
			this.entityModel = ModelManager.getInstance().getCreatureModel(entity.creatureInfo, entity.subspecies);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(this.entityModel == null) {
			return;
		}

		boolean shouldSit = entity.isPassenger() && (entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit());
		this.entityModel.isSitting = shouldSit;
		this.entityModel.isChild = entity.isChild();
		float f = MathHelper.func_219805_h(yaw, entity.prevRenderYawOffset, entity.renderYawOffset);
		float f1 = MathHelper.func_219805_h(yaw, entity.prevRotationYawHead, entity.rotationYawHead);
		float lookY = f1 - f;
		if (shouldSit && entity.getRidingEntity() instanceof LivingEntity) {
			LivingEntity livingentity = (LivingEntity)entity.getRidingEntity();
			f = MathHelper.func_219805_h(yaw, livingentity.prevRenderYawOffset, livingentity.renderYawOffset);
			lookY = f1 - f;
			float f3 = MathHelper.wrapDegrees(lookY);
			if (f3 < -85.0F) {
				f3 = -85.0F;
			}

			if (f3 >= 85.0F) {
				f3 = 85.0F;
			}

			f = f1 - f3;
			if (f3 * f3 > 2500.0F) {
				f += f3 * 0.2F;
			}

			lookY = f1 - f;
		}

		float lookX = MathHelper.lerp(yaw, entity.prevRotationPitch, entity.rotationPitch);
		if (entity.getPose() == Pose.SLEEPING) {
			Direction direction = entity.getBedDirection();
			if (direction != null) {
				float f4 = entity.getEyeHeight(Pose.STANDING) - 0.1F;
				matrixStack.func_227861_a_((double)((float)(-direction.getXOffset()) * f4), 0.0D, (double)((float)(-direction.getZOffset()) * f4));
			}
		}

		float loop = this.handleRotationFloat(entity, yaw);
		this.func_225621_a_(entity, matrixStack, loop, f, yaw);
		matrixStack.func_227862_a_(-1.0F, -1.0F, 1.0F);
		this.func_225620_a_(entity, matrixStack, yaw);
		matrixStack.func_227861_a_(0.0D, (double)-1.501F, 0.0D);
		float distance = 0.0F;
		float time = 0.0F;
		if (!shouldSit && entity.isAlive()) {
			distance = MathHelper.lerp(yaw, entity.prevLimbSwingAmount, entity.limbSwingAmount);
			time = entity.limbSwing - entity.limbSwingAmount * (1.0F - yaw);
			if (entity.isChild()) {
				time *= 3.0F;
			}

			if (distance > 1.0F) {
				distance = 1.0F;
			}
		}


		boolean isVisible = this.func_225622_a_(entity, false);
		boolean allyInvisible = !isVisible && !entity.isInvisibleToPlayer(Minecraft.getInstance().player);
		ResourceLocation resourceLocation = this.getEntityTexture(entity);
		RenderType rendertype;
		if (allyInvisible) {
			rendertype = RenderType.func_228644_e_(resourceLocation);
		}
		else if (isVisible) {
			rendertype = this.entityModel.func_228282_a_(resourceLocation);
		}
		else {
			rendertype = RenderType.func_228654_j_(resourceLocation);
		}

		this.bindEntityTexture(entity);
		this.getMainModel().render(entity, matrixStack, renderTypeBuffer.getBuffer(rendertype), null, time, distance, loop, lookY, lookX, 0.0625F, brightness, true);
    }

	public ModelCreatureBase getMainModel() {
		return this.entityModel;
	}
    
    
    // ==================================================
 	//                     Visuals
 	// ==================================================
    // ========== Main ==========
    public boolean bindEntityTexture(BaseCreatureEntity entity) {
        ResourceLocation texture = this.getEntityTexture(entity);
        if(texture == null)
            return false;
        this.bindTexture(texture);
        return true;
    }
    
    @Override
    public ResourceLocation getEntityTexture(BaseCreatureEntity entity) {
		return entity.getTexture();
	}

	public void bindTexture(ResourceLocation texture) {
		this.renderManager.textureManager.bindTexture(texture);
	}
    
    
    // ==================================================
  	//                     Effects
  	// ==================================================
    /** If true, display the name of the entity above it. **/
    @Override
    protected boolean canRenderName(BaseCreatureEntity entity) {
        if(!Minecraft.isGuiEnabled()) return false;
    	//if(entity == this.renderManager.pointedEntity) return false; // This was renderViewEntity not pointedEntity, perhaps for hiding name in inventory/beastiary view?
    	if(entity.isInvisibleToPlayer(Minecraft.getInstance().player)) return false;
    	if(entity.getControllingPassenger() != null) return false;
    	
    	if(entity.getAlwaysRenderNameTagForRender()) {
			if(entity.isTamed())
				return entity == this.renderManager.pointedEntity;
    		return true;
    	}
    	
    	return entity.hasCustomName() && entity == this.renderManager.pointedEntity;
    }
    
    
    // ==================================================
  	//                     Tools
  	// ==================================================
    /**
    * Returns a rotation angle that is inbetween two other rotation angles. par1 and par2 are the angles between which
    * to interpolate, par3 is probably a float between 0.0 and 1.0 that tells us where "between" the two angles we are.
    * Example: par1 = 30, par2 = 50, par3 = 0.5, then return = 40
    */
	public float interpolateRotation(float par1, float par2, float par3) {
		float f3;

		for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F) {}

		while (f3 >= 180.0F) {
			f3 -= 360.0F;
		}

		return par1 + par3 * f3;
	}
}
