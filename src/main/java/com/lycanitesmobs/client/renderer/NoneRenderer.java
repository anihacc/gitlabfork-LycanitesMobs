package com.lycanitesmobs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoneRenderer extends EntityRenderer<Entity> {
    
    // ==================================================
    //                     Constructor
    // ==================================================
    public NoneRenderer(EntityRenderDispatcher renderManager) {
    	super(renderManager);
    }
    
    
    // ==================================================
    //                     Do Render
    // ==================================================
    @Override
	public void render(Entity entity, float p_225623_2_, float p_225623_3_, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int p_225623_6_) {
    	return;
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    // ========== Get Texture ==========
    @Override
	public ResourceLocation getTextureLocation(Entity entity) {
    	return null;
    }
}
