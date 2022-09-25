package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class ProjectileRenderFactory implements EntityRendererProvider<BaseProjectileEntity>
{
	protected ProjectileInfo projectileInfo;

    protected String oldProjectileName;
    protected Class oldProjectileClass;
    protected boolean oldModel;


	public ProjectileRenderFactory(ProjectileInfo projectileInfo) {
		this.projectileInfo = projectileInfo;
	}

	public ProjectileRenderFactory(String projectileName, Class projectileClass, boolean hasModel) {
		this.oldProjectileName = projectileName;
		this.oldProjectileClass = projectileClass;
		this.oldModel = hasModel;
	}

    @Override
    public EntityRenderer<BaseProjectileEntity> create(Context context) {
		// Old Projectile Obj Models:
		if(this.oldModel) {
			try {
				return new ProjectileModelRenderer(context, this.oldProjectileName);
			}
			catch(Exception e) {
				LycanitesMobs.logWarning("", "An exception occurred when creating an old projectile renderer:");
				e.printStackTrace();
			}
		}

		// Old Projectile Item Sprite Models:
		if(this.oldProjectileClass != null) {
			return new ProjectileSpriteRenderer(context, this.oldProjectileClass);
		}

		// New JSON Projectile:
		if(this.projectileInfo != null && this.projectileInfo.modelClassName != null) {
			try {
				return new ProjectileModelRenderer(context, this.projectileInfo);
			}
			catch(Exception e) {
				LycanitesMobs.logWarning("", "An exception occurred when creating a projectile renderer:");
				e.printStackTrace();
			}
		}
		return new ProjectileSpriteRenderer(context, CustomProjectileEntity.class);
    }

}
