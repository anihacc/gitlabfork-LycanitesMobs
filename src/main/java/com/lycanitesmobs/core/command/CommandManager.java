package com.lycanitesmobs.core.command;

import net.minecraft.command.Commands;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class CommandManager {
	public static CommandManager INSTANCE;

	public static CommandManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new CommandManager();
		}
		return INSTANCE;
	}

	@SubscribeEvent
	public void register(final FMLServerStartingEvent event) {
		event.getCommandDispatcher().register(
				Commands.literal("lm")
						.then(CreaturesCommand.register())
						.then(BeastiaryCommand.register())
						.then(SpawnersCommand.register())
						.then(SpawnerCommand.register())
						.then(MobEventsCommand.register())
						.then(MobEventCommand.register())
						.then(EquipmentCommand.register())
						.then(DungeonsCommand.register())
						.then(DebugCommand.register())
		);
	}
}
