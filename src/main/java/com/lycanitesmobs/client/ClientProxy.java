package com.lycanitesmobs.client;

import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.core.IProxy;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy implements IProxy {
	@Override
	public void registerEvents() {
		FMLJavaModLoadingContext.get().getModEventBus().register(ClientManager.getInstance());
	}
}
