package com.lycanitesmobs.core;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ServerProxy implements IProxy {
	@Override
	public void registerEvents() {

	}

	@Override
	public World getWorld() {
		return null;
	}

	@Override
	public void addEntityToWorld(int entityId, Entity entity) {

	}
}