package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ProjectileBehaviourCatch extends ProjectileBehaviour {
	public List<String> catchEntityIds = new ArrayList<>();
	public boolean resetAttackCooldown = true;
	public boolean preventDamage = true;

	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("catchEntityIds")) {
			this.catchEntityIds = JSONHelper.getJsonStrings(json.get("catchEntityIds").getAsJsonArray());
		}
		if(json.has("resetAttackCooldown")) {
			this.resetAttackCooldown = json.get("resetAttackCooldown").getAsBoolean();
		}
		if(json.has("preventDamage")) {
			this.preventDamage = json.get("preventDamage").getAsBoolean();
		}
	}

	@Override
	public boolean canDamage(BaseProjectileEntity projectile, World world, LivingEntity target, boolean canDamage) {
		if(projectile.getEntityWorld().isRemote || target == projectile.getThrower()) {
			return canDamage;
		}
		Identifier entityIdentifier = target.getType().getRegistryName();
		if(entityIdentifier == null) {
			return canDamage;
		}
		String entityId = entityIdentifier.toString();
		if(!this.catchEntityIds.contains(entityId)) {
			return canDamage;
		}

		if(this.resetAttackCooldown && target instanceof BaseCreatureEntity) {
			((BaseCreatureEntity)target).resetAttackCooldown();
		}
		projectile.remove();
		return !this.preventDamage;
	}
}
