package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.model.ProjectileModel;
import com.lycanitesmobs.client.renderer.ProjectileModelRenderer;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerProjectileBase extends LayerRenderer<BaseProjectileEntity, ProjectileModel> {
    public ProjectileModelRenderer renderer;
    public String name;


    public LayerProjectileBase(ProjectileModelRenderer renderer) {
        super(renderer);
        this.renderer = renderer;
        this.name = "Layer";
    }

    @Override
    public void func_225628_a_(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int ticks, BaseProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {}

    public boolean canRenderLayer(BaseProjectileEntity entity, float scale) {
        if(entity == null)
            return false;
        return true;
    }

    public ResourceLocation getLayerTexture(BaseProjectileEntity entity) {
        return null;
    }

    /**
     * Returns if this layer can render the provided model part.
     * @param partName The name of the model part.
     * @param entity The entity to render.
     * @return True if this layer can render the part.
     */
    public boolean canRenderPart(String partName, BaseProjectileEntity entity) {
        if(this.renderer.getEntityModel() != null) {
            this.renderer.getEntityModel().canBaseRenderPart(partName, entity);
        }
        return true;
    }

    /**
     *  Returns the color that this layer should render the provided part at.
     * @param partName The name of the model part.
     * @param entity The entity to render.
     * @return The part color.
     */
    public Vector4f getPartColor(String partName, BaseProjectileEntity entity) {
        return new Vector4f(1, 1, 1, 1);
    }

    /**
     *  Returns the texture offset that this layer should render the provided part at.
     * @param partName The name of the model part.
     * @param entity The entity to render.
     * @return The part texture offset.
     */
    public Vec2f getTextureOffset(String partName, BaseProjectileEntity entity, float loop) {
        return new Vec2f(0, 0);
    }

    /**
     *  Returns the brightness that this layer should use.
     * @param partName The name of the model part.
     * @param entity The entity to render.
     * @param brightness The base brightness.
     * @return The part brightness.
     */
    public int getBrightness(String partName, BaseProjectileEntity entity, int brightness) {
        return brightness;
    }
}
