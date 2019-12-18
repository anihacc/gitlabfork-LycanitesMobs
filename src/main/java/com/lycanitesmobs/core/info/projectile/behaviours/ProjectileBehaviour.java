package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Projectile Effects add new behaviour and other effects to projectiles such as placing blocks on impact, firing additional projectiles, etc. **/
public abstract class ProjectileBehaviour {
	public String type;

	/** Creates a Projectile Effect from the provided JSON data. **/
	public static ProjectileBehaviour createFromJSON(JsonObject json) {
		String type = json.get("type").getAsString();
		ProjectileBehaviour projectileBehaviour = null;

		if("placeBlocks".equals(type)) {
			projectileBehaviour = new ProjectileBehaviourPlaceBlocks();
		}
		else if("summon".equals(type)) {
			projectileBehaviour = new ProjectileBehaviourSummon();
		}

		if(projectileBehaviour == null) {
			return null;
		}

		projectileBehaviour.type = type;
		projectileBehaviour.loadFromJSON(json);
		return projectileBehaviour;
	}


	/**
	 * Loads this projectile effect from a JSON object.
	 * @param json The json to load from.
	 */
	public abstract void loadFromJSON(JsonObject json);


	/**
	 * Called on the projectile with this behaviour's update.
	 * @param projectile The projectile entity instance.
	 */
	public void onProjectileUpdate(BaseProjectileEntity projectile) {

	}


	/**
	 * Called when the projectile with this behaviour impacts and is destroyed.
	 * @param projectile The projectile entity instance.
	 * @param world The impact world.
	 * @param pos The impact block position.
	 */
	public void onProjectileImpact(BaseProjectileEntity projectile, World world, BlockPos pos) {

	}
}
