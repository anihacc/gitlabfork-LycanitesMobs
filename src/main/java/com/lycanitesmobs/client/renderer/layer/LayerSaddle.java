package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.client.renderer.RenderCreature;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerSaddle extends LayerBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerSaddle(RenderCreature renderer) {
        super(renderer);
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public boolean canRenderLayer(BaseCreatureEntity entity, float scale) {
        if(!super.canRenderLayer(entity, scale) || !(entity instanceof RideableCreatureEntity))
            return false;
        return ((RideableCreatureEntity)entity).hasSaddle();
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
        return entity.getEquipmentTexture("saddle");
    }
}
