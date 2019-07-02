package com.lycanitesmobs.core.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderNone extends EntityRenderer<Entity> {
    
    // ==================================================
    //                     Constructor
    // ==================================================
    public RenderNone(EntityRendererManager renderManager) {
    	super(renderManager);
    }
    
    
    // ==================================================
    //                     Do Render
    // ==================================================
    @Override
    public void func_76986_a(Entity entity, double par2, double par4, double par6, float par8, float par9) {
    	return;
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    // ========== Get Texture ==========
    @Override
    protected ResourceLocation func_110775_a(Entity entity) {
    	return null;
    }
}
