package com.lycanitesmobs.core.command;

import com.lycanitesmobs.core.config.ConfigDungeons;
import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class DungeonsCommand {
	public static ArgumentBuilder<CommandSource, ?> register() {
		return Commands.literal("dungeons")
				.then(Commands.literal("reload").executes(DungeonsCommand::reload))
				.then(Commands.literal("enable").executes(DungeonsCommand::enable))
				.then(Commands.literal("disable").executes(DungeonsCommand::disable));
	}

	public static int reload(final CommandContext<CommandSource> context) {
		DungeonManager.getInstance().reload();
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.dungeons.reload"), true);
		return 0;
	}

	public static int enable(final CommandContext<CommandSource> context) {
		ConfigDungeons.INSTANCE.dungeonsEnabled.set(true);
		ConfigDungeons.INSTANCE.dungeonsEnabled.save();
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.dungeons.enable"), true);
		return 0;
	}

	public static int disable(final CommandContext<CommandSource> context) {
		ConfigDungeons.INSTANCE.dungeonsEnabled.set(false);
		ConfigDungeons.INSTANCE.dungeonsEnabled.save();
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.dungeons.disable"), true);
		return 0;
	}
}
