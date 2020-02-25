package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.client.renderer.RenderCreature;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class LayerShield extends LayerBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerShield(RenderCreature renderer) {
        super(renderer);
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public boolean canRenderLayer(BaseCreatureEntity entity, float scale) {
        if(!super.canRenderLayer(entity, scale))
            return false;
        return entity.isBlocking();
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public boolean canRenderPart(String partName, BaseCreatureEntity entity, boolean trophy) {
        return "shield".equals(partName);
    }

    @Override
    public Vector4f getPartColor(String partName, BaseCreatureEntity entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 1);
    }

    @Override
    public void onRenderStart(Entity entity, boolean trophy) {
        // Glow In Dark:
        int i = 15728880;
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        GlStateManager.enableCull();
    }

    @Override
    public void onRenderFinish(Entity entity, boolean trophy) {
        GlStateManager.disableCull();
    }
}
