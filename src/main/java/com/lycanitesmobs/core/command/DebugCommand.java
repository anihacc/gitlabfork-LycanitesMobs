package com.lycanitesmobs.core.command;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.config.ConfigDebug;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class DebugCommand {
	public static ArgumentBuilder<CommandSource, ?> register() {
		return Commands.literal("debug")
				.then(Commands.literal("log").then(Commands.argument("category", StringArgumentType.string()).executes(DebugCommand::log)))
				.then(Commands.literal("list").executes(DebugCommand::list))
				.then(Commands.literal("biomesfromtag").then(Commands.argument("biometag", StringArgumentType.string()).executes(DebugCommand::biomesfromtag)))
				.then(Commands.literal("listbiometags").executes(DebugCommand::listbiometags))
				.then(Commands.literal("overlay").executes(DebugCommand::overlay));
	}

	public static int log(final CommandContext<CommandSource> context) {
		String category = StringArgumentType.getString(context, "category").toLowerCase();
		List<String> enabledLogs = new ArrayList<>();
		enabledLogs.addAll(ConfigDebug.INSTANCE.enabled.get());

		if(enabledLogs.contains(category)) {
			enabledLogs.remove(category);
			LycanitesMobs.logDebug("", category + " Debug Logging Disabled");
		}
		else {
			enabledLogs.add(category);
			LycanitesMobs.logDebug("", category + " Debug Logging Enabled");
		}
		ConfigDebug.INSTANCE.enabled.set(enabledLogs);
		ConfigDebug.INSTANCE.enabled.save();

		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.debug.log").appendString(" " + category), true);
		return 0;
	}

	public static int list(final CommandContext<CommandSource> context) {
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.debug.list"), true);
		String[] debugCategories = new String[] {"jsonspawner", "mobspawns", "entity", "subspecies", "creature", "mobevents", "dungeon", "items", "equipment"};
		for(String debugCategory : debugCategories) {
			context.getSource().sendFeedback(new StringTextComponent(debugCategory), true);
		}
		return 0;
	}

	public static int biomesfromtag(final CommandContext<CommandSource> context) {
		String biomeTag = StringArgumentType.getString(context, "biometag").toLowerCase();
		BiomeDictionary.Type biomeType = null;
		try {
			biomeType = BiomeDictionary.Type.getType(biomeTag);
		} catch (Exception e) {
			LycanitesMobs.logWarning("", "Unknown biome tag: " + biomeTag + ".");
		}
		if (biomeType == null) {
			return 0;
		}
		for(RegistryKey<Biome> biomeKey : BiomeDictionary.getBiomes(biomeType)) {
			context.getSource().sendFeedback(new StringTextComponent(biomeKey.toString()), true); // TODO Figure out how the hell to get a biome display name now...
		}
		return 0;
	}

	public static int listbiometags(final CommandContext<CommandSource> context) {
		for(BiomeDictionary.Type biomeType : BiomeDictionary.Type.getAll()) {
			context.getSource().sendFeedback(new StringTextComponent(biomeType.getName()), true);
		}
		return 0;
	}

	public static int overlay(final CommandContext<CommandSource> context) {
		ConfigDebug.INSTANCE.creatureOverlay.set(!ConfigDebug.INSTANCE.creatureOverlay.get());
		ConfigDebug.INSTANCE.creatureOverlay.save();
		context.getSource().sendFeedback(new TranslationTextComponent("lyc.command.debug.overlay"), true);
		return 0;
	}
}
