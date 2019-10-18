package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.EntityProjectileCustom;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderRegister {
    public ModInfo modInfo;

    public RenderRegister(ModInfo modInfo) {
        this.modInfo = modInfo;
    }

    public void registerRenderFactories() {
        // Creatures:
        for(CreatureInfo creatureInfo : CreatureManager.getInstance().creatures.values()) {
            if(creatureInfo.dummy) {
                RenderingRegistry.registerEntityRenderingHandler(creatureInfo.entityClass, new RenderFactoryNone<EntityProjectileBase>(creatureInfo.entityClass));
            }
            RenderingRegistry.registerEntityRenderingHandler(creatureInfo.entityClass, new RenderFactoryCreature<EntityCreatureBase>(creatureInfo));
        }

        // Projectiles:
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileCustom.class, new RenderFactoryProjectile<EntityProjectileCustom>());

        // Old Sprite Projectiles:
        for(String projectileName : ProjectileManager.getInstance().oldSpriteProjectiles.keySet()) {
            Class projectileClass = ProjectileManager.getInstance().oldSpriteProjectiles.get(projectileName);
            RenderingRegistry.registerEntityRenderingHandler(projectileClass, new RenderFactoryProjectile<EntityProjectileBase>(projectileName, projectileClass, false));
        }

        // Old Model Projectiles:
        for(String projectileName : ProjectileManager.getInstance().oldModelProjectiles.keySet()) {
            Class projectileClass = ProjectileManager.getInstance().oldModelProjectiles.get(projectileName);
            RenderingRegistry.registerEntityRenderingHandler(projectileClass, new RenderFactoryProjectile<EntityProjectileBase>(projectileName,projectileClass, true));
        }

        // Special Entities:
        for(Class specialClass : ObjectManager.specialEntities.values()) {
            RenderingRegistry.registerEntityRenderingHandler(specialClass, new RenderFactoryNone<EntityProjectileBase>(specialClass));
        }
    }
}
