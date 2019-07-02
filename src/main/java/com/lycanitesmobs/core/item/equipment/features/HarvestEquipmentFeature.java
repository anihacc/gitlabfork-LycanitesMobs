package com.lycanitesmobs.core.item.equipment.features;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.Set;

public class HarvestEquipmentFeature extends EquipmentFeature {
	/** List of blocks by harvest type. These are checked after Materials are checked. **/
	private static final Set<Block> SPADE_HARVEST = Sets.newHashSet(Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.GRAY_CONCRETE_POWDER);
	private static final Set<Block> PICKAXE_HARVEST = Sets.newHashSet(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.POWERED_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE);
	private static final Set<Block> AXE_HARVEST = Sets.newHashSet(Blocks.OAK_PLANKS, Blocks.BOOKSHELF, Blocks.CHEST, Blocks.PUMPKIN, Blocks.MELON, Blocks.LADDER, Blocks.OAK_BUTTON, Blocks.OAK_PRESSURE_PLATE);

	/** The type of tool to harvest as. Can be: pickaxe, axe, shovel, hoe, sword or shears. **/
	public String harvestType;

	/** The shape of the harvest. Can be block, cross or random. **/
	public String harvestShape = "block";

	/** How much harvest speed to add when harvesting compatible blocks. **/
	public float harvestSpeed = 1;

	/** The level of harvesting. 0 = Wood, 1 = Stone, 2 = Iron, 3 = Diamond. Currently not used as all tools are diamond level. **/
	public int harvestLevel = 3;

	/** The range of the harvest shape, the central block is not affected by this. **/
	public Vec3i harvestRange = new Vec3i(0, 0, 0);

	/** Each extra level of the part that is using this featured increases the range by its base range times by this multiplier per level. **/
	public double harvestRangeLevelMultiplier = 1;


	// ==================================================
	//                        JSON
	// ==================================================
	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);

		this.harvestType = json.get("harvestType").getAsString();

		if(json.has("harvestType"))
			this.harvestType = json.get("harvestType").getAsString();

		if(json.has("harvestSpeed"))
			this.harvestSpeed = json.get("harvestSpeed").getAsFloat();

		if(json.has("harvestLevel"))
			this.harvestLevel = json.get("harvestLevel").getAsInt();

		if(json.has("harvestShape"))
			this.harvestShape = json.get("harvestShape").getAsString();

		this.harvestRange = JSONHelper.getVec3i(json, "harvestRange");

		if(json.has("harvestRangeLevelMultiplier"))
			this.harvestRangeLevelMultiplier = json.get("harvestRangeLevelMultiplier").getAsDouble();
	}

	@Override
	public ITextComponent getDescription(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		ITextComponent description = new TranslationTextComponent("equipment.feature." + this.featureType)
			.appendText(" " + this.harvestType);

		description.appendText("\n")
			.appendSibling(new TranslationTextComponent("equipment.feature.harvest.shape"))
			.appendText(" " + this.harvestShape);

		if(this.harvestRange.distanceSq(new Vec3i(0, 0, 0)) > 0) {
			description.appendText("\n")
					.appendSibling(new TranslationTextComponent("equipment.feature.harvest.range"))
					.appendText(" " + this.getHarvestRangeString(level));
		}

		return description;
	}

	public String getHarvestRangeString(int level) {
		String harvestRangeString = "" + Math.round(this.harvestRange.getX() + (this.harvestRange.getX() * (level - 1) * this.harvestRangeLevelMultiplier));
		harvestRangeString += ", " + Math.round(this.harvestRange.getY() + (this.harvestRange.getY() * (level - 1) * this.harvestRangeLevelMultiplier));
		harvestRangeString += ", " + Math.round(this.harvestRange.getZ() + (this.harvestRange.getZ() * (level - 1) * this.harvestRangeLevelMultiplier));
		return harvestRangeString;
	}


	// ==================================================
	//                     Harvesting
	// ==================================================
	/**
	 * Returns if this feature can harvest the provided block or not.
	 * @param blockState
	 * @return
	 */
	public boolean canHarvestBlock(BlockState blockState) {
		Block block = blockState.getBlock();
		Material material = blockState.getMaterial();

		// Stone:
		if(material == Material.IRON || material == Material.ANVIL || material == Material.ROCK || PICKAXE_HARVEST.contains(block)) {
			return this.harvestType.equalsIgnoreCase("pickaxe");
		}

		// Wood:
		if(material == Material.WOOD || AXE_HARVEST.contains(block)) {
			return this.harvestType.equalsIgnoreCase("axe");
		}

		// Plants:
		if(material == Material.PLANTS || material == Material.TALL_PLANTS) {
			return this.harvestType.equalsIgnoreCase("axe") || this.harvestType.equalsIgnoreCase("sword") || this.harvestType.equalsIgnoreCase("shears");
		}

		// Web and Leaves:
		if(material == Material.WEB || material == Material.LEAVES || material == Material.SEA_GRASS) {
			return this.harvestType.equalsIgnoreCase("sword") || this.harvestType.equalsIgnoreCase("shears");
		}

		// Dirt:
		if(material == Material.EARTH || material == Material.CLAY || material == Material.SAND || material == Material.ORGANIC || SPADE_HARVEST.contains(block)) {
			return this.harvestType.equalsIgnoreCase("shovel");
		}

		// Growth:
		if(material == Material.CORAL || material == Material.GOURD) {
			return this.harvestType.equalsIgnoreCase("sword");
		}

		// Wire:
		if(block == Blocks.TRIPWIRE) {
			return this.harvestType.equalsIgnoreCase("shears");
		}

		return false;
	}


	/**
	 * Returns the speed that this feature adds to harvesting the provided block.
	 * @param blockState The block to harvest.
	 * @return The harvest speed to add (all harvest features have their speed added together).
	 */
	public float getHarvestSpeed(BlockState blockState) {
		if(!this.canHarvestBlock(blockState)) {
			return 0;
		}

		return this.harvestSpeed * this.getHarvestMultiplier(blockState);
	}


	/**
	 * Returns a harvest speed multiplier for the provided block.
	 * @param blockState The block to check.
	 * @return A harvest speed multiplier.
	 */
	public float getHarvestMultiplier(BlockState blockState) {
		Material material = blockState.getMaterial();

		// Web:
		if(material == Material.WEB) {
			return 10;
		}

		// Shears:
		if((material == Material.LEAVES || material == Material.TALL_PLANTS) && this.harvestType.equalsIgnoreCase("shears")) {
			return 10;
		}

		return 1;
	}


	/**
	 * Called when a block is destroyed by Equipment with this Feature.
	 * @param world The world where the block was destroyed.
	 * @param blockState The block state that was destroyed.
	 * @param pos The position of the destroyed block.
	 * @param entityLiving The entity that destroyed the block.
	 */
	public void onBlockDestroyed(World world, BlockState blockState, BlockPos pos, LivingEntity entityLiving)
	{
		LycanitesMobs.logWarning("", "Area Harvesting! " + this.harvestRange);
		// Block and Random Area Harvesting:
		if(this.harvestShape.equalsIgnoreCase("block") || this.harvestShape.equalsIgnoreCase("random")) {
			if(this.harvestRange.getX() <= 1 && this.harvestRange.getY() <= 1 && this.harvestRange.getZ() <= 1) {
				return;
			}

			boolean random = this.harvestShape.equalsIgnoreCase("random");

			for(int x = pos.getX() - (this.harvestRange.getX() - 1); x < pos.getX() + this.harvestRange.getX(); x++) {
				for(int y = pos.getY() - (this.harvestRange.getY() - 1); y < pos.getY() + this.harvestRange.getY(); y++) {
					for(int z = pos.getZ() - (this.harvestRange.getZ() - 1); z < pos.getZ() + this.harvestRange.getZ(); z++) {
						BlockPos destroyPos = new BlockPos(x, y, z);
						if(destroyPos.equals(pos) || !this.canHarvestBlock(world.getBlockState(destroyPos))) {
							continue;
						}
						if(random && world.rand.nextBoolean()) {
							continue;
						}
						world.destroyBlock(destroyPos, true);
					}
				}
			}
			return;
		}

		// Cross Area Harvesting:
		if(this.harvestShape.equalsIgnoreCase("cross")) {
			if(this.harvestRange.getX() > 1) {
				for (int x = pos.getX() - (this.harvestRange.getX() - 1); x < pos.getX() + this.harvestRange.getX(); x++) {
					BlockPos destroyPos = new BlockPos(x, pos.getY(), pos.getZ());
					if (destroyPos.equals(pos) || !this.canHarvestBlock(world.getBlockState(destroyPos))) {
						continue;
					}
					world.destroyBlock(destroyPos, true);
				}
			}

			if(this.harvestRange.getY() > 1) {
				for (int y = pos.getY() - (this.harvestRange.getY() - 1); y < pos.getY() + this.harvestRange.getY(); y++) {
					BlockPos destroyPos = new BlockPos(pos.getX(), y, pos.getZ());
					if (destroyPos.equals(pos) || !this.canHarvestBlock(world.getBlockState(destroyPos))) {
						continue;
					}
					world.destroyBlock(destroyPos, true);
				}
			}

			if(this.harvestRange.getZ() > 1) {
				for (int z = pos.getZ() - (this.harvestRange.getZ() - 1); z < pos.getZ() + this.harvestRange.getZ(); z++) {
					BlockPos destroyPos = new BlockPos(pos.getX(), pos.getY(), z);
					if (destroyPos.equals(pos) || !this.canHarvestBlock(world.getBlockState(destroyPos))) {
						continue;
					}
					world.destroyBlock(destroyPos, true);
				}
			}
		}
	}
}
