package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryProjectile<T extends Entity> implements IRenderFactory {
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
    public Render createRenderFor(RenderManager manager) {
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
		if(this.hasModel) {
			try {
				return new RenderProjectileModel(manager);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new RenderProjectileSprite(manager, CustomProjectileEntity.class);
    }

}
