package com.lycanitesmobs.core.command;

import com.lycanitesmobs.core.spawner.SpawnerManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class SpawnerCommand {
	public static ArgumentBuilder<CommandSource, ?> register() {
		return Commands.literal("spawner")
				.then(Commands.literal("test")
						.then(Commands.argument("spawner", StringArgumentType.string())
								.then(Commands.argument("level", IntegerArgumentType.integer()).executes(SpawnerCommand::test))))
				.then(Commands.literal("lighttest").executes(SpawnerCommand::lightTest));
	}

	public static int test(final CommandContext<CommandSource> context) {
		String spawnerName = StringArgumentType.getString(context, "spawner");
		int level = Math.max(1, IntegerArgumentType.getInteger(context, "level"));
		if(!SpawnerManager.getInstance().spawners.containsKey(spawnerName)) {
			context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.spawner.test.unknown"), true);
			return 0;
		}

		World world = context.getSource().getWorld();
		PlayerEntity player = null;
		BlockPos pos = BlockPos.ZERO;
		if(context.getSource().getEntity() instanceof PlayerEntity) {
			player = (PlayerEntity)context.getSource().getEntity();
			pos = new BlockPos(player.getPositionVec());
		}

		SpawnerManager.getInstance().spawners.get(spawnerName).trigger(world, player, null, pos, level, 1, 0);
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.spawner.test"), true);
		return 0;
	}

	public static int lightTest(final CommandContext<CommandSource> context) {
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
			context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.spawner.lighttest.dark").appendString(results), true);
		}
		else {
			context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.spawner.lighttest.light").appendString(results), true);
		}
		return 0;
	}
}
