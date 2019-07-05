package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderRegister {

    public void registerModelLoaders() {
        ModelLoaderRegistry.registerLoader(new ModelLoader());
    }

    public void registerRenderFactories() {

        // Creatures:
        for(CreatureInfo creatureInfo : CreatureManager.getInstance().creatures.values()) {
            if(creatureInfo.dummy) {
                RenderingRegistry.registerEntityRenderingHandler(creatureInfo.entityClass, new RenderFactoryNone<BaseProjectileEntity>(creatureInfo.entityClass));
            }
            RenderingRegistry.registerEntityRenderingHandler(creatureInfo.entityClass, new RenderFactoryCreature<BaseCreatureEntity>(creatureInfo));
        }

        // Projectiles:
        RenderingRegistry.registerEntityRenderingHandler(CustomProjectileEntity.class, new RenderFactoryProjectile<CustomProjectileEntity>());

        // Old Sprite Projectiles:
        for(String projectileName : ProjectileManager.getInstance().oldSpriteProjectiles.keySet()) {
            Class projectileClass = ProjectileManager.getInstance().oldSpriteProjectiles.get(projectileName);
            RenderingRegistry.registerEntityRenderingHandler(projectileClass, new RenderFactoryProjectile<BaseProjectileEntity>(projectileName, projectileClass, false));
        }

        // Old Model Projectiles:
        for(String projectileName : ProjectileManager.getInstance().oldModelProjectiles.keySet()) {
            Class projectileClass = ProjectileManager.getInstance().oldModelProjectiles.get(projectileName);
            RenderingRegistry.registerEntityRenderingHandler(projectileClass, new RenderFactoryProjectile<BaseProjectileEntity>(projectileName, projectileClass, true));
        }

        // Special Entities:
        for(Class specialClass : ObjectManager.specialEntities.values()) {
            RenderingRegistry.registerEntityRenderingHandler(specialClass, new RenderFactoryNone<BaseProjectileEntity>(specialClass));
        }
    }
}