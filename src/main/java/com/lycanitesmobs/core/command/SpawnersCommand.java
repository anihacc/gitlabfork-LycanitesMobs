package com.lycanitesmobs.core.command;

import com.lycanitesmobs.core.spawner.Spawner;
import com.lycanitesmobs.core.spawner.SpawnerEventListener;
import com.lycanitesmobs.core.spawner.SpawnerManager;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SpawnersCommand {
	public static ArgumentBuilder<CommandSource, ?> register() {
		return Commands.literal("spawners")
				.then(Commands.literal("reload").executes(SpawnersCommand::reload))
				.then(Commands.literal("creative")
						.then(Commands.literal("enable").executes(SpawnersCommand::creativeEnable))
						.then(Commands.literal("disable").executes(SpawnersCommand::creativeDisable)))
				.then(Commands.literal("list").executes(SpawnersCommand::list));
	}

	public static int reload(final CommandContext<CommandSource> context) {
		SpawnerManager.getInstance().reload();
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.spawners.reload"), true);
		return 0;
	}

	public static int creativeEnable(final CommandContext<CommandSource> context) {
		SpawnerEventListener.testOnCreative = true;
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.spawners.creative.enable"), true);
		return 0;
	}

	public static int creativeDisable(final CommandContext<CommandSource> context) {
		SpawnerEventListener.testOnCreative = false;
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.spawners.creative.disable"), true);
		return 0;
	}

	public static int list(final CommandContext<CommandSource> context) {
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.mobevents.list"), true);
		for(Spawner spawner : SpawnerManager.getInstance().spawners.values()) {
			String eventName = spawner.name;
			context.getSource().sendFeedback(new StringTextComponent(eventName), true);
		}
		return 0;
	}
}
