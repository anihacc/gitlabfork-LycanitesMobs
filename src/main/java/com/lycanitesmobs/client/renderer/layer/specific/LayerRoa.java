package com.lycanitesmobs.client.renderer.layer.specific;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.client.renderer.CreatureRenderer;
import com.lycanitesmobs.core.entity.creature.EntityRoa;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.vecmath.Vector4f;

@OnlyIn(Dist.CLIENT)
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
    public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
        return entity.getSubTexture("effect");
    }

    @Override
    public void onRenderStart(Entity entity, boolean trophy) {}

    @Override
    public void onRenderFinish(Entity entity, boolean trophy) {}
}
