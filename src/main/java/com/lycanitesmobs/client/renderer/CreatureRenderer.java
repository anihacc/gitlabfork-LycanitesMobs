package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.client.ModelManager;
import com.lycanitesmobs.client.model.ModelCreatureBase;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
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
	public void func_225621_a_(BaseCreatureEntity entity, MatrixStack matrixStack, float entityYaw, float partialTicks, float something) {
		this.entityModel = this.defaultModel;
		try {
			this.entityModel = ModelManager.getInstance().getCreatureModel(entity.creatureInfo, entity.subspecies);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(this.entityModel == null) {
			return;
		}

    	super.func_225621_a_(entity, matrixStack, entityYaw, partialTicks, something);
	}

	/*@Override
	protected void renderModel(BaseCreatureEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
	}

	@Override
	protected void renderLayers(BaseCreatureEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		super.renderLayers(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleFactor);
	}*/

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
