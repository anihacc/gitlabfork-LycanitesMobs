package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.client.model.ModelCustom;
import com.lycanitesmobs.client.renderer.RenderCreature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class LayerBase implements LayerRenderer<EntityCreatureBase> {
    public RenderCreature renderer;
    public String name;

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerBase(RenderCreature renderer) {
        this.renderer = renderer;
        this.name = "Layer";
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public void doRenderLayer(EntityCreatureBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if(!this.canRenderLayer(entity, scale))
            return;
        if(this.renderer.getMainModel() instanceof ModelCustom) {
            ResourceLocation layerTexture = this.getLayerTexture(entity);
            if(layerTexture != null)
                this.renderer.bindTexture(layerTexture);
            ((ModelCustom)this.renderer.getMainModel()).render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, this, true);
        }
    }

    public boolean canRenderLayer(EntityCreatureBase entity, float scale) {
        if(entity == null)
            return false;
        if(entity.isInvisible() && entity.isInvisibleToPlayer(Minecraft.getMinecraft().player))
            return false;
        return true;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    public ResourceLocation getLayerTexture(EntityCreatureBase entity) {
        return null;
    }

    public boolean canRenderPart(String partName, EntityCreatureBase entity, boolean trophy) {
        if(this.renderer.getMainModel() instanceof ModelCustom) {
            ((ModelCustom)this.renderer.getMainModel()).canBaseRenderPart(partName, entity, trophy);
        }
        return true;
    }

    public Vector4f getPartColor(String partName, EntityCreatureBase entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 1);
    }

    public Vector2f getTextureOffset(String partName, EntityCreatureBase entity, boolean trophy, float loop) {
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
