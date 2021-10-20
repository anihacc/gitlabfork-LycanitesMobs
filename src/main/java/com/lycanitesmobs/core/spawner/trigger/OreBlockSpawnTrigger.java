package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OreBlockSpawnTrigger extends BlockSpawnTrigger {

	/** If true, ores (ore blocks that drop as blocks as well as coal and monster egg) will trigger. **/
	public boolean ores = true;

	/** If true, gems (ore blocks that drop as items excluding coal and monster egg) will trigger. **/
	public boolean gems = false;

	/** Constructor **/
	public OreBlockSpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);

		if(json.has("ores"))
			this.ores = json.get("ores").getAsBoolean();

		if(json.has("gems"))
			this.gems = json.get("gems").getAsBoolean();
	}


	@Override
	public boolean isTriggerBlock(IBlockState blockState, World world, BlockPos blockPos, int fortune) {
		Block block = blockState.getBlock();

		if(block == Blocks.MONSTER_EGG) {
			return this.ores;
		}
		if(block == Blocks.COAL_ORE) {
			return this.ores;
		}

		if(block.getRegistryName() == null) {
			return false;
		}
		String blockName = block.getRegistryName().getResourcePath();
		String[] blockNameParts = blockName.split("\\.");
		for (String blockNamePart : blockNameParts) {
			int blockNamePartLength = blockNamePart.length();

			// Check if start or end of block name part is "ore" or "crystal".
			boolean nameMatch = false;
			if (blockNamePartLength >= 3) {
				if (blockNamePart.substring(0, 3).equalsIgnoreCase("ore") || blockNamePart.substring(blockNamePartLength - 3, blockNamePartLength).equalsIgnoreCase("ore")) {
					nameMatch = true;
				}
			}
			if (!nameMatch && blockNamePartLength >= 7) {
				if ( blockNamePart.substring(0, 7).equalsIgnoreCase("crystal") || blockNamePart.substring(blockNamePartLength - 7, blockNamePartLength).equalsIgnoreCase("crystal")) {
					nameMatch = true;
				}
			}

			if (nameMatch) {
				if(this.ores && this.gems) {
					return true;
				}
				Item dropItem = block.getItemDropped(blockState, world.rand, fortune);
				if(dropItem instanceof ItemBlock || blockName.contains("coal")) {
					return this.ores;
				}
				else {
					return this.gems;
				}
			}
		}

		return false;
	}

	@Override
	public int getBlockLevel(IBlockState blockState, World world, BlockPos blockPos) {
		Block block = blockState.getBlock();
		if(block == Blocks.DIAMOND_ORE)
			return 3;
		if(block == Blocks.EMERALD_ORE)
			return 3;
		if(block == Blocks.LAPIS_ORE)
			return 2;
		if(block == Blocks.GOLD_ORE)
			return 2;
		if(block == Blocks.IRON_ORE)
			return 1;
		return 0;
	}
}
