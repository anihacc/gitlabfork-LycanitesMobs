package com.lycanitesmobs.core.command;

import com.lycanitesmobs.core.spawner.Spawner;
import com.lycanitesmobs.core.spawner.SpawnerEventListener;
import com.lycanitesmobs.core.spawner.SpawnerManager;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class SpawnersCommand {
	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("spawners")
				.then(Commands.literal("reload").executes(SpawnersCommand::reload))
				.then(Commands.literal("creative")
						.then(Commands.literal("enable").executes(SpawnersCommand::creativeEnable))
						.then(Commands.literal("disable").executes(SpawnersCommand::creativeDisable)))
				.then(Commands.literal("list").executes(SpawnersCommand::list));
	}

	public static int reload(final CommandContext<CommandSourceStack> context) {
		if (!context.getSource().hasPermission(2)) {
			return 0;
		}
		SpawnerManager.getInstance().reload();
		context.getSource().sendSuccess(new TranslatableComponent("lyc.command.spawners.reload"), true);
		return 0;
	}

	public static int creativeEnable(final CommandContext<CommandSourceStack> context) {
		if (!context.getSource().hasPermission(2)) {
			return 0;
		}
		SpawnerEventListener.testOnCreative = true;
		context.getSource().sendSuccess(new TranslatableComponent("lyc.command.spawners.creative.enable"), true);
		return 0;
	}

	public static int creativeDisable(final CommandContext<CommandSourceStack> context) {
		if (!context.getSource().hasPermission(2)) {
			return 0;
		}
		SpawnerEventListener.testOnCreative = false;
		context.getSource().sendSuccess(new TranslatableComponent("lyc.command.spawners.creative.disable"), true);
		return 0;
	}

	public static int list(final CommandContext<CommandSourceStack> context) {
		if (!context.getSource().hasPermission(2)) {
			return 0;
		}
		context.getSource().sendSuccess(new TranslatableComponent("lyc.command.spawners.list"), true);
		for(Spawner spawner : SpawnerManager.getInstance().spawners.values()) {
			if(!"".equals(spawner.eventName)) {
				continue;
			}
			String spawnerName = spawner.name;
			context.getSource().sendSuccess(new TextComponent(spawnerName), true);
		}
		return 0;
	}
}
