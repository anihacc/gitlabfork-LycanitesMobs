package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
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
