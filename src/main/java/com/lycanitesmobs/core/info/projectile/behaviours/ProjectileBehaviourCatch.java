package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
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
	public boolean canDamage(BaseProjectileEntity projectile, World world, EntityLivingBase target, boolean canDamage) {
		if(projectile.getEntityWorld().isRemote || target == projectile.getThrower()) {
			return canDamage;
		}
		ResourceLocation entityResourceLocation = EntityList.getKey(target);
		if(entityResourceLocation == null) {
			return canDamage;
		}
		String entityId = entityResourceLocation.toString();
		if(!this.catchEntityIds.contains(entityId)) {
			return canDamage;
		}

		if(this.resetAttackCooldown && target instanceof BaseCreatureEntity) {
			((BaseCreatureEntity)target).resetAttackCooldown();
		}
		projectile.setDead();
		return !this.preventDamage;
	}
}
