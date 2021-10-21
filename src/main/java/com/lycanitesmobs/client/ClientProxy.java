package com.lycanitesmobs.client;

import com.lycanitesmobs.client.gui.beastiary.IndexBeastiaryScreen;
import com.lycanitesmobs.client.gui.beastiary.SummoningBeastiaryScreen;
import com.lycanitesmobs.core.IProxy;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.network.MessageScreenRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy implements IProxy {
	@Override
	public void registerEvents() {
		FMLJavaModLoadingContext.get().getModEventBus().register(ClientManager.getInstance());
	}

	@Override
	public Level getWorld() {
		return Minecraft.getInstance().level;
	}

	@Override
	public void addEntityToWorld(int entityId, Entity entity) {
		Minecraft.getInstance().level.putNonPlayerEntity(entityId, entity);
	}

	@Override
	public Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	@Override
	public void openScreen(int screenId, Player player) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if (playerExt == null) {
			return;
		}

		if(screenId == MessageScreenRequest.GuiRequest.BEASTIARY.id) {
			Minecraft.getInstance().setScreen(new IndexBeastiaryScreen(player));
		}
		else if(screenId == MessageScreenRequest.GuiRequest.SUMMONING.id) {
			Minecraft.getInstance().setScreen(new SummoningBeastiaryScreen(player));
		}
	}
}
