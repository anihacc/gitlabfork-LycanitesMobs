package com.lycanitesmobs.client;

import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.core.IProxy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy implements IProxy {
	@Override
	public void registerEvents() {
		FMLJavaModLoadingContext.get().getModEventBus().register(ClientManager.getInstance());
	}

	@Override
	public World getWorld() {
		return MinecraftClient.getInstance().world;
	}

	@Override
	public void addEntityToWorld(int entityId, Entity entity) {
		MinecraftClient.getInstance().world.addEntity(entityId, entity);
	}
}
