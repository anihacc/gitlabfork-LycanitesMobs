package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.client.model.ModelCustom;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;



@SideOnly(Side.CLIENT)
public class RenderProjectileModel extends Render {
	public ModelBase mainModel;

    // ==================================================
  	//                    Constructor
  	// ==================================================
	public RenderProjectileModel(RenderManager renderManager) {
		super(renderManager);
	}

    public RenderProjectileModel(RenderManager renderManager, String projectileName) {
    	super(renderManager);
    }


	// ==================================================
	//                    Do Render
	// ==================================================
	public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if(entity instanceof CustomProjectileEntity) {
			ProjectileInfo projectileInfo = ((CustomProjectileEntity)entity).projectileInfo;
			if(projectileInfo == null) {
				return;
			}
			try {
				this.mainModel = AssetManager.getProjectileModel(projectileInfo);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		else {
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.disableCull();

		try {
			GlStateManager.enableAlpha();
			if (this.bindEntityTexture(entity)) {
				GlStateManager.translate((float) x, (float) y - 0.25F, (float) z);
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				GlStateManager.rotate(entity.rotationYaw, 0.0F, 1.0F, 0.0F);
				this.mainModel.render(entity, 0, 0, partialTicks, 0, 0, 1);
				GlStateManager.depthMask(true);
				GlStateManager.disableRescaleNormal();
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}

		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}
    
    
    // ==================================================
 	//                     Visuals
 	// ==================================================
    // ========== Main ==========
	@Override
    protected boolean bindEntityTexture(Entity entity) {
        ResourceLocation texture = this.getEntityTexture(entity);
        if(texture == null)
            return false;
        this.bindTexture(texture);
        return true;
    }

	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
    	if(entity instanceof BaseProjectileEntity) {
			return ((BaseProjectileEntity)entity).getTexture();
		}
		return null;
    }
}
