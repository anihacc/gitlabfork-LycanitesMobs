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

	public RenderFactoryProjectile() {}

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
				return new RenderProjectileModel(manager, this.oldProjectileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Old Projectile Item Sprite Models:
		if(this.oldProjectileClass != null) {
			return new RenderProjectileSprite(manager, this.oldProjectileClass);
		}

		// New JSON Projectile:
		return new RenderProjectileSprite(manager, CustomProjectileEntity.class);
    }

}
