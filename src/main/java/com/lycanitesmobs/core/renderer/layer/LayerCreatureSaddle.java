package com.lycanitesmobs.core.renderer.layer;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.renderer.RenderCreature;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerCreatureSaddle extends LayerCreatureBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerCreatureSaddle(RenderCreature renderer) {
        super(renderer);
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public boolean canRenderLayer(EntityCreatureBase entity, float scale) {
        if(!super.canRenderLayer(entity, scale) || !(entity instanceof EntityCreatureRideable))
            return false;
        return ((EntityCreatureRideable)entity).hasSaddle();
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public ResourceLocation getLayerTexture(EntityCreatureBase entity) {
        return entity.getEquipmentTexture("saddle");
    }
}
