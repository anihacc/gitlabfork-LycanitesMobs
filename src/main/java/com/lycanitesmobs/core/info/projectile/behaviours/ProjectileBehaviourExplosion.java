package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.util.math.BlockPos;
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

		if(!world.getGameRules().getBoolean("mobGriefing")) {
			return;
		}

		int explosionRadius = this.radius;
		if (projectile.getThrower() != null && projectile.getThrower() instanceof BaseCreatureEntity) {
			BaseCreatureEntity baseCreatureEntity = (BaseCreatureEntity)projectile.getThrower();
			if (baseCreatureEntity.isRareVariant()) {
				explosionRadius += 2;
			}
		}

		boolean breakBlocks = false;
		boolean destroyBlocks = false;
		if("break".equalsIgnoreCase(this.mode)) {
			breakBlocks = true;
		}
		else if("destroy".equalsIgnoreCase(this.mode)) {
			breakBlocks = true;
			destroyBlocks = true;
		}

		world.newExplosion(projectile, projectile.getPositionVector().x, projectile.getPositionVector().y, projectile.getPositionVector().z, explosionRadius, destroyBlocks, breakBlocks);
	}
}
