package com.lycanitesmobs.core.command;

import com.lycanitesmobs.core.config.ConfigDebug;
import com.lycanitesmobs.core.info.CreatureManager;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class DebugCommand {
	public static ArgumentBuilder<CommandSource, ?> register() {
		return Commands.literal("debug")
				.then(Commands.literal("overlay").executes(DebugCommand::overlay));
	}

	public static int overlay(final CommandContext<CommandSource> context) {
		ConfigDebug.INSTANCE.creatureOverlay.set(!ConfigDebug.INSTANCE.creatureOverlay.get());
		ConfigDebug.INSTANCE.creatureOverlay.save();
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.debug.overlay"), true);
		return 0;
	}
}
