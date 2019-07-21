package com.lycanitesmobs.core;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public interface IProxy {
	void registerEvents();

	World getWorld();

	void addEntityToWorld(int entityId, Entity entity);
}
