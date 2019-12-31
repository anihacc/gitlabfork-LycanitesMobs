package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SilverfishBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

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
	public boolean isTriggerBlock(BlockState blockState, World world, BlockPos blockPos, int fortune, @Nullable LivingEntity entity) {
		Block block = blockState.getBlock();

		if(block instanceof SilverfishBlock) {
			return this.ores;
		}
		if(block == Blocks.COAL_ORE) {
			return this.ores;
		}

		if(block.getRegistryName() == null) {
			return false;
		}
		String blockName = block.getRegistryName().getPath();
		String[] blockNameParts = blockName.split("\\.");
		for(String blockNamePart : blockNameParts) {
			int blockNamePartLength = blockNamePart.length();
			if(blockNamePartLength >= 3) {
				// Check if start or end of block name part is "ore".
				if(
						blockNamePart.substring(0, 3).equalsIgnoreCase("ore")
						|| blockNamePart.substring(blockNamePartLength - 3, blockNamePartLength).equalsIgnoreCase("ore")
						|| blockNamePart.substring(0, 3).equalsIgnoreCase("crystal")
						|| blockNamePart.substring(blockNamePartLength - 3, blockNamePartLength).equalsIgnoreCase("crystal")
				) {
					if(this.ores && this.gems) {
						return true;
					}

					if(world instanceof ServerWorld) {
						List<ItemStack> drops;
						if(entity == null) {
							drops = block.getDrops(blockState, (ServerWorld)world, blockPos, null);
						}
						else {
							drops = block.getDrops(blockState, (ServerWorld)world, blockPos, null, entity, entity.getActiveItemStack());
						}
						for(ItemStack dropStack : drops) {
							if(dropStack.getItem() instanceof BlockItem) {
								return this.ores;
							}
							else {
								return this.gems;
							}
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	public int getBlockLevel(BlockState blockState, World world, BlockPos blockPos) {
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
