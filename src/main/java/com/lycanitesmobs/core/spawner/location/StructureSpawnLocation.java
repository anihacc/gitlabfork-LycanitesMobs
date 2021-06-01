package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.RegistryManager;

import java.util.ArrayList;
import java.util.List;

public class StructureSpawnLocation extends RandomSpawnLocation {

	/** The name of the Structure Type. Vanilla offers: Stronghold, Monument, Village, Mansion, EndCity, Fortress, Temple and Mineshaft, though Mineshaft is buggy, see the mineshaft.json spawner for a better way. Default: Stronghold. **/
	public ResourceLocation structureId;

	/** How close to the player (in blocks) Structures must be. Default: 100. **/
	public int structureRange = 100;


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);

		if(json.has("structureId"))
			this.structureId = new ResourceLocation(json.get("structureId").getAsString());

		if(json.has("structureRange"))
			this.structureRange = json.get("structureRange").getAsInt();
	}


	@Override
	public List<BlockPos> getSpawnPositions(World world, PlayerEntity player, BlockPos triggerPos) {
		Structure<?> spawnStructure = RegistryManager.ACTIVE.getRegistry(Registry.STRUCTURE_FEATURE_REGISTRY).getValue(this.structureId);
		if (spawnStructure == null) {
			LycanitesMobs.logWarning("JSONSpawner", "Invalid Structure ID: " + this.structureId + ".");
			return new ArrayList<>();
		}
		LycanitesMobs.logDebug("JSONSpawner", "Getting Nearest " + this.structureId + " Structures Within Range");
		if(!(world instanceof ServerWorld)) {
			LycanitesMobs.logWarning("", "[JSONSpawner] Structure spawn location was called with a non ServerWorld World instance.");
			return new ArrayList<>();
		}

		BlockPos structurePos = null;
		try {
			structurePos = ((ServerWorld)world).findNearestMapFeature(spawnStructure, triggerPos, this.structureRange, false);
		}
		catch (Exception e) {}

		// No Structure:
		if(structurePos == null) {
			LycanitesMobs.logDebug("JSONSpawner", "No " + this.structureId + " Structures found.");
			return new ArrayList<>();
		}

		// Too Far:
		double structureDistance = Math.sqrt(structurePos.distSqr(triggerPos));
		if(structureDistance > this.structureRange) {
			LycanitesMobs.logDebug("JSONSpawner", "No " + this.structureId + " Structures within range, nearest was: " + structureDistance + "/" + this.structureRange + " at: " + structurePos);
			return new ArrayList<>();
		}

		// Structure Found:
		LycanitesMobs.logDebug("JSONSpawner", "Found a " + this.structureId + " Structure within range, at: " + structurePos + " distance: " + structureDistance + "/" + this.structureRange);
		return super.getSpawnPositions(world, player, structurePos);
	}

}
