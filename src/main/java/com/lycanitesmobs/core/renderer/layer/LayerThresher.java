package com.lycanitesmobs.core.renderer.layer;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.core.entity.creature.EntityThresher;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector4f;

@OnlyIn(Dist.CLIENT)
public class LayerThresher extends LayerBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerThresher(RenderCreature renderer) {
        super(renderer);
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public boolean canRenderLayer(EntityCreatureBase entity, float scale) {
        if(entity instanceof EntityThresher) {
            EntityThresher entityThresher = (EntityThresher)entity;
            return entityThresher.canWhirlpool();
        }
        return false;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public boolean canRenderPart(String partName, EntityCreatureBase entity, boolean trophy) {
        return "effect".equals(partName);
    }

    @Override
    public Vector4f getPartColor(String partName, EntityCreatureBase entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 0.5f);
    }

    @Override
    public ResourceLocation getLayerTexture(EntityCreatureBase entity) {
        return entity.getSubTexture("effect");
    }

    @Override
    public void onRenderStart(Entity entity, boolean trophy) {}

    @Override
    public void onRenderFinish(Entity entity, boolean trophy) {}
}