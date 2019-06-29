package com.lycanitesmobs.core.renderer.layer.specific;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityYale;
import com.lycanitesmobs.core.renderer.CreatureRenderer;
import com.lycanitesmobs.core.renderer.layer.LayerCreatureBase;
import net.minecraft.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.vecmath.Vector4f;

@OnlyIn(Dist.CLIENT)
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
