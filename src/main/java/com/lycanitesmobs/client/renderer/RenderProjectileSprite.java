package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.entity.LaserProjectileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderProjectileSprite extends EntityRenderer<BaseProjectileEntity> {
    private int renderTime = 0;
    Class projectileClass;
    
    // Laser Box:
    protected Model laserModel = new Model() {};
    private RendererModel laserBox;
    
    // ==================================================
    //                     Constructor
    // ==================================================
    public RenderProjectileSprite(EntityRendererManager renderManager, Class projectileClass) {
    	super(renderManager);
        this.projectileClass = projectileClass;
    }
    
    
    // ==================================================
    //                     Do Render
    // ==================================================
    @Override
	public void func_76986_a(BaseProjectileEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
    	if(this.renderTime++ > Integer.MAX_VALUE - 1)
            this.renderTime = 0;
        this.renderProjectile(entity, x, y, z, entityYaw, partialTicks);
    	if(entity instanceof LaserProjectileEntity)
    		this.renderLaser((LaserProjectileEntity)entity, x, y, z, entityYaw, partialTicks);
    }
    
    
    // ==================================================
    //                 Render Projectile
    // ==================================================
    public void renderProjectile(BaseProjectileEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
    	double scale = 0.5d;
    	if(entity instanceof CustomProjectileEntity && ((CustomProjectileEntity)entity).projectileInfo == null) {
    		return;
		}
        if(entity != null) {
            scale *= entity.getProjectileScale();
            y += entity.getTextureOffsetY();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scaled(scale, scale, scale);

        this.bindTexture(this.func_110775_a(entity));
        this.renderTexture(Tessellator.getInstance(), entity);

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }


    // ==================================================
    //                  Render Texture
    // ==================================================
    private void renderTexture(Tessellator tessellator, BaseProjectileEntity entity) {
        double minU = 0;
        double maxU = 1;
        double minV = 0;
        double maxV = 1;
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

        //GlStateManager.rotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        //GlStateManager.rotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
        //GlStateManager.translatef(-scale / 2, -scale / 2, -scale / 2);

        if(this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
        }

        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

        vertexbuffer.pos(-textureWidth, -textureHeight + (textureHeight / 2) + offsetY, 0.0D)
                .tex(minU, maxV)
                .normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(textureWidth, -textureHeight + (textureHeight / 2) + offsetY, 0.0D)
                .tex(maxU, maxV)
                .normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(textureWidth, textureHeight + (textureHeight / 2) + offsetY, 0.0D)
                .tex(maxU, minV)
                .normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(-textureWidth, textureHeight + (textureHeight / 2) + offsetY, 0.0D)
                .tex(minU, minV)
                .normal(0.0F, 1.0F, 0.0F).endVertex();

        tessellator.draw();
    }
    
    
    // ==================================================
    //                 Render Laser
    // ==================================================
    public void renderLaser(LaserProjectileEntity entity, double x, double y, double z, float par8, float par9) {
    	float scale = entity.getLaserWidth();
    	
    	// Create Laser Model If Null:
    	if(this.laserBox == null) {
			this.laserBox = new RendererModel(laserModel, 0, 0);
			this.laserBox.addBox(-(scale / 2), -(scale / 2), 0, (int)scale, (int)scale, 16);
			this.laserBox.rotationPointX = 0;
			this.laserBox.rotationPointY = 0;
			this.laserBox.rotationPointZ = 0;
    	}
        
    	float factor = (float)(1.0 / 16.0);
    	float lastSegment = 0;
    	float laserSize = entity.getLength();
    	if(laserSize <= 0)
            return;
    	
    	// Render Laser Beam:
        GlStateManager.pushMatrix();
        GlStateManager.enableAlphaTest();
        GlStateManager.color4f(1, 1, 1, entity.getLaserAlpha());
        GlStateManager.translated(x, y, z);
    	this.bindTexture(this.getLaserTexture(entity));
        
        // Rotation:
        float[] angles = entity.getBeamAngles();
        GlStateManager.rotatef(angles[1], 0, 1, 0);
        GlStateManager.rotatef(angles[3], 1, 0, 0);
    	
    	// Length:
        for(float segment = 0; segment <= laserSize - 1; ++segment) {
                this.laserBox.render(factor);
                GlStateManager.translatef(0, 0, 1);
                lastSegment = segment;
        }
        lastSegment++;
        GlStateManager.scalef((laserSize - lastSegment), 1, 1);
        this.laserBox.render(factor);

        GlStateManager.popMatrix();
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    // ========== Get Texture ==========
    @Override
    protected ResourceLocation func_110775_a(BaseProjectileEntity entity) {
		return entity.getTexture();
	}

    // ========== Get Laser Texture ==========
    protected ResourceLocation getLaserTexture(LaserProjectileEntity entity) {
    	return entity.getBeamTexture();
    }
}
