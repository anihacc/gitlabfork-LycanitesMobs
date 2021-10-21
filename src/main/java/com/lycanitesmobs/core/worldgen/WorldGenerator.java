package com.lycanitesmobs.core.worldgen;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldGenerator {
	@SubscribeEvent
	public void registerFeatures(BiomeLoadingEvent event) {
		WorldGenManager manager = WorldGenManager.getInstance();

		// Chunk Spawner:
		event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION.ordinal(),
				() -> manager.chunkSpawnFeature.configured(FeatureConfiguration.NONE).decorated(manager.alwaysPlacement.configured(NoneDecoratorConfiguration.INSTANCE)));

		// Dungeons:
		event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION.ordinal(),
				() -> manager.dungeonFeature.configured(FeatureConfiguration.NONE).decorated(manager.alwaysPlacement.configured(NoneDecoratorConfiguration.INSTANCE)));

		// Lakes:
		if (event.getCategory() == Biome.BiomeCategory.SWAMP || event.getCategory() == Biome.BiomeCategory.MUSHROOM) {
			event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, manager.fluidConfiguredFeatures.get("poison_lake"));
			event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, manager.fluidConfiguredFeatures.get("poison_spring"));
		}
		else if (event.getCategory() == Biome.BiomeCategory.DESERT || event.getCategory() == Biome.BiomeCategory.MESA || event.getCategory() == Biome.BiomeCategory.THEEND) {
			event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, manager.fluidConfiguredFeatures.get("acid_lake"));
			event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, manager.fluidConfiguredFeatures.get("acid_spring"));
		}
		else if (event.getCategory() == Biome.BiomeCategory.ICY || event.getCategory() == Biome.BiomeCategory.EXTREME_HILLS) {
			event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, manager.fluidConfiguredFeatures.get("ooze_lake"));
			event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, manager.fluidConfiguredFeatures.get("ooze_spring"));
		}
		else if (event.getCategory() == Biome.BiomeCategory.NETHER || event.getCategory() == Biome.BiomeCategory.JUNGLE) {
			event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, manager.fluidConfiguredFeatures.get("moglava_lake"));
			event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, manager.fluidConfiguredFeatures.get("moglava_spring"));
		}
	}
}
