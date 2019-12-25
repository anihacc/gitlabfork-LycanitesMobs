package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.entity.LaserProjectileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProjectileSpriteRenderer extends EntityRenderer<BaseProjectileEntity> {
    private Class projectileClass;

    public ProjectileSpriteRenderer(EntityRendererManager renderManager, Class projectileClass) {
    	super(renderManager);
        this.projectileClass = projectileClass;
    }

    @Override
	public void func_225623_a_(BaseProjectileEntity entity, float partialTicks, float yaw, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int brightness) {
		if(entity instanceof CustomProjectileEntity && ((CustomProjectileEntity)entity).projectileInfo == null) {
			return;
		}

		// Render States:
    	float loop = (float)entity.ticksExisted + partialTicks;
    	float scale = entity.projectileScale;

    	// Render Projectile Sprite:
		matrixStack.func_227860_a_();
		matrixStack.func_227863_a_(this.renderManager.func_229098_b_());
		matrixStack.func_227863_a_(new Vector3f(0.0F, 1.0F, 0.0F).func_229187_a_(180.0F));
		matrixStack.func_227863_a_(new Vector3f(0, 0, 1).func_229187_a_(loop * entity.spinSpeed)); // Projectile Spinning
		matrixStack.func_227862_a_(scale, scale, scale); // Projectile Scaling
		matrixStack.func_227861_a_(0, entity.getTextureOffsetY(), 0); // translate
		ResourceLocation texture = this.getEntityTexture(entity);
		RenderType rendertype = CustomRenderStates.getSpriteRenderType(texture);
		this.renderSprite(entity, matrixStack, renderTypeBuffer, rendertype, entity.textureScale);
		matrixStack.func_227865_b_();

		// Render Laser:
		if(entity instanceof LaserProjectileEntity) {
			matrixStack.func_227860_a_();
			this.renderLaser((LaserProjectileEntity)entity, matrixStack, renderTypeBuffer, ((LaserProjectileEntity)entity).laserWidth / 4);
			matrixStack.func_227865_b_();
		}
    }

    public void renderSprite(BaseProjectileEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, RenderType rendertype, float scale) {
		float textureWidth = 0.5F;
		float textureHeight = 0.5F;
		float minU = 0;
		float maxU = 1;
		float minV = 0;
		float maxV = 1;
		if(entity.animationFrameMax > 0) {
			minV = (float)entity.animationFrame / (float)entity.animationFrameMax;
			maxV = minV + (1F / (float)entity.animationFrameMax);
			textureWidth *= scale;
			textureHeight *= scale;
		}

		Matrix4f matrix4f = matrixStack.func_227866_c_().func_227870_a_();
		IVertexBuilder vertexBuilder = renderTypeBuffer.getBuffer(rendertype);
		vertexBuilder
				.func_227888_a_(matrix4f, -textureWidth, -textureHeight + (textureHeight / 2), 0.0F) // pos
				.func_227885_a_(1, 1, 1, 1) // color
				.func_225583_a_(minU, maxV) // texture
				.func_225584_a_(0.0F, 1.0F, 0.0F) // normal
				.endVertex();
		vertexBuilder
				.func_227888_a_(matrix4f, textureWidth, -textureHeight + (textureHeight / 2), 0.0F)
				.func_227885_a_(1, 1, 1, 1)
				.func_225583_a_(maxU, maxV)
				.func_225584_a_(0.0F, 1.0F, 0.0F)
				.endVertex();
		vertexBuilder
				.func_227888_a_(matrix4f, textureWidth, textureHeight + (textureHeight / 2), 0.0F)
				.func_227885_a_(1, 1, 1, 1)
				.func_225583_a_(maxU, minV)
				.func_225584_a_(0.0F, 1.0F, 0.0F)
				.endVertex();
		vertexBuilder
				.func_227888_a_(matrix4f, -textureWidth, textureHeight + (textureHeight / 2), 0.0F)
				.func_227885_a_(1, 1, 1, 1)
				.func_225583_a_(minU, minV)
				.func_225584_a_(0.0F, 1.0F, 0.0F)
				.endVertex();
    }

    public void renderLaser(LaserProjectileEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float scale) {
		float laserSize = entity.getLength();
		float spacing = 1;
		float factor = spacing / laserSize;
		if(laserSize <= 0 || entity.getLaserEnd() == null) {
			return;
		}
		ResourceLocation texture = this.getEntityTexture(entity); // TODO Fancy laser textures.
		RenderType rendertype = CustomRenderStates.getSpriteRenderType(texture);
		Vec3d direction = entity.getLaserEnd().getPositionVec().subtract(entity.getPositionVec()).normalize();
		for(float segment = 0; segment <= laserSize; segment += factor) {
			matrixStack.func_227860_a_();
			matrixStack.func_227861_a_(segment * direction.getX() * spacing, segment * direction.getY() * spacing, segment * direction.getZ() * spacing);
			matrixStack.func_227861_a_(0, entity.getTextureOffsetY(), 0); // translate
			matrixStack.func_227863_a_(this.renderManager.func_229098_b_());
			matrixStack.func_227863_a_(new Vector3f(0.0F, 1.0F, 0.0F).func_229187_a_(180.0F));
			matrixStack.func_227862_a_(scale, scale, scale); // Laser Scaling
			this.renderSprite(entity, matrixStack, renderTypeBuffer, rendertype, scale);
			matrixStack.func_227865_b_();
		}
    }

    @Override
    public ResourceLocation getEntityTexture(BaseProjectileEntity entity) {
		return entity.getTexture();
	}

    protected ResourceLocation getLaserTexture(LaserProjectileEntity entity) {
    	return entity.getBeamTexture();
    }

	public void bindTexture(ResourceLocation texture) {
		// TODO Remove
	}
}
