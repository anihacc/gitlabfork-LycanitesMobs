package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.entity.LaserEndProjectileEntity;
import com.lycanitesmobs.core.entity.LaserProjectileEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class ProjectileSpriteRenderer extends EntityRenderer<BaseProjectileEntity> {
    private Class projectileClass;

    public ProjectileSpriteRenderer(EntityRendererManager renderManager, Class projectileClass) {
    	super(renderManager);
        this.projectileClass = projectileClass;
    }

    @Override
	public void render(BaseProjectileEntity entity, float partialTicks, float yaw, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int brightness) {
		if(entity instanceof CustomProjectileEntity && ((CustomProjectileEntity)entity).projectileInfo == null) {
			return;
		}
		if(entity.getClass() == LaserEndProjectileEntity.class) {
			return;
		}

		// Render States:
    	float loop = (float)entity.ticksExisted + Math.min(1, partialTicks);
    	float scale = entity.projectileScale;

		// Render Laser:
		if(entity instanceof CustomProjectileEntity && ((CustomProjectileEntity)entity).getLaserEnd() != null) {
			matrixStack.push();
			this.renderLaser((CustomProjectileEntity)entity, matrixStack, renderTypeBuffer, ((CustomProjectileEntity)entity).laserWidth / 4, loop);
			matrixStack.pop();
			return;
		}

    	// Render Projectile Sprite:
		matrixStack.push();
		matrixStack.multiply(this.renderManager.getCameraOrientation());
		matrixStack.multiply(new Vector3f(0.0F, 1.0F, 0.0F).getDegreesQuaternion(180.0F));
		matrixStack.multiply(new Vector3f(0, 0, 1).getDegreesQuaternion(loop * entity.rollSpeed)); // Projectile Spinning
		matrixStack.scale(scale, scale, scale); // Projectile Scaling
		matrixStack.translate(0, entity.getTextureOffsetY(), 0); // translate
		Identifier texture = this.getEntityTexture(entity);
		RenderType rendertype = CustomRenderStates.getSpriteRenderType(texture);
		this.renderSprite(entity, matrixStack, renderTypeBuffer, rendertype, entity.textureScale);
		matrixStack.pop();
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

		Matrix4f matrix4f = matrixStack.peek().getModel();
		BufferBuilder vertexBuilder = renderTypeBuffer.getBuffer(rendertype);
		vertexBuilder
				.pos(matrix4f, -textureWidth, -textureHeight + (textureHeight / 2), 0.0F) // pos
				.color(255, 255, 255, 255) // color
				.tex(minU, maxV) // texture
				.normal(0.0F, 1.0F, 0.0F) // normal
				.endVertex();
		vertexBuilder
				.pos(matrix4f, textureWidth, -textureHeight + (textureHeight / 2), 0.0F)
				.color(255, 255, 255, 255) // color
				.tex(maxU, maxV)
				.normal(0.0F, 1.0F, 0.0F)
				.endVertex();
		vertexBuilder
				.pos(matrix4f, textureWidth, textureHeight + (textureHeight / 2), 0.0F)
				.color(255, 255, 255, 255) // color
				.tex(maxU, minV)
				.normal(0.0F, 1.0F, 0.0F)
				.endVertex();
		vertexBuilder
				.pos(matrix4f, -textureWidth, textureHeight + (textureHeight / 2), 0.0F)
				.color(255, 255, 255, 255) // color
				.tex(minU, minV)
				.normal(0.0F, 1.0F, 0.0F)
				.endVertex();
    }

    public void renderLaser(CustomProjectileEntity entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float scale, float loop) {
    	double laserSize = entity.getPositionVec().distanceTo(entity.getLaserEnd());
		float spacing = 1;
		double factor = spacing / laserSize;
		if(laserSize <= 0) {
			return;
		}
		Identifier texture = this.getEntityTexture(entity);
		RenderType rendertype = CustomRenderStates.getSpriteRenderType(texture);
		Vec3d direction = entity.getLaserEnd().subtract(entity.getPositionVec()).normalize();
		for(float segment = 0; segment <= laserSize; segment += factor) {
			matrixStack.push();
			matrixStack.translate(segment * direction.getX() * spacing, segment * direction.getY() * spacing, segment * direction.getZ() * spacing);
			matrixStack.translate(0, entity.getTextureOffsetY(), 0); // translate
			matrixStack.multiply(this.renderManager.getCameraOrientation());
			matrixStack.multiply(new Vector3f(0.0F, 1.0F, 0.0F).getDegreesQuaternion(180.0F));
			matrixStack.scale(scale, scale, scale); // Laser Scaling
			matrixStack.multiply(new Vector3f(0, 0, 1).getDegreesQuaternion(loop * entity.rollSpeed)); // Projectile Spinning
			this.renderSprite(entity, matrixStack, renderTypeBuffer, rendertype, scale);
			matrixStack.pop();
		}
    }

    @Override
    public Identifier getEntityTexture(BaseProjectileEntity entity) {
		return entity.getTexture();
	}

    protected Identifier getLaserTexture(LaserProjectileEntity entity) {
    	return entity.getBeamTexture();
    }

	public void bindTexture(Identifier texture) {
		// TODO Remove
	}
}
