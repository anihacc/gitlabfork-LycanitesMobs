package com.lycanitesmobs.core.renderer.layer;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.vecmath.Vector4f;

@OnlyIn(Dist.CLIENT)
public class LayerShield extends LayerCreatureBase {

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
    public boolean canRenderLayer(EntityCreatureBase entity, float scale) {
        if(!super.canRenderLayer(entity, scale))
            return false;
        return entity.isBlocking();
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public boolean canRenderPart(String partName, EntityCreatureBase entity, boolean trophy) {
        return "shield".equals(partName);
    }

    @Override
    public Vector4f getPartColor(String partName, EntityCreatureBase entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 1);
    }

    @Override
    public void onRenderStart(Entity entity, boolean trophy) {
        // Glow In Dark:
        int i = 15728880;
        int j = i % 65536;
        int k = i / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) j, (float) k);

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        GlStateManager.enableCull();
    }

    @Override
    public void onRenderFinish(Entity entity, boolean trophy) {
        GlStateManager.disableCull();
    }
}
