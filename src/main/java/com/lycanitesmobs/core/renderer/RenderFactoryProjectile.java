package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.EntityProjectileCustom;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryProjectile<T extends EntityProjectileBase> implements IRenderFactory {
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
    public EntityRenderer<? super T> createRenderFor(EntityRendererManager manager) {

		// New JSON Projectiles
		if(this.oldProjectileClass == null) {
			return new RenderProjectile(manager, EntityProjectileCustom.class);
		}

		// Old Projectile Obj Models:
        if(this.oldModel) {
			try {
				return new RenderProjectileModel(this.oldProjectileName, manager);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

        // Old Projectile Item Sprite Models:
        return new RenderProjectile(manager, this.oldProjectileClass);
    }

}
