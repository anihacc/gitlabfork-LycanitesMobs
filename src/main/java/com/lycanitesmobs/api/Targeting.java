package com.lycanitesmobs.api;

import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;

import java.util.Set;
import java.util.function.BiFunction;

/** Used when setting an attack target for interaction with other mods. **/
public class Targeting {

	private static Set<BiFunction<Entity, Entity, Boolean>> callbacks = Sets.newHashSet();

	public static void registerCallback(BiFunction<Entity, Entity, Boolean> callback) {
		callbacks.add(callback);
	}

	public static boolean isValidTarget(Entity caster, Entity target) {
		if(callbacks.isEmpty()) {
			return true;
		}

		for (BiFunction<Entity, Entity, Boolean> callback : callbacks){
			if (callback.apply(caster, target)){
				return true;
			}
		}

		return false;
	}

}
