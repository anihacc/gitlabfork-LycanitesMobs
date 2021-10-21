package com.lycanitesmobs.client.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryNone<T extends Entity> implements IRenderFactory {
    protected Class entityClass;

    public RenderFactoryNone(Class entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public EntityRenderer<? super T> createRenderFor(EntityRenderDispatcher manager) {
        return new NoneRenderer(manager);
    }

}
