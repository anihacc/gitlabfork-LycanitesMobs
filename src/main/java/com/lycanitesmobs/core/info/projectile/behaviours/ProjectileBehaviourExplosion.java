package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
		if (projectile.getThrower() != null && projectile.getThrower() instanceof BaseCreatureEntity) {
			BaseCreatureEntity baseCreatureEntity = (BaseCreatureEntity)projectile.getThrower();
			if (baseCreatureEntity.isRareSubspecies()) {
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
}
