package com.lycanitesmobs.core.renderer;

import com.google.common.collect.Lists;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.core.model.ModelProjectileBase;
import com.lycanitesmobs.core.model.ModelProjectileObj;
import com.lycanitesmobs.core.renderer.layer.LayerProjectileBase;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RenderProjectileModel extends EntityRenderer<BaseProjectileEntity> implements IEntityRenderer<BaseProjectileEntity, ModelProjectileBase> {
	protected ModelProjectileBase renderModel;
	protected ModelProjectileBase defaultModel;
	protected final List<LayerRenderer<BaseProjectileEntity, ModelProjectileBase>> renderLayers = Lists.newArrayList(); // TODO Layers for projectiles.

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public RenderProjectileModel(String entityID, EntityRendererManager renderManager) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    	super(renderManager);
		ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(entityID);
		if(projectileInfo != null) {
			this.renderModel = AssetManager.getProjectileModel(projectileInfo);
		}
    	else {
			this.renderModel = AssetManager.getOldProjectileModel(entityID);
		}
    	if(renderModel == null) {
    		return;
		}
		this.defaultModel = this.renderModel;
		this.renderModel.addCustomLayers(this);
    }


	// ==================================================
	//                    Do Render
	// ==================================================
	@Override
	public void func_76986_a(BaseProjectileEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();

		try {
			GlStateManager.enableAlphaTest();
			if (!this.bindEntityTexture(entity)) {
				return;
			}
			GlStateManager.translatef((float)x, (float)y - 0.25F, (float)z);
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.rotatef(entity.rotationYaw, 0.0F, 1.0F, 0.0F);

			if(!(this.renderModel instanceof ModelProjectileObj)) {
				this.renderModel.func_78088_a(entity, 0, 0, partialTicks, 0, 0, 1); //render
			}
			else {
				((ModelProjectileObj)this.renderModel).generateAnimationFrames(entity, 0, 0, partialTicks, 0, 0, 1);
				for (LayerRenderer<BaseProjectileEntity, ModelProjectileBase> renderLayer : this.renderLayers) {
					if (renderLayer instanceof LayerProjectileBase)
						this.renderModel.render(entity, 0, 0, partialTicks, 0, 0, 1, (LayerProjectileBase) renderLayer, false);
				}
				((ModelProjectileObj)this.renderModel).clearAnimationFrames();
			}

			GlStateManager.depthMask(true);
			GlStateManager.disableRescaleNormal();
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}

		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.enableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}

	public ModelProjectileBase getMainModel() {
		return this.renderModel;
	}

	@Override
	public ModelProjectileBase getEntityModel() {
    	return this.getMainModel();
	}

	public final boolean addLayer(LayerRenderer<BaseProjectileEntity, ModelProjectileBase> layer) {
		return this.renderLayers.add(layer);
	}
    
    
    // ==================================================
 	//                     Visuals
 	// ==================================================
    // ========== Main ==========
	@Override
    protected boolean bindEntityTexture(BaseProjectileEntity entity) {
        ResourceLocation texture = this.func_110775_a(entity);
        if(texture == null)
            return false;
        this.bindTexture(texture);
        return true;
    }

	@Nullable
	@Override
	protected ResourceLocation func_110775_a(BaseProjectileEntity entity) {
		return entity.getTexture();
	}
}
