package com.lycanitesmobs.core.command;

import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommandManager {
	public static CommandManager INSTANCE;

	public static CommandManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new CommandManager();
		}
		return INSTANCE;
	}

	@SubscribeEvent
	public void register(final RegisterCommandsEvent event) {
		event.getDispatcher().register(
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
