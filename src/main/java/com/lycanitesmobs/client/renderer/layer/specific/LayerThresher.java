package com.lycanitesmobs.client.renderer.layer.specific;

import com.lycanitesmobs.client.renderer.CreatureRenderer;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.Thresher;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerThresher extends LayerCreatureBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerThresher(CreatureRenderer renderer) {
        super(renderer);
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public boolean canRenderLayer(BaseCreatureEntity entity, float scale) {
        if(entity instanceof Thresher) {
            Thresher entityThresher = (Thresher)entity;
            return entityThresher.canWhirlpool();
        }
        return false;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public boolean canRenderPart(String partName, BaseCreatureEntity entity, boolean trophy) {
        return "effect".equals(partName);
    }

    @Override
    public Vector4f getPartColor(String partName, BaseCreatureEntity entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 0.5f);
    }

    @Override
    public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
        return entity.getSubTexture("effect");
    }
}
