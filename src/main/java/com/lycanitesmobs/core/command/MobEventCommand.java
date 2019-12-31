package com.lycanitesmobs.core.command;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.core.mobevent.MobEventListener;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class MobEventCommand {
	public static ArgumentBuilder<CommandSource, ?> register() {
		return Commands.literal("mobevent")
				.then(Commands.literal("start")
						.then(Commands.argument("mobevent", StringArgumentType.string())
								.then(Commands.argument("level", IntegerArgumentType.integer())
										.executes(MobEventCommand::start)
											.then(Commands.argument("world", IntegerArgumentType.integer())
													.executes(MobEventCommand::startWorld))
				)))
				.then(Commands.literal("random")
							.then(Commands.argument("level", IntegerArgumentType.integer())
									.executes(MobEventCommand::random)
									.then(Commands.argument("world", IntegerArgumentType.integer())
											.executes(MobEventCommand::randomWorld))
							))
				.then(Commands.literal("stop")
						.executes(MobEventCommand::stop)
							.then(Commands.argument("world", IntegerArgumentType.integer())
									.executes(MobEventCommand::stopWorld)));
	}

	public static int start(final CommandContext<CommandSource> context) {
		World world = context.getSource().getWorld();
		String eventName = StringArgumentType.getString(context, "mobevent");
		int level = Math.max(1, IntegerArgumentType.getInteger(context, "level"));

		if(!MobEventManager.getInstance().mobEvents.containsKey(eventName)) {
			context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.mobevent.start.unknown"), true);
			return 0;
		}

		ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(world);
		if(extendedWorld == null) {
			return 0;
		}
		if(context.getSource().getEntity() instanceof PlayerEntity) {
			extendedWorld.startMobEvent(eventName, (PlayerEntity)context.getSource().getEntity(), context.getSource().getEntity().getPosition(), level);
		}
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.mobevent.start"), true);
		return 0;
	}

	public static int startWorld(final CommandContext<CommandSource> context) {
		String eventName = StringArgumentType.getString(context, "mobevent");
		int level = Math.max(1, IntegerArgumentType.getInteger(context, "level"));
		int worldId = IntegerArgumentType.getInteger(context, "world");

		if(!MobEventManager.getInstance().mobEvents.containsKey(eventName)) {
			context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.mobevent.start.unknown"), true);
			return 0;
		}

		World world = DimensionManager.getWorld(context.getSource().getServer(), DimensionType.getById(worldId), false, false);
		ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(world);
		if(extendedWorld == null) {
			return 0;
		}
		if(context.getSource().getEntity() instanceof PlayerEntity) {
			extendedWorld.startMobEvent(eventName, (PlayerEntity)context.getSource().getEntity(), context.getSource().getEntity().getPosition(), level);
		}
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.mobevent.start"), true);
		return 0;
	}

	public static int random(final CommandContext<CommandSource> context) {
		World world = context.getSource().getWorld();
		int level = Math.max(1, IntegerArgumentType.getInteger(context, "level"));

		ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(world);
		if(extendedWorld == null) {
			return 0;
		}
		extendedWorld.stopWorldEvent();
		MobEventListener.getInstance().triggerRandomMobEvent(world, extendedWorld, level);
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.mobevent.random"), true);
		return 0;
	}

	public static int randomWorld(final CommandContext<CommandSource> context) {
		int level = Math.max(1, IntegerArgumentType.getInteger(context, "level"));
		int worldId = IntegerArgumentType.getInteger(context, "world");

		World world = DimensionManager.getWorld(context.getSource().getServer(), DimensionType.getById(worldId), false, false);
		ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(world);
		if(extendedWorld == null) {
			return 0;
		}
		extendedWorld.stopWorldEvent();
		MobEventListener.getInstance().triggerRandomMobEvent(world, extendedWorld, level);
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.mobevent.random"), true);
		return 0;
	}

	public static int stop(final CommandContext<CommandSource> context) {
		World world = context.getSource().getWorld();
		ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(world);
		if(extendedWorld == null) {
			return 0;
		}
		extendedWorld.stopWorldEvent();
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.mobevent.stop"), true);
		return 0;
	}

	public static int stopWorld(final CommandContext<CommandSource> context) {
		int worldId = IntegerArgumentType.getInteger(context, "world");
		World world = DimensionManager.getWorld(context.getSource().getServer(), DimensionType.getById(worldId), false, false);
		ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(world);
		if(extendedWorld == null) {
			return 0;
		}
		extendedWorld.stopWorldEvent();
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.mobevent.stop"), true);
		return 0;
	}
}
