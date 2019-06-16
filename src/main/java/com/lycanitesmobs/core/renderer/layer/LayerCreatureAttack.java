package com.lycanitesmobs.core.renderer.layer;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.core.renderer.layer.LayerCreatureBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerCreatureAttack extends LayerCreatureBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerCreatureAttack(RenderCreature renderer) {
        super(renderer);
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public boolean canRenderLayer(EntityCreatureBase entity, float scale) {
        if(!super.canRenderLayer(entity, scale))
            return false;
        return entity.isAttackOnCooldown();
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public ResourceLocation getLayerTexture(EntityCreatureBase entity) {
        return entity.getSubTexture("fire");
    }
}
