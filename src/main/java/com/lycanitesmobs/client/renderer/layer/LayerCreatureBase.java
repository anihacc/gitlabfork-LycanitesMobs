package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.model.ModelCreatureBase;
import com.lycanitesmobs.client.renderer.CreatureRenderer;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerCreatureBase extends LayerRenderer<BaseCreatureEntity, ModelCreatureBase> {
    public CreatureRenderer renderer;
    public String name;

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerCreatureBase(CreatureRenderer renderer) {
        super(renderer);
        this.renderer = renderer;
        this.name = "Layer";
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public void func_225628_a_(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int ticks, BaseCreatureEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if(!this.canRenderLayer(entity, scale))
            return;
        if(this.renderer.getMainModel() != null) {
            ResourceLocation layerTexture = this.getLayerTexture(entity);
            if(layerTexture != null)
                this.renderer.bindTexture(layerTexture);
            this.renderer.getMainModel().animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, this, true);
        }
    }

    public boolean canRenderLayer(BaseCreatureEntity entity, float scale) {
        if(entity == null)
            return false;
        if(entity.isInvisible() && entity.isInvisibleToPlayer(Minecraft.getInstance().player))
            return false;
        return true;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
        return null;
    }

    public boolean canRenderPart(String partName, BaseCreatureEntity entity, boolean trophy) {
        if(this.renderer.getMainModel() != null) {
            this.renderer.getMainModel().canBaseRenderPart(partName, entity, trophy);
        }
        return true;
    }

    public Vector4f getPartColor(String partName, BaseCreatureEntity entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 1);
    }

    public Vec2f getTextureOffset(String partName, BaseCreatureEntity entity, boolean trophy, float loop) {
        return new Vec2f(0, 0);
    }

    /** Called just before this layer is rendered. **/
    public void onRenderStart(Entity entity, boolean trophy) {

    }

	/** Called just after this layer is rendered. **/
    public void onRenderFinish(Entity entity, boolean trophy) {

    }

    public boolean shouldCombineTextures() {
        return true;
    }
}
