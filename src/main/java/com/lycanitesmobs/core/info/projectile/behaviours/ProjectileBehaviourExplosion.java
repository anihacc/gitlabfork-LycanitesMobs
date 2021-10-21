package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

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
	public void onProjectileImpact(BaseProjectileEntity projectile, Level world, BlockPos pos) {
		if(this.radius <= 0 || projectile.getCommandSenderWorld().isClientSide) {
			return;
		}

		if(!world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
			return;
		}

		int explosionRadius = this.radius;
		if (projectile.getOwner() != null && projectile.getOwner() instanceof BaseCreatureEntity) {
			BaseCreatureEntity baseCreatureEntity = (BaseCreatureEntity)projectile.getOwner();
			if (baseCreatureEntity.isRareVariant()) {
				explosionRadius += 2;
			}
		}

		Explosion.BlockInteraction explosionMode = Explosion.BlockInteraction.NONE;
		if("break".equalsIgnoreCase(this.mode)) {
			explosionMode = Explosion.BlockInteraction.BREAK;
		}
		else if("destroy".equalsIgnoreCase(this.mode)) {
			explosionMode = Explosion.BlockInteraction.DESTROY;
		}

		world.explode(projectile, projectile.position().x(), projectile.position().y(), projectile.position().z(), explosionRadius, explosionMode);
	}
}
