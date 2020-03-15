package com.lycanitesmobs.core.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JSONHelper {

	public static Vec3i getVec3i(JsonObject json, String memberName) {
		if(json.has(memberName)) {
			JsonArray jsonArray = json.get(memberName).getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			int[] coords = new int[3];
			int i = 0;
			while (jsonIterator.hasNext() && i < coords.length) {
				coords[i] = jsonIterator.next().getAsInt();
				i++;
			}
			return new Vec3i(coords[0], coords[1], coords[2]);
		}
		return new Vec3i(0, 0, 0);
	}

	public static Vec3d getVec3d(JsonObject json, String memberName, Vec3d defaultVec) {
		if(json.has(memberName)) {
			JsonArray jsonArray = json.get(memberName).getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			double[] coords = new double[3];
			int i = 0;
			while (jsonIterator.hasNext() && i < coords.length) {
				coords[i] = jsonIterator.next().getAsDouble();
				i++;
			}
			return new Vec3d(coords[0], coords[1], coords[2]);
		}
		return defaultVec;
	}

	public static List<String> getJsonStrings(JsonArray jsonArray) {
		List<String> strings = new ArrayList<>();
		Iterator<JsonElement> jsonIterator = jsonArray.iterator();
		while (jsonIterator.hasNext()) {
			String string = jsonIterator.next().getAsString();
			strings.add(string);
		}
		return strings;
	}

	public static List<Block> getJsonBlocks(JsonObject json) {
		List<Block> blocks = new ArrayList<>();
		if(json.has("blocks")) {
			blocks = getJsonBlocks(json.get("blocks").getAsJsonArray());
		}
		return blocks;
	}

	public static List<Block> getJsonBlocks(JsonArray jsonArray) {
		List<Block> blocks = new ArrayList<>();
		Iterator<JsonElement> jsonIterator = jsonArray.iterator();
		while (jsonIterator.hasNext()) {
			Block block = GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(jsonIterator.next().getAsString()));
			if(block != null) {
				blocks.add(block);
			}
		}
		return blocks;
	}

	public static List<Item> getJsonItems(JsonArray jsonArray) {
		List<Item> items = new ArrayList<>();
		Iterator<JsonElement> jsonIterator = jsonArray.iterator();
		while (jsonIterator.hasNext()) {
			Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(jsonIterator.next().getAsString()));
			if(item != null) {
				items.add(item);
			}
		}
		return items;
	}

	public static List<Material> getJsonMaterials(JsonObject json) {
		List<Material> materials = new ArrayList<>();
		if(json.has("materials")) {
			JsonArray jsonArray = json.get("materials").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				Material material = null;
				String materialName = jsonIterator.next().getAsString();

				if("air".equalsIgnoreCase(materialName)) {
					material = Material.AIR;
				}
				else if("lava".equalsIgnoreCase(materialName)) {
					material = Material.LAVA;
				}
				else if("fire".equalsIgnoreCase(materialName)) {
					material = Material.FIRE;
				}
				else if("water".equalsIgnoreCase(materialName)) {
					material = Material.WATER;
				}
				else if("ground".equalsIgnoreCase(materialName)) {
					material = Material.GROUND;
				}
				else if("sand".equalsIgnoreCase(materialName)) {
					material = Material.SAND;
				}
				else if("claw".equalsIgnoreCase(materialName)) {
					material = Material.CLAY;
				}
				else if("wood".equalsIgnoreCase(materialName)) {
					material = Material.WOOD;
				}
				else if("rock".equalsIgnoreCase(materialName)) {
					material = Material.ROCK;
				}
				else if("grass".equalsIgnoreCase(materialName)) {
					material = Material.GRASS;
				}
				else if("plants".equalsIgnoreCase(materialName)) {
					material = Material.PLANTS;
				}
				else if("leaves".equalsIgnoreCase(materialName)) {
					material = Material.LEAVES;
				}
				else if("vine".equalsIgnoreCase(materialName)) {
					material = Material.VINE;
				}
				else if("cactus".equalsIgnoreCase(materialName)) {
					material = Material.CACTUS;
				}
				else if("snow".equalsIgnoreCase(materialName)) {
					material = Material.SNOW;
				}
				else if("ice".equalsIgnoreCase(materialName)) {
					material = Material.ICE;
				}
				else if("iron".equalsIgnoreCase(materialName)) {
					material = Material.IRON;
				}
				else if("web".equalsIgnoreCase(materialName)) {
					material = Material.WEB;
				}

				if(material != null) {
					materials.add(material);
				}
			}
		}
		return materials;
	}

	public static List<Biome> getBiomesFromTags(List<String> biomeTags) {
		List<Biome> biomeList = new ArrayList<>();
		for(String biomeEntry : biomeTags) {

			// Determine Function:
			boolean additive = true;
			if (biomeEntry.charAt(0) == '-' || biomeEntry.charAt(0) == '+') {
				if (biomeEntry.charAt(0) == '-')
					additive = false;
				biomeEntry = biomeEntry.substring(1);
			}

			List<Biome> selectedBiomes = new ArrayList<>();
			if ("ALL".equalsIgnoreCase(biomeEntry)) {
				for (BiomeDictionary.Type biomeType : BiomeDictionary.Type.getAll()) {
					Set<Biome> selectedBiomesSet = BiomeDictionary.getBiomes(biomeType);
					selectedBiomes.addAll(selectedBiomesSet);
				}
			}
			else if (!"NONE".equalsIgnoreCase(biomeEntry)) {
				BiomeDictionary.Type biomeType = null;
				try {
					biomeType = BiomeDictionary.Type.getType(biomeEntry);
				} catch (Exception e) {
					LycanitesMobs.logWarning("", "[Spawning] Unknown biome type " + biomeEntry + " this will be ignored and treated as NONE.");
				}
				if (biomeType != null) {
					Set<Biome> selectedBiomesSet = BiomeDictionary.getBiomes(biomeType);
					selectedBiomes.addAll(selectedBiomesSet);
				}
			}

			if (!selectedBiomes.isEmpty()) {
				for (Biome biome : selectedBiomes) {
					if (additive) {
						if (!biomeList.contains(biome)) {
							biomeList.add(biome);
						}
					} else {
						biomeList.remove(biome);
					}
				}
			}
		}

		return biomeList;
	}

	public static List<Biome> getBiomes(List<String> biomeIds) {
		List<Biome> biomes = new ArrayList<>();
		for(String biomeId : biomeIds) {
			Biome biome = GameRegistry.findRegistry(Biome.class).getValue(new ResourceLocation(biomeId));
			if(biome != null) {
				biomes.add(biome);
			}
		}
		return biomes;
	}
}
