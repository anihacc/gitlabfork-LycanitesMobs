package com.lycanitesmobs.core.command;

import com.lycanitesmobs.core.item.equipment.EquipmentPartManager;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class EquipmentCommand {
	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("equipment")
				.then(Commands.literal("reload").executes(EquipmentCommand::reload));
	}

	public static int reload(final CommandContext<CommandSourceStack> context) {
		if (!context.getSource().hasPermission(2)) {
			return 0;
		}
		EquipmentPartManager.getInstance().reload();
		context.getSource().sendSuccess(new TranslatableComponent("lyc.command.equipment.reload"), true);
		return 0;
	}
}
