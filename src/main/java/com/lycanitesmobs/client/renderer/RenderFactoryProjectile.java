package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryProjectile<T extends BaseProjectileEntity> implements IRenderFactory {
    protected String oldProjectileName;
    protected Class oldProjectileClass;
    protected boolean oldModel;

	protected boolean hasModel;

	public RenderFactoryProjectile(boolean hasModel) {
		this.hasModel = hasModel;
	}

	public RenderFactoryProjectile(String projectileName, Class projectileClass, boolean hasModel) {
		this.oldProjectileName = projectileName;
		this.oldProjectileClass = projectileClass;
		this.oldModel = hasModel;
	}

    @Override
    public EntityRenderer<? super T> createRenderFor(EntityRendererManager manager) {
		// Old Projectile Obj Models:
		if(this.oldModel) {
			try {
				return new ProjectileModelRenderer(manager, this.oldProjectileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Old Projectile Item Sprite Models:
		if(this.oldProjectileClass != null) {
			return new ProjectileSpriteRenderer(manager, this.oldProjectileClass);
		}

		// New JSON Projectile:
		if(this.hasModel) {
			try {
				return new ProjectileModelRenderer(manager);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new ProjectileSpriteRenderer(manager, CustomProjectileEntity.class);
    }

}
