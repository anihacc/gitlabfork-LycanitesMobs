package com.lycanitesmobs.core.worldgen;

import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldGenerator {
	@SubscribeEvent
	public void registerFeatures(BiomeLoadingEvent event) {
		WorldGenManager manager = WorldGenManager.getInstance();
		event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION.ordinal(),
				() -> manager.chunkSpawnFeature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(manager.alwaysPlacement.configure(NoPlacementConfig.INSTANCE)));
		event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES.ordinal(),
				() -> manager.dungeonFeature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(manager.alwaysPlacement.configure(NoPlacementConfig.INSTANCE)));
	}
}
