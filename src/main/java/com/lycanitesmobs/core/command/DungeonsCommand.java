package com.lycanitesmobs.core.command;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.core.config.ConfigDungeons;
import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.dungeon.instance.DungeonInstance;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import java.util.List;

public class DungeonsCommand {
	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("dungeons")
				.then(Commands.literal("reload").executes(DungeonsCommand::reload))
				.then(Commands.literal("enable").executes(DungeonsCommand::enable))
				.then(Commands.literal("disable").executes(DungeonsCommand::disable))
				.then(Commands.literal("locate").executes(DungeonsCommand::locate));
	}

	public static int reload(final CommandContext<CommandSourceStack> context) {
		if (!context.getSource().hasPermission(2)) {
			return 0;
		}
		DungeonManager.getInstance().reload();
		context.getSource().sendSuccess(new TranslatableComponent("lyc.command.dungeons.reload"), true);
		return 0;
	}

	public static int enable(final CommandContext<CommandSourceStack> context) {
		if (!context.getSource().hasPermission(2)) {
			return 0;
		}
		ConfigDungeons.INSTANCE.dungeonsEnabled.set(true);
		ConfigDungeons.INSTANCE.dungeonsEnabled.save();
		context.getSource().sendSuccess(new TranslatableComponent("lyc.command.dungeons.enable"), true);
		return 0;
	}

	public static int disable(final CommandContext<CommandSourceStack> context) {
		if (!context.getSource().hasPermission(2)) {
			return 0;
		}
		ConfigDungeons.INSTANCE.dungeonsEnabled.set(false);
		ConfigDungeons.INSTANCE.dungeonsEnabled.save();
		context.getSource().sendSuccess(new TranslatableComponent("lyc.command.dungeons.disable"), true);
		return 0;
	}

	public static int locate(final CommandContext<CommandSourceStack> context) {
		if (!context.getSource().hasPermission(2)) {
			return 0;
		}
		context.getSource().sendSuccess(new TranslatableComponent("lyc.command.dungeons.locate"), true);
		Level world = context.getSource().getLevel();
		ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(world);
		List<DungeonInstance> nearbyDungeons = extendedWorld.getNearbyDungeonInstances(new ChunkPos(new BlockPos(context.getSource().getPosition())), ConfigDungeons.INSTANCE.dungeonDistance.get() * 2);
		if(nearbyDungeons.isEmpty()) {
			context.getSource().sendSuccess(new TranslatableComponent("common.none"), true);
			return 0;
		}
		for(DungeonInstance dungeonInstance : nearbyDungeons) {
			context.getSource().sendSuccess(new TextComponent(dungeonInstance.toString()), true);
		}
		return 0;
	}
}
