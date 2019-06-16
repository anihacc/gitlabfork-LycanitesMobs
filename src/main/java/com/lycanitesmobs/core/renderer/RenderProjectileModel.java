package com.lycanitesmobs.core.renderer;

import com.google.common.collect.Lists;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
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
public class RenderProjectileModel extends EntityRenderer<EntityProjectileBase> implements IEntityRenderer<EntityProjectileBase, ModelProjectileBase> {
	protected ModelProjectileBase renderModel;
	protected ModelProjectileBase defaultModel;
	protected final List<LayerRenderer<EntityProjectileBase, ModelProjectileBase>> renderLayers = Lists.newArrayList(); // TODO Layers for projectiles.

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
	public void doRender(EntityProjectileBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
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
				this.renderModel.render(entity, 0, 0, partialTicks, 0, 0, 1);
			}
			else {
				((ModelProjectileObj)this.renderModel).generateAnimationFrames(entity, 0, 0, partialTicks, 0, 0, 1);
				for (LayerRenderer<EntityProjectileBase, ModelProjectileBase> renderLayer : this.renderLayers) {
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
	public ModelProjectileBase func_217764_d() {
    	return this.getMainModel();
	}

	public final boolean addLayer(LayerRenderer<EntityProjectileBase, ModelProjectileBase> layer) {
		return this.renderLayers.add(layer);
	}
    
    
    // ==================================================
 	//                     Visuals
 	// ==================================================
    // ========== Main ==========
	@Override
    protected boolean bindEntityTexture(EntityProjectileBase entity) {
        ResourceLocation texture = this.getEntityTexture(entity);
        if(texture == null)
            return false;
        this.bindTexture(texture);
        return true;
    }

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityProjectileBase entity) {
		return entity.getTexture();
	}
}
