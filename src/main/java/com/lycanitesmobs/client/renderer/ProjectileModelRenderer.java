package com.lycanitesmobs.client.renderer;

import com.google.common.collect.Lists;
import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.client.ModelManager;
import com.lycanitesmobs.client.model.ModelProjectileBase;
import com.lycanitesmobs.client.model.ModelProjectileObj;
import com.lycanitesmobs.client.renderer.layer.LayerProjectileBase;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ProjectileModelRenderer extends EntityRenderer<BaseProjectileEntity> implements IEntityRenderer<BaseProjectileEntity, ModelProjectileBase> {
	protected ModelProjectileBase renderModel;
	protected ModelProjectileBase defaultModel;
	protected final List<LayerRenderer<BaseProjectileEntity, ModelProjectileBase>> renderLayers = Lists.newArrayList(); // TODO Layers for projectiles.

    // ==================================================
  	//                    Constructor
  	// ==================================================
	public ProjectileModelRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

    public ProjectileModelRenderer(EntityRendererManager renderManager, String projectileName) {
    	super(renderManager);
		ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(projectileName);
		if(projectileInfo != null) {
			this.renderModel = ModelManager.getInstance().getProjectileModel(projectileInfo);
		}
    	else {
			this.renderModel = ModelManager.getInstance().getOldProjectileModel(projectileName);
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
	protected void func_225629_a_(BaseProjectileEntity entity, String someString, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int ticks) {
		if(this.renderModel == null) {
			if(entity instanceof CustomProjectileEntity) {
				ProjectileInfo projectileInfo = ((CustomProjectileEntity)entity).projectileInfo;
				if(projectileInfo == null) {
					return;
				}
				this.renderModel = ModelManager.getInstance().getProjectileModel(projectileInfo);
				this.defaultModel = this.renderModel;
			}
			else {
				return;
			}
		}

		RenderSystem.pushMatrix();
		RenderSystem.disableCull();

		float x = 0; // TODO
		float y = 0;
		float z = 0;

		try {
			RenderSystem.enableAlphaTest();
			if (this.bindEntityTexture(entity)) {
				RenderSystem.translatef(x, y - 0.25F, z);
				RenderSystem.scalef(0.25F, 0.25F, 0.25F);
				RenderSystem.rotatef(entity.rotationYaw, 0.0F, 1.0F, 0.0F);

				if (!(this.renderModel instanceof ModelProjectileObj)) {
					this.renderModel.func_225597_a_(entity, 0, 0, ticks, 0, 0); //render
				}
				else {
					((ModelProjectileObj) this.renderModel).generateAnimationFrames(entity, 0, 0, ticks, 0, 0, 1);
					this.renderModel.render(entity, 0, 0, ticks, 0, 0, 1, null, false);
					for (LayerRenderer<BaseProjectileEntity, ModelProjectileBase> renderLayer : this.renderLayers) {
						if (renderLayer instanceof LayerProjectileBase)
							this.renderModel.render(entity, 0, 0, ticks, 0, 0, 1, (LayerProjectileBase) renderLayer, false);
					}
					((ModelProjectileObj) this.renderModel).clearAnimationFrames();
				}
				RenderSystem.depthMask(true);
				RenderSystem.disableRescaleNormal();
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}

		RenderSystem.activeTexture(ClientManager.GL_TEXTURE1);
		RenderSystem.enableTexture();
		RenderSystem.activeTexture(ClientManager.GL_TEXTURE0);
		RenderSystem.enableCull();
		RenderSystem.popMatrix();
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
    public boolean bindEntityTexture(BaseProjectileEntity entity) {
        ResourceLocation texture = this.getEntityTexture(entity);
        if(texture == null)
            return false;
        this.bindTexture(texture);
        return true;
    }

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(BaseProjectileEntity entity) {
		return entity.getTexture();
	}

	public void bindTexture(ResourceLocation texture) {
		this.renderManager.textureManager.bindTexture(texture);
	}
}
