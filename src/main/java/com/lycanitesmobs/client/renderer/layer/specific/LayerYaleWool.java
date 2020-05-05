package com.lycanitesmobs.client.renderer.layer.specific;

import com.lycanitesmobs.client.renderer.CreatureRenderer;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityYale;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.item.DyeColor;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class LayerYaleWool extends LayerCreatureBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerYaleWool(CreatureRenderer renderer) {
        super(renderer);
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public boolean canRenderLayer(BaseCreatureEntity entity, float scale) {
        if(!super.canRenderLayer(entity, scale))
            return false;
        if(!(entity instanceof EntityYale))
            return false;
        return ((EntityYale)entity).hasFur();
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public boolean canRenderPart(String partName, BaseCreatureEntity entity, boolean trophy) {
        return "fur".equals(partName);
    }

    @Override
    public Vector4f getPartColor(String partName, BaseCreatureEntity entity, boolean trophy) {
        DyeColor color = entity.getColor();
        return new Vector4f(color.getColorComponentValues()[0], color.getColorComponentValues()[1], color.getColorComponentValues()[2], 1.0F);
    }
}
