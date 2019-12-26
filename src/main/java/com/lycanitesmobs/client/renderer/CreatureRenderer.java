package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.client.ModelManager;
import com.lycanitesmobs.client.model.CreatureModel;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreatureRenderer extends MobRenderer<BaseCreatureEntity, CreatureModel> {
    public CreatureRenderer(String entityID, EntityRendererManager renderManager, float shadowSize) {
    	super(renderManager, ModelManager.getInstance().getCreatureModel(CreatureManager.getInstance().getCreature(entityID), null), shadowSize);
		if(this.entityModel == null)
			return;
    }

	/**
	 * Called by the main entity renderer to perform rendering.
	 * @param entity The entity to render.
	 * @param partialTicks The partial fraction of the animation tick.
	 * @param yaw The yaw rotation of the entity.
	 * @param matrixStack The entity matrix stack for animating with, etc.
	 * @param renderTypeBuffer  The render type buff for rendering with.
	 * @param brightness The brightness of the mob based on block location, etc.
	 */
	@Override
	public void func_225623_a_(BaseCreatureEntity entity, float partialTicks, float yaw, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int brightness) {
		// Get Model and Layers:
		try {
			this.layerRenderers.clear();
			this.entityModel = ModelManager.getInstance().getCreatureModel(entity.creatureInfo, entity.subspecies);
			this.entityModel.addCustomLayers(this);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		if(this.entityModel == null) {
			return;
		}

		// Get Entity States:
		float scale = 1;
		boolean shouldSit = entity.isPassenger() && (entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit());
		this.entityModel.isSitting = shouldSit;
		this.entityModel.isChild = entity.isChild();
		float renderYaw = MathHelper.func_219805_h(yaw, entity.prevRenderYawOffset, entity.renderYawOffset);
		float renderYawHead = MathHelper.func_219805_h(yaw, entity.prevRotationYawHead, entity.rotationYawHead);

		// Looking Yaw:
		float lookYaw = renderYawHead - renderYaw;
		if(shouldSit && entity.getRidingEntity() instanceof LivingEntity) {
			LivingEntity livingentity = (LivingEntity)entity.getRidingEntity();
			renderYaw = MathHelper.func_219805_h(yaw, livingentity.prevRenderYawOffset, livingentity.renderYawOffset);
			lookYaw = renderYawHead - renderYaw;
			float renderYawMountOffset = MathHelper.wrapDegrees(lookYaw);
			if (renderYawMountOffset < -85.0F) {
				renderYawMountOffset = -85.0F;
			}

			if (renderYawMountOffset >= 85.0F) {
				renderYawMountOffset = 85.0F;
			}

			renderYaw = renderYawHead - renderYawMountOffset;
			if (renderYawMountOffset * renderYawMountOffset > 2500.0F) {
				renderYaw += renderYawMountOffset * 0.2F;
			}

			lookYaw = renderYawHead - renderYaw;
		}

		// Looking Pitch:
		matrixStack.func_227860_a_();
		float lookPitch = MathHelper.lerp(yaw, entity.prevRotationPitch, entity.rotationPitch);
		if(entity.getPose() == Pose.SLEEPING) {
			Direction direction = entity.getBedDirection();
			if (direction != null) {
				float f4 = entity.getEyeHeight(Pose.STANDING) - 0.1F;
				matrixStack.func_227861_a_((double)((float)(-direction.getXOffset()) * f4), 0.0D, (double)((float)(-direction.getZOffset()) * f4));
			}
		}

		// Animation Ticks:
		float loop = this.handleRotationFloat(entity, partialTicks % 1.0F); // partialTicks is increased when turning for some reason
		this.func_225621_a_(entity, matrixStack, loop, renderYaw, yaw);
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

		// Damage Fade:
		int fade = 0;
		if(entity.hurtTime > 0) {
			fade = 10;
		}

		// Entity Visibility:
		boolean invisible = !this.func_225622_a_(entity, false);
		boolean allyInvisible = invisible && !entity.isInvisibleToPlayer(Minecraft.getInstance().player);

		// Render Model Layers:
		this.getMainModel().generateAnimationFrames(entity, time, distance, loop, lookYaw, lookPitch, 1, brightness);
		this.renderModel(entity, matrixStack, renderTypeBuffer, null, time, distance, loop, lookYaw, lookPitch, 1, brightness, fade, invisible, allyInvisible);
		for(LayerRenderer<BaseCreatureEntity, CreatureModel> layer : this.layerRenderers) {
			if(!(layer instanceof LayerCreatureBase)) {
				continue;
			}
			LayerCreatureBase layerCreatureBase = (LayerCreatureBase)layer;
			if(!layerCreatureBase.canRenderLayer(entity, scale)) {
				continue;
			}
			this.renderModel(entity, matrixStack, renderTypeBuffer, layerCreatureBase, time, distance, loop, lookYaw, lookPitch, scale, brightness, fade, invisible, allyInvisible);
		}
		this.getMainModel().clearAnimationFrames();
		matrixStack.func_227865_b_();
    }

	/**
	 * Renders the main model.
	 * @param entity The entity to render.
	 * @param matrixStack The matrix stack for animation.
	 * @param renderTypeBuffer  The render type buffer for rendering with.
	 * @param layer The layer to render, the base layer is null.
	 * @param time The current movement time for walk cycles, etc.
	 * @param distance The current movement amount for walk cycles, etc.
	 * @param loop A constant tick for looping animations.
	 * @param lookY The entity's yaw looking position for head rotation, etc.
	 * @param lookX The entity's pitch looking position for head rotation, etc.
	 * @param scale The base scale to render the model at, usually just 1 which scales 1m unit in Blender to a 1m block unit in Minecraft.
	 * @param brightness The brightness of the mob based on block location, etc.
	 * @param fade The damage fade to render (red flash when damaged).
	 * @param invisible If true, the entity has invisibility or some form of stealth.
	 * @param allyInvisible If true, the entity has invisibility or some form of stealth but is allied to the player so should be translucent, etc.
	 */
	protected void renderModel(BaseCreatureEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, LayerCreatureBase layer, float time, float distance, float loop, float lookY, float lookX, float scale, int brightness, int fade, boolean invisible, boolean allyInvisible) {
		ResourceLocation texture = this.getEntityTexture(entity, layer);
		RenderType rendertype;
		if (invisible && !allyInvisible) {
			rendertype = CustomRenderStates.getObjOutlineRenderType(texture);
		}
		else {
			rendertype = CustomRenderStates.getObjRenderType(texture, this.getMainModel().getBlending(entity, layer), this.getMainModel().getGlow(entity, layer));
		}
		// TODO allyInvisible lower color alpha
		this.getMainModel().render(entity, matrixStack, renderTypeBuffer.getBuffer(rendertype), layer, time, distance, loop, lookY, lookX, 1, brightness, fade);
    }

	/**
	 * Returns the main model used by this renderer.
	 * @return The main model to render.
	 */
	public CreatureModel getMainModel() {
		return this.entityModel;
	}

	/**
	 * Gets the texture to use.
	 * @param entity The entity to get the texture from.
	 * @param layer The layer to get the texture for.
	 * @return The texture to bind.
	 */
	public ResourceLocation getEntityTexture(BaseCreatureEntity entity, LayerCreatureBase layer) {
    	if(layer == null) {
			return this.getEntityTexture(entity);
		}
    	ResourceLocation layerTexture = layer.getLayerTexture(entity);
		return layerTexture != null ? layerTexture : this.getEntityTexture(entity);
	}

	@Override
	public ResourceLocation getEntityTexture(BaseCreatureEntity entity) {
		return entity.getTexture();
	}

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
