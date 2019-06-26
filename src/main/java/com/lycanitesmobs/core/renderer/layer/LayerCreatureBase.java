package com.lycanitesmobs.core.renderer.layer;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.model.ModelCreatureBase;
import com.lycanitesmobs.core.renderer.RenderCreature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

@OnlyIn(Dist.CLIENT)
public class LayerCreatureBase extends LayerRenderer<BaseCreatureEntity, ModelCreatureBase> {
    public RenderCreature renderer;
    public String name;

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerCreatureBase(RenderCreature renderer) {
        super(renderer);
        this.renderer = renderer;
        this.name = "Layer";
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override //render
    public void func_212842_a_(BaseCreatureEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if(!this.canRenderLayer(entity, scale))
            return;
        if(this.renderer.getMainModel() != null) {
            ResourceLocation layerTexture = this.getLayerTexture(entity);
            if(layerTexture != null)
                this.renderer.bindTexture(layerTexture);
            this.renderer.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, this, true);
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

    public Vector2f getTextureOffset(String partName, BaseCreatureEntity entity, boolean trophy, float loop) {
        return new Vector2f(0, 0);
    }

    /** Called just before this layer is rendered. **/
    public void onRenderStart(Entity entity, boolean trophy) {

    }

	/** Called just after this layer is rendered. **/
    public void onRenderFinish(Entity entity, boolean trophy) {

    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
