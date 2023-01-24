package com.lycanitesmobs.core.dungeon.definition;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.helpers.JSONHelper;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public abstract class DungeonPlacer {

	public static DungeonPlacer createFromJson(JsonObject json) {
		String type = json.get("type").getAsString();
		DungeonPlacer spawnCondition = null;

		if ("random".equalsIgnoreCase(type)) {
			spawnCondition = new RandomPlacer();
		} else if ("surface".equalsIgnoreCase(type)) {
			spawnCondition = new SurfacePlacer();
		}

		spawnCondition.loadFromJSON(json);
		return spawnCondition;
	}

	public void loadFromJSON(JsonObject json) {

	}

	public abstract BlockPos calculatePosition(World world, BlockPos pos, Random random);

	public static class RandomPlacer extends DungeonPlacer {

		private int minY = 64;
		private int maxY = 64;

		@Override
		public void loadFromJSON(JsonObject json) {
			if (json.has("minY"))
				minY = json.get("minY").getAsInt();
			if (json.has("maxY"))
				maxY = json.get("maxY").getAsInt();
		}

		@Override
		public BlockPos calculatePosition(World world, BlockPos pos, Random random) {
			int y = minY;
			if (minY < maxY)
				y += random.nextInt(maxY - minY);
			return new BlockPos(pos.getX(), y, pos.getZ());
		}

	}

	public static class SurfacePlacer extends DungeonPlacer {

		private Set<Material> materialsToIgnore = Stream.of(Material.AIR, Material.WOOD, Material.LEAVES,
				Material.PLANTS, Material.VINE, Material.SNOW, Material.CACTUS).collect(Collectors.toSet());

		@Override
		public void loadFromJSON(JsonObject json) {
			materialsToIgnore = new HashSet<>(JSONHelper.getJsonMaterials(json));
		}

		@Override
		public BlockPos calculatePosition(World world, BlockPos pos, Random random) {
			Chunk chunk = world.getChunkFromBlockCoords(pos);
			MutableBlockPos mutablePos = new MutableBlockPos(pos.getX(), chunk.getTopFilledSegment() + 15, pos.getZ());
			Material material = chunk.getBlockState(mutablePos).getMaterial();
			while (mutablePos.getY() > 0 && materialsToIgnore.contains(material)) {
				mutablePos.setY(mutablePos.getY() - 1);
				material = chunk.getBlockState(mutablePos).getMaterial();
			}
			return new BlockPos(pos.getX(), mutablePos.getY(), pos.getZ());
		}

	}

}
