package com.lycanitesmobs.core.renderer.layer;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.model.ModelProjectileBase;
import com.lycanitesmobs.core.renderer.RenderProjectileModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

@OnlyIn(Dist.CLIENT)
public class LayerProjectileBase extends LayerRenderer<EntityProjectileBase, ModelProjectileBase> {
    public RenderProjectileModel renderer;
    public String name;

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerProjectileBase(RenderProjectileModel renderer) {
        super(renderer);
        this.renderer = renderer;
        this.name = "Layer";
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public void render(EntityProjectileBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if(!this.canRenderLayer(entity, scale))
            return;
        if(this.renderer.getMainModel() != null) {
            ResourceLocation layerTexture = this.getLayerTexture(entity);
            if(layerTexture != null)
                this.renderer.bindTexture(layerTexture);
            this.renderer.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, this, true);
        }
    }

    public boolean canRenderLayer(EntityProjectileBase entity, float scale) {
        if(entity == null)
            return false;
        if(entity.isInvisible() && entity.isInvisibleToPlayer(Minecraft.getInstance().player))
            return false;
        return true;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    public ResourceLocation getLayerTexture(EntityProjectileBase entity) {
        return null;
    }

    public boolean canRenderPart(String partName, EntityProjectileBase entity, boolean trophy) {
        if(this.renderer.getMainModel() != null) {
            this.renderer.getMainModel().canBaseRenderPart(partName, entity, trophy);
        }
        return true;
    }

    public Vector4f getPartColor(String partName, EntityProjectileBase entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 1);
    }

    public Vector2f getTextureOffset(String partName, EntityProjectileBase entity, boolean trophy, float loop) {
        return new Vector2f(0, 0);
    }

    /** Called just before this layer is rendered. **/
    public void onRenderStart(Entity entity) {

    }

	/** Called just after this layer is rendered. **/
    public void onRenderFinish(Entity entity) {

    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
