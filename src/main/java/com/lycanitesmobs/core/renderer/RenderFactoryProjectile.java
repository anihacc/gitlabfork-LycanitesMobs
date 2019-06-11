package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.core.entity.EntityProjectileCustom;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryProjectile<T extends Entity> implements IRenderFactory {
    protected String oldProjectileName;
    protected Class oldProjectileClass;
    protected boolean oldModel;

	public RenderFactoryProjectile() {

	}

	public RenderFactoryProjectile(String projectileName, Class projectileClass, boolean hasModel) {
		this.oldProjectileName = projectileName;
		this.oldProjectileClass = projectileClass;
		this.oldModel = hasModel;
	}

    @Override
    public Render createRenderFor(RenderManager manager) {

		// New JSON Projectiles
		if(this.oldProjectileClass == null) {
			return new RenderProjectile(manager, EntityProjectileCustom.class);
		}

		// Old Projectile Obj Models:
        if(this.oldModel) {
            return new RenderProjectileModel(this.oldProjectileName, manager);
        }

        // Old Projectile Item Sprite Models:
        return new RenderProjectile(manager, this.oldProjectileClass);
    }

}
