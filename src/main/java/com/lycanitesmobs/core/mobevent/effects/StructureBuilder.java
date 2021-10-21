package com.lycanitesmobs.core.mobevent.effects;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public abstract class StructureBuilder {
	public static Map<String, StructureBuilder> STRUCTURE_BUILDERS = new HashMap<>();

	public String name;

	/** Gets a Structure Builder by name. **/
	public static StructureBuilder getStructureBuilder(String name) {
		if(STRUCTURE_BUILDERS.containsKey(name)) {
			return STRUCTURE_BUILDERS.get(name);
		}
		return null;
	}


	/** Adds a new Structure Builder. **/
	public static void addStructureBuilder(StructureBuilder structureBuilder) {
		STRUCTURE_BUILDERS.put(structureBuilder.name, structureBuilder );
	}


	public abstract void build(Level world, Player player, BlockPos pos, int level, int ticks, int variant);
}
