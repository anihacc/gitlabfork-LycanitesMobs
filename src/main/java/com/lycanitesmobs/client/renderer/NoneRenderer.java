package com.lycanitesmobs.client.renderer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class NoneRenderer extends EntityRenderer<Entity> {
    
    // ==================================================
    //                     Constructor
    // ==================================================
    public NoneRenderer(EntityRendererManager renderManager) {
    	super(renderManager);
    }
    
    
    // ==================================================
    //                     Do Render
    // ==================================================
    @Override
	public void render(Entity entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_) {
    	return;
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    // ========== Get Texture ==========
    @Override
	public Identifier getEntityTexture(Entity entity) {
    	return null;
    }
}
