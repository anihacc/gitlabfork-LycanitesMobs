package com.lycanitesmobs.client;

import com.lycanitesmobs.core.IProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy implements IProxy {
	@Override
	public void registerEvents() {
		FMLJavaModLoadingContext.get().getModEventBus().register(ClientManager.getInstance());
	}

	@Override
	public World getWorld() {
		return Minecraft.getInstance().level;
	}

	@Override
	public void addEntityToWorld(int entityId, Entity entity) {
		Minecraft.getInstance().level.putNonPlayerEntity(entityId, entity);
	}

	@Override
	public PlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}
}
