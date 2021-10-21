package com.lycanitesmobs.core.command;

import com.lycanitesmobs.core.spawner.SpawnerManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class SpawnerCommand {
	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("spawner")
				.then(Commands.literal("test")
						.then(Commands.argument("spawner", StringArgumentType.string())
								.then(Commands.argument("level", IntegerArgumentType.integer()).executes(SpawnerCommand::test))))
				.then(Commands.literal("lighttest").executes(SpawnerCommand::lightTest));
	}

	public static int test(final CommandContext<CommandSourceStack> context) {
		if (!context.getSource().hasPermission(2)) {
			return 0;
		}
		String spawnerName = StringArgumentType.getString(context, "spawner");
		int level = Math.max(1, IntegerArgumentType.getInteger(context, "level"));
		if(!SpawnerManager.getInstance().spawners.containsKey(spawnerName)) {
			context.getSource().sendSuccess(new TranslatableComponent("lyc.command.spawner.test.unknown"), true);
			return 0;
		}

		Level world = context.getSource().getLevel();
		Player player = null;
		BlockPos pos = BlockPos.ZERO;
		if(context.getSource().getEntity() instanceof Player) {
			player = (Player)context.getSource().getEntity();
			pos = new BlockPos(player.position());
		}

		SpawnerManager.getInstance().spawners.get(spawnerName).trigger(world, player, null, pos, level, 1, 0);
		context.getSource().sendSuccess(new TranslatableComponent("lyc.command.spawner.test"), true);
		return 0;
	}

	public static int lightTest(final CommandContext<CommandSourceStack> context) {
		if (!context.getSource().hasPermission(2)) {
			return 0;
		}
		if(context.getSource().getEntity() == null) {
			return 0;
		}
		float brightness = context.getSource().getEntity().getBrightness();
		int level = 3;
		if(brightness == 0) level = 0;
		else if(brightness < 0.25F) level = 1;
		else if(brightness < 1) level = 2;
		String results = " Level: " + level + " Brightness: " + brightness;
		if(level <= 1) {
			context.getSource().sendSuccess(new TranslatableComponent("lyc.command.spawner.lighttest.dark").append(results), true);
		}
		else {
			context.getSource().sendSuccess(new TranslatableComponent("lyc.command.spawner.lighttest.light").append(results), true);
		}
		return 0;
	}
}
