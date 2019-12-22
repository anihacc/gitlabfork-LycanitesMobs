package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.entity.LaserProjectileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProjectileSpriteRenderer extends EntityRenderer<BaseProjectileEntity> {
    private int renderTime = 0;
    Class projectileClass;
    
    // Laser Box:
    protected Model laserModel;
    private ModelRenderer laserBox;
    
    // ==================================================
    //                     Constructor
    // ==================================================
    public ProjectileSpriteRenderer(EntityRendererManager renderManager, Class projectileClass) {
    	super(renderManager);
        this.projectileClass = projectileClass;
    }
    
    
    // ==================================================
    //                     Do Render
    // ==================================================
    @Override
	protected void func_225629_a_(BaseProjectileEntity entity, String someString, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int ticks) {
    	if(this.renderTime++ > Integer.MAX_VALUE - 1)
            this.renderTime = 0;

    	float x = 0;
    	float y = 0;
    	float z = 0;
    	float yaw = 0;

        this.renderProjectile(entity, x, y, z, yaw, ticks);
    	if(entity instanceof LaserProjectileEntity)
    		this.renderLaser((LaserProjectileEntity)entity, x, y, z, yaw, ticks);
    }
    
    
    // ==================================================
    //                 Render Projectile
    // ==================================================
    public void renderProjectile(BaseProjectileEntity entity, double x, double y, double z, float entityYaw, int ticks) {
    	double scale = 0.5d;
    	if(entity instanceof CustomProjectileEntity && ((CustomProjectileEntity)entity).projectileInfo == null) {
    		return;
		}
        if(entity != null) {
            scale *= entity.getProjectileScale();
            y += entity.getTextureOffsetY();
        }

        RenderSystem.pushMatrix();
		RenderSystem.translatef((float) x, (float) y, (float) z);
		RenderSystem.enableRescaleNormal();
		RenderSystem.scaled(scale, scale, scale);

        this.bindTexture(this.getEntityTexture(entity));
        this.renderTexture(Tessellator.getInstance(), entity);

		RenderSystem.disableRescaleNormal();
		RenderSystem.popMatrix();
    }


    // ==================================================
    //                  Render Texture
    // ==================================================
    private void renderTexture(Tessellator tessellator, BaseProjectileEntity entity) {
        float minU = 0;
		float maxU = 1;
		float minV = 0;
		float maxV = 1;
        double textureWidth = 0.5D;
        double textureHeight = 0.5D;
        double offsetY = 0;
        if(entity != null) {
            if(entity.animationFrameMax > 0) {
                minV = (float)entity.animationFrame / (float)entity.animationFrameMax;
                maxV = minV + (1F / (float)entity.animationFrameMax);
                textureWidth *= entity.textureScale;
                textureHeight *= entity.textureScale;
                offsetY = entity.textureOffsetY;
            }
        }

		//RenderSystem.rotatef(-this.renderManager.getRenderer().playerViewY, 0.0F, 1.0F, 0.0F); TODO Figure out how to do sprites again...
		//RenderSystem.rotatef((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		RenderSystem.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
        //RenderSystem.translatef(-scale / 2, -scale / 2, -scale / 2);

        /*if(this.renderOutlines) {
            RenderSystem.enableColorMaterial();
			RenderSystem.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
        }*/

        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

        vertexbuffer.func_225582_a_(-textureWidth, -textureHeight + (textureHeight / 2) + offsetY, 0.0D)
                .func_225583_a_(minU, maxV)
				.func_227885_a_(1, 1, 1, 1)
                .func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.func_225582_a_(textureWidth, -textureHeight + (textureHeight / 2) + offsetY, 0.0D)
                .func_225583_a_(maxU, maxV)
				.func_227885_a_(1, 1, 1, 1)
                .func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.func_225582_a_(textureWidth, textureHeight + (textureHeight / 2) + offsetY, 0.0D)
                .func_225583_a_(maxU, minV)
				.func_227885_a_(1, 1, 1, 1)
                .func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.func_225582_a_(-textureWidth, textureHeight + (textureHeight / 2) + offsetY, 0.0D)
                .func_225583_a_(minU, minV)
				.func_227885_a_(1, 1, 1, 1)
                .func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();

        tessellator.draw();
    }
    
    
    // ==================================================
    //                 Render Laser
    // ==================================================
    public void renderLaser(LaserProjectileEntity entity, double x, double y, double z, float par8, float par9) {
    	float scale = entity.getLaserWidth();
    	
    	/*/ Create Laser Model If Null:
    	if(this.laserBox == null) {
			this.laserBox = new ModelRenderer(this.laserModel, 0, 0);
			this.laserBox.addBox(-(scale / 2), -(scale / 2), 0, (int)scale, (int)scale, 16);
			this.laserBox.rotationPointX = 0;
			this.laserBox.rotationPointY = 0;
			this.laserBox.rotationPointZ = 0;
    	} TODO Laser */
        
    	float factor = (float)(1.0 / 16.0);
    	float lastSegment = 0;
    	float laserSize = entity.getLength();
    	if(laserSize <= 0)
            return;
    	
    	// Render Laser Beam:
		RenderSystem.pushMatrix();
		RenderSystem.enableAlphaTest();
		RenderSystem.color4f(1, 1, 1, entity.getLaserAlpha());
		RenderSystem.translated(x, y, z);
    	this.bindTexture(this.getLaserTexture(entity));
        
        // Rotation:
        float[] angles = entity.getBeamAngles();
		RenderSystem.rotatef(angles[1], 0, 1, 0);
		RenderSystem.rotatef(angles[3], 1, 0, 0);
    	
    	// Length:
        for(float segment = 0; segment <= laserSize - 1; ++segment) {
        	//this.laserBox.render(factor); TODO Laser
			RenderSystem.translatef(0, 0, 1);
                lastSegment = segment;
        }
        lastSegment++;
		RenderSystem.scalef((laserSize - lastSegment), 1, 1);
        //this.laserBox.render(factor); TODO Laser

		RenderSystem.popMatrix();
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    // ========== Get Texture ==========
    @Override
    public ResourceLocation getEntityTexture(BaseProjectileEntity entity) {
		return entity.getTexture();
	}

    // ========== Get Laser Texture ==========
    protected ResourceLocation getLaserTexture(LaserProjectileEntity entity) {
    	return entity.getBeamTexture();
    }

	public void bindTexture(ResourceLocation texture) {
		this.renderManager.textureManager.bindTexture(texture);
	}
}
