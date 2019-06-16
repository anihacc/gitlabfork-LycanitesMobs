package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.config.ConfigClient;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.model.ModelCreatureBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.InvocationTargetException;

@OnlyIn(Dist.CLIENT)
public class RenderCreature extends LivingRenderer<EntityCreatureBase, ModelCreatureBase> {
	public boolean multipass = true;
	protected ModelCreatureBase defaultModel;
	
	/** A color table for mobs that can be dyed or pet collars. Follows the same pattern as the vanilla sheep. */
	public static final float[][] colorTable = new float[][] {{1.0F, 1.0F, 1.0F}, {0.85F, 0.5F, 0.2F}, {0.7F, 0.3F, 0.85F}, {0.4F, 0.6F, 0.85F}, {0.9F, 0.9F, 0.2F}, {0.5F, 0.8F, 0.1F}, {0.95F, 0.5F, 0.65F}, {0.3F, 0.3F, 0.3F}, {0.6F, 0.6F, 0.6F}, {0.3F, 0.5F, 0.6F}, {0.5F, 0.25F, 0.7F}, {0.2F, 0.3F, 0.7F}, {0.4F, 0.3F, 0.2F}, {0.4F, 0.5F, 0.2F}, {0.6F, 0.2F, 0.2F}, {0.1F, 0.1F, 0.1F}};

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public RenderCreature(String entityID, EntityRendererManager renderManager, float shadowSize) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    	super(renderManager, AssetManager.getCreatureModel(CreatureManager.getInstance().getCreature(entityID)), shadowSize);
		
    	this.defaultModel = this.field_77045_g;
		ModelCreatureBase modelCreatureBase = this.field_77045_g;
		modelCreatureBase.addCustomLayers(this);

        this.multipass = ConfigClient.INSTANCE.modelMultipass.get();
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

	@Override
	public void doRender(EntityCreatureBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
		try {
			this.field_77045_g = AssetManager.getCreatureModel(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.field_77045_g = this.defaultModel;

		if(this.field_77045_g == null) {
			return;
		}

    	super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	public void renderMultipass(EntityCreatureBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
		//super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected void renderModel(EntityCreatureBase entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
	}

	@Override
	protected void renderLayers(EntityCreatureBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
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
    protected boolean bindEntityTexture(EntityCreatureBase entity) {
        ResourceLocation texture = this.getEntityTexture(entity);
        if(texture == null)
            return false;
        this.bindTexture(texture);
        return true;
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntityCreatureBase entity) {
		return entity.getTexture();
	}
    
    // ========== Equipment ==========
    protected void bindEquipmentTexture(EntityCreatureBase entity, String equipmentName) {
        this.bindTexture(this.getEquipmentTexture(entity, equipmentName));
    }
    
    protected ResourceLocation getEquipmentTexture(EntityCreatureBase entity, String equipmentName) {
    	if(entity != null)
    		return entity.getEquipmentTexture(equipmentName);
        return null;
    }
    
    
    // ==================================================
  	//                     Effects
  	// ==================================================
    @Override
    protected void preRenderCallback(EntityCreatureBase entity, float particleTickTime) {
        // No effects.
    }
    
    /** If true, display the name of the entity above it. **/
    @Override
    protected boolean canRenderName(EntityCreatureBase entity) {
        if(!Minecraft.isGuiEnabled()) return false;
    	//if(entity == this.field_76990_c.pointedEntity) return false; // This was renderViewEntity not pointedEntity, perhaps for hiding name in inventory/beastiary view?
    	if(entity.isInvisibleToPlayer(Minecraft.getInstance().player)) return false;
    	if(entity.getControllingPassenger() != null) return false;
    	
    	if(entity.getAlwaysRenderNameTagForRender()) {
    		if(entity instanceof EntityCreatureTameable)
    			if(((EntityCreatureTameable)entity).isTamed())
    				return entity == this.field_76990_c.pointedEntity;
    		return true;
    	}
    	
    	return entity.hasCustomName() && entity == this.field_76990_c.pointedEntity;
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
