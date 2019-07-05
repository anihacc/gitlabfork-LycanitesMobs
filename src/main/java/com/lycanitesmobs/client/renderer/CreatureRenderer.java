package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.client.ModelManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.client.model.ModelCreatureBase;
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
    public CreatureRenderer(String entityID, EntityRendererManager renderManager, float shadowSize) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    	super(renderManager, ModelManager.getInstance().getCreatureModel(CreatureManager.getInstance().getCreature(entityID), null), shadowSize);
		
    	this.defaultModel = this.field_77045_g;
		ModelCreatureBase modelCreatureBase = this.field_77045_g;
		if(modelCreatureBase == null)
			return;
		modelCreatureBase.addCustomLayers(this);

        //this.multipass = ConfigClient.INSTANCE.modelMultipass.get();
    }


	// ==================================================
	//                     Render
	// ==================================================
	/**
	 * Returns if this renderer should render multiple passes.
	 * @return True for multi pass rendering.
	 */
	@Override
	public boolean isMultipass() {
		//return this.multipass;
		return false; // Disabled as this doesn't have the desired effect.
	}

	@Override //doRender
	public void func_76986_a(BaseCreatureEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
		try {
			this.field_77045_g = ModelManager.getInstance().getCreatureModel(entity.creatureInfo, entity.subspecies);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.field_77045_g = this.defaultModel;

		if(this.field_77045_g == null) {
			return;
		}

    	super.func_76986_a(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	public void renderMultipass(BaseCreatureEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
		//super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected void renderModel(BaseCreatureEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
	}

	@Override
	protected void renderLayers(BaseCreatureEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		super.renderLayers(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleFactor);
	}

	public ModelCreatureBase getMainModel() {
		return this.field_77045_g;
	}
    
    
    // ==================================================
 	//                     Visuals
 	// ==================================================
    // ========== Main ==========
    @Override
    protected boolean bindEntityTexture(BaseCreatureEntity entity) {
        ResourceLocation texture = this.func_110775_a(entity);
        if(texture == null)
            return false;
        this.bindTexture(texture);
        return true;
    }
    
    @Override //getEntityTexture
    protected ResourceLocation func_110775_a(BaseCreatureEntity entity) {
		return entity.getTexture();
	}
    
    // ========== Equipment ==========
    protected void bindEquipmentTexture(BaseCreatureEntity entity, String equipmentName) {
        this.bindTexture(this.getEquipmentTexture(entity, equipmentName));
    }
    
    protected ResourceLocation getEquipmentTexture(BaseCreatureEntity entity, String equipmentName) {
    	if(entity != null)
    		return entity.getEquipmentTexture(equipmentName);
        return null;
    }
    
    
    // ==================================================
  	//                     Effects
  	// ==================================================
    @Override //preRenderCallback
    protected void func_77041_b(BaseCreatureEntity entity, float particleTickTime) {
        // No effects.
    }
    
    /** If true, display the name of the entity above it. **/
    @Override //canRenderName
    protected boolean func_177070_b(BaseCreatureEntity entity) {
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