package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public class StructureSpawnLocation extends RandomSpawnLocation {

	/** The name of the Structure Type. Vanilla offers: Stronghold, Monument, Village, Mansion, EndCity, Fortress, Temple and Mineshaft, though Mineshaft is buggy, see the mineshaft.json spawner for a better way. Default: Stronghold. **/
	public String structureName = Structure.STRONGHOLD.getFeatureName();

	/** How close to the player (in blocks) Structures must be. Default: 100. **/
	public int structureRange = 100;


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);

		if(json.has("structureType"))
			this.structureName = json.get("structureType").getAsString();

		if(json.has("structureRange"))
			this.structureRange = json.get("structureRange").getAsInt();
	}


	@Override
	public List<BlockPos> getSpawnPositions(World world, PlayerEntity player, BlockPos triggerPos) {
		LycanitesMobs.logDebug("JSONSpawner", "Getting Nearest " + this.structureName + " Type Structures Within Range");
		if(!(world instanceof ServerWorld)) {
			LycanitesMobs.logWarning("", "[JSONSpawner] Structure spawn location was called with a non ServerWorld World instance.");
			return new ArrayList<>();
		}

		BlockPos structurePos = null;
		try {
			structurePos = ((ServerWorld)world).findNearestMapFeature(Structure.STRONGHOLD, triggerPos, this.structureRange, false);
		}
		catch (Exception e) {}

		// No Structure:
		if(structurePos == null) {
			LycanitesMobs.logDebug("JSONSpawner", "No " + this.structureName + " Structures found.");
			return new ArrayList<>();
		}

		// Too Far:
		double structureDistance = Math.sqrt(structurePos.distSqr(triggerPos));
		if(structureDistance > this.structureRange * this.structureRange) {
			LycanitesMobs.logDebug("JSONSpawner", "No " + this.structureName + " Structures within range, nearest was: " + structureDistance + "/" + (this.structureRange * this.structureRange) + " at: " + structurePos);
			return new ArrayList<>();
		}

		// Village Found:
		LycanitesMobs.logDebug("JSONSpawner", "Found a " + this.structureName + " Structure within range, at: " + structurePos);
		return super.getSpawnPositions(world, player, structurePos);
	}

}
