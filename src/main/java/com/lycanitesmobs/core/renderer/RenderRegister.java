package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.EntityProjectileCustom;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.core.item.equipment.EquipmentPartManager;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.model.EquipmentPartModelLoader;
import com.lycanitesmobs.core.model.ModelEquipmentPart;
import com.lycanitesmobs.core.model.projectile.ModelAetherwave;
import com.lycanitesmobs.core.model.projectile.ModelChaosOrb;
import com.lycanitesmobs.core.model.projectile.ModelCrystalShard;
import com.lycanitesmobs.core.model.projectile.ModelLightBall;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderRegister {

    public void registerModels() {
        // Old Model Projectiles:
        AssetManager.registerOldProjectileModel("lightball", ModelLightBall.class);
        AssetManager.registerOldProjectileModel("crystalshard", ModelCrystalShard.class);
        AssetManager.registerOldProjectileModel("aetherwave", ModelAetherwave.class);
        AssetManager.registerOldProjectileModel("chaosorb", ModelChaosOrb.class);
    }

    public void registerModelLoaders() {
        ModelLoaderRegistry.registerLoader(new EquipmentPartModelLoader());
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
            RenderingRegistry.registerEntityRenderingHandler(projectileClass, new RenderFactoryProjectile<EntityProjectileBase>(projectileName, projectileClass, true));
        }

        // Special Entities:
        for(Class specialClass : ObjectManager.specialEntities.values()) {
            RenderingRegistry.registerEntityRenderingHandler(specialClass, new RenderFactoryNone<EntityProjectileBase>(specialClass));
        }
    }
}
