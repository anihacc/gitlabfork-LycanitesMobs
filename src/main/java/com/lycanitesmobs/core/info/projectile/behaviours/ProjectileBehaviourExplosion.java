package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class ProjectileBehaviourExplosion extends ProjectileBehaviour {
	/** The explosion radius. **/
	public int radius = 2;

	/** The explosion mode can be: none, break or destroy. **/
	public String mode = "none";

	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("radius"))
			this.radius = json.get("radius").getAsInt();

		if(json.has("mode"))
			this.mode = json.get("mode").getAsString();
	}

	@Override
	public void onProjectileImpact(BaseProjectileEntity projectile, World world, BlockPos pos) {
		if(this.radius <= 0 || projectile.getEntityWorld().isRemote) {
			return;
		}

		if(!world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
			return;
		}

		int explosionRadius = this.radius;
		if (projectile.func_234616_v_() != null && projectile.func_234616_v_() instanceof BaseCreatureEntity) {
			BaseCreatureEntity baseCreatureEntity = (BaseCreatureEntity)projectile.func_234616_v_();
			if (baseCreatureEntity.isRareVariant()) {
				explosionRadius += 2;
			}
		}

		Explosion.Mode explosionMode = Explosion.Mode.NONE;
		if("break".equalsIgnoreCase(this.mode)) {
			explosionMode = Explosion.Mode.BREAK;
		}
		else if("destroy".equalsIgnoreCase(this.mode)) {
			explosionMode = Explosion.Mode.DESTROY;
		}

		world.createExplosion(projectile, projectile.getPositionVec().getX(), projectile.getPositionVec().getY(), projectile.getPositionVec().getZ(), explosionRadius, explosionMode);
	}

	@Override
	protected LivingEntity getShooter() {
		return null;
	}
}
