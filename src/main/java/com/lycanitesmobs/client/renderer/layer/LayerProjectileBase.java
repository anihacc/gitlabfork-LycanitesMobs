package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.model.ModelProjectileBase;
import com.lycanitesmobs.client.renderer.RenderProjectileModel;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerProjectileBase extends LayerRenderer<BaseProjectileEntity, ModelProjectileBase> {
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
    @Override //render
    public void render(BaseProjectileEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if(!this.canRenderLayer(entity, scale))
            return;
        if(this.renderer.getMainModel() != null) {
            ResourceLocation layerTexture = this.getLayerTexture(entity);
            if(layerTexture != null)
                this.renderer.bindTexture(layerTexture);
            this.renderer.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, this, true);
        }
    }

    public boolean canRenderLayer(BaseProjectileEntity entity, float scale) {
        if(entity == null)
            return false;
        if(entity.isInvisible() && entity.isInvisibleToPlayer(Minecraft.getInstance().player))
            return false;
        return true;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    public ResourceLocation getLayerTexture(BaseProjectileEntity entity) {
        return null;
    }

    public boolean canRenderPart(String partName, BaseProjectileEntity entity, boolean trophy) {
        if(this.renderer.getMainModel() != null) {
            this.renderer.getMainModel().canBaseRenderPart(partName, entity, trophy);
        }
        return true;
    }

    public Vector4f getPartColor(String partName, BaseProjectileEntity entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 1);
    }

    public Vec2f getTextureOffset(String partName, BaseProjectileEntity entity, boolean trophy, float loop) {
        return new Vec2f(0, 0);
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
