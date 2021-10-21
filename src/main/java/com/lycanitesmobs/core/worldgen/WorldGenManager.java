package com.lycanitesmobs.core.worldgen;

import com.google.common.collect.ImmutableSet;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.fluid.BaseFluidBlock;
import com.lycanitesmobs.core.info.ItemManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.level.levelgen.placement.ChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.configurations.RangeDecoratorConfiguration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;

public class WorldGenManager {
	private static WorldGenManager INSTANCE;
	public static WorldGenManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new WorldGenManager();
		}
		return INSTANCE;
	}
	
	// Placements:
	public AlwaysPlacement alwaysPlacement = new AlwaysPlacement(NoneDecoratorConfiguration.CODEC);
	
	// Features:
	public ChunkSpawnFeature chunkSpawnFeature = new ChunkSpawnFeature(NoneFeatureConfiguration.CODEC);
	public DungeonFeature dungeonFeature = new DungeonFeature(NoneFeatureConfiguration.CODEC);
	public Map<String, ConfiguredFeature<?, ?>> fluidConfiguredFeatures = new HashMap<>();

	/**
	 * Called on startup after all LM assets have been defined but before mob startup events.
	 */
	public void startup() {
		for (String fluidName : ItemManager.getInstance().worldgenFluidBlocks.keySet()) {
			BaseFluidBlock fluidBlock = ItemManager.getInstance().worldgenFluidBlocks.get(fluidName);
			ConfiguredFeature<?, ?> lakeFeature = null;
			ConfiguredFeature<?, ?> springFeature = null;
			SpringConfiguration springConfig = new SpringConfiguration(fluidBlock.getFluid().defaultFluidState(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE));

			if (fluidBlock.defaultBlockState().getMaterial() == Material.WATER) {
				lakeFeature = Feature.LAKE.configured(new BlockStateConfiguration(fluidBlock.defaultBlockState())).decorated(FeatureDecorator.WATER_LAKE.configured(new ChanceDecoratorConfiguration(40)));
				springFeature = Feature.SPRING.configured(springConfig).decorated(FeatureDecorator.RANGE_BIASED.configured(new RangeDecoratorConfiguration(8, 8, 256))).squared().count(20);
			}
			else {
				lakeFeature = Feature.LAKE.configured(new BlockStateConfiguration(fluidBlock.defaultBlockState())).decorated(FeatureDecorator.LAVA_LAKE.configured(new ChanceDecoratorConfiguration(40)));
				springFeature = Feature.SPRING.configured(springConfig).decorated(FeatureDecorator.RANGE_VERY_BIASED.configured(new RangeDecoratorConfiguration(8, 16, 256))).squared().count(20);
			}

			fluidConfiguredFeatures.put(fluidName + "_lake", lakeFeature);
			fluidConfiguredFeatures.put(fluidName + "_spring", springFeature);
		}
	}

	@SubscribeEvent
	public void registerPlacements(RegistryEvent.Register<FeatureDecorator<?>> event) {
		event.getRegistry().register(this.alwaysPlacement.setRegistryName(LycanitesMobs.modInfo.modid, "always"));
	}

	@SubscribeEvent
	public void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
		event.getRegistry().register(this.chunkSpawnFeature.setRegistryName(LycanitesMobs.modInfo.modid, "chunkspawn"));
		event.getRegistry().register(this.dungeonFeature.setRegistryName(LycanitesMobs.modInfo.modid, "dungeon"));
	}
}
