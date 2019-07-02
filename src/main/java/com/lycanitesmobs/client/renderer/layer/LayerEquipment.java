package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.client.renderer.CreatureRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerEquipment extends LayerCreatureBase {
    public String equipmentSlot;

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerEquipment(CreatureRenderer renderer, String equipmentSlot) {
        super(renderer);
        this.equipmentSlot = equipmentSlot;
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public boolean canRenderLayer(BaseCreatureEntity entity, float scale) {
        if(!super.canRenderLayer(entity, scale) || this.equipmentSlot == null)
            return false;
        return entity.getEquipmentName(this.equipmentSlot) != null;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
        return entity.getEquipmentTexture(entity.getEquipmentName(this.equipmentSlot));
    }
}
