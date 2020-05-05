package com.lycanitesmobs.client.renderer.layer.specific;

import com.lycanitesmobs.client.renderer.CreatureRenderer;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityRoa;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.Identifier;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class LayerRoa extends LayerCreatureBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerRoa(CreatureRenderer renderer) {
        super(renderer);
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public boolean canRenderLayer(BaseCreatureEntity entity, float scale) {
        if(entity instanceof EntityRoa) {
            EntityRoa entityRoa = (EntityRoa)entity;
            return entityRoa.canWhirlpool();
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
    public Identifier getLayerTexture(BaseCreatureEntity entity) {
        return entity.getSubTexture("effect");
    }
}
