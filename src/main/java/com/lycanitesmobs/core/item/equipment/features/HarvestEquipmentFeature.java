package com.lycanitesmobs.core.item.equipment.features;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Set;

public class HarvestEquipmentFeature extends EquipmentFeature {
	/** List of blocks by harvest type. These are checked after Materials are checked. **/
	private static final Set<Block> SPADE_HARVEST = Sets.newHashSet(Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.SNOW, Blocks.SNOW_LAYER, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.CONCRETE_POWDER);
	private static final Set<Block> PICKAXE_HARVEST = Sets.newHashSet(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.DOUBLE_STONE_SLAB, Blocks.GOLDEN_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE);
	private static final Set<Block> AXE_HARVEST = Sets.newHashSet(Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE);

	/** The type of tool to harvest as. Can be: pickaxe, axe, shovel, hoe, sword or shears. **/
	public String harvestType;

	/** The shape of the harvest. Can be block, cross or random. **/
	public String harvestShape = "block";

	/** How much harvest speed to add when harvesting compatible blocks. **/
	public float harvestSpeed = 1;

	/** The level of harvesting. 0 = Wood, 1 = Stone, 2 = Iron, 3 = Diamond. **/
	public int harvestLevel = 3;

	/** The additional block range of the harvest shape, relative to the harvesting direction, the central block is not affected by this. X = number of blocks both sides laterally (sideways). Y = Number of blocks vertically. Z = Number of blocks forwards. **/
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
	public String getDescription(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		String description = LanguageManager.translate("equipment.feature." + this.featureType) + " " + this.harvestType;
		description += "\n" + LanguageManager.translate("equipment.feature.harvest.shape") + " " + this.harvestShape;
		if(this.harvestRange.distanceSq(new Vec3i(0, 0, 0)) > 0) {
			description += "\n" + LanguageManager.translate("equipment.feature.harvest.range") + " " + this.getHarvestRangeString(level);
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
	 * Gets the Tool Type provided by this Harvest Feature.
	 * @return The Tool Type of this feature, will return null if it's not a Pickaxe, Axe or Shovel.
	 */
	@Nullable
	public String getToolType() {
		return this.harvestType;
	}

	/**
	 * Returns if this feature can harvest the provided block or not.
	 * @param blockState
	 * @return
	 */
	public boolean canHarvestBlock(IBlockState blockState) {
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
		if(material == Material.PLANTS || material == Material.VINE) {
			return this.harvestType.equalsIgnoreCase("axe") || this.harvestType.equalsIgnoreCase("sword") || this.harvestType.equalsIgnoreCase("shears");
		}

		// Web and Leaves:
		if(material == Material.WEB || material == Material.LEAVES) {
			return this.harvestType.equalsIgnoreCase("sword") || this.harvestType.equalsIgnoreCase("shears");
		}

		// Dirt:
		if(material == Material.GROUND || material == Material.SAND || material == Material.GRASS || SPADE_HARVEST.contains(block)) {
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
	public float getHarvestSpeed(IBlockState blockState) {
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
	public float getHarvestMultiplier(IBlockState blockState) {
		Material material = blockState.getMaterial();

		// Web:
		if(material == Material.WEB) {
			return 10;
		}

		// Shears:
		if((material == Material.LEAVES || material == Material.VINE) && this.harvestType.equalsIgnoreCase("shears")) {
			return 10;
		}

		return 1;
	}


	/**
	 * Called when a block is destroyed by Equipment with this Feature.
	 * @param world The world where the block was destroyed.
	 * @param harvestedBlockState The block state that was destroyed.
	 * @param harvestedPos The position of the destroyed block.
	 * @param livingEntity The entity that destroyed the block.
	 */
	public void onBlockDestroyed(World world, IBlockState harvestedBlockState, BlockPos harvestedPos, EntityLivingBase livingEntity) {
		if(livingEntity == null || livingEntity.isSneaking()) {
			return;
		}

		// Get Facing:
		EnumFacing facingH = livingEntity.getHorizontalFacing();
		EnumFacing facingLat = facingH.rotateY();
		EnumFacing facing = facingH;
		if(livingEntity.rotationPitch > 45) {
			facing = EnumFacing.DOWN;
		}
		else if(livingEntity.rotationPitch < -45) {
			facing = EnumFacing.UP;
		}
		Vec3i[][] selectionRanges = new Vec3i[3][2];
		int lon = 0;
		int lat = 1;
		int vert = 2;
		int min = 0;
		int max = 1;

		// Get Longitudinal (Z):
		selectionRanges[lon][min] = new Vec3i(
				Math.min(0, this.harvestRange.getZ() * facing.getFrontOffsetX()),
				Math.min(0, this.harvestRange.getZ() * facing.getFrontOffsetY()),
				Math.min(0, this.harvestRange.getZ() * facing.getFrontOffsetZ())
		);
		selectionRanges[lon][max] = new Vec3i(
				Math.max(0, this.harvestRange.getZ() * facing.getFrontOffsetX()),
				Math.max(0, this.harvestRange.getZ() * facing.getFrontOffsetY()),
				Math.max(0, this.harvestRange.getZ() * facing.getFrontOffsetZ())
		);

		// Get Lateral (X):
		selectionRanges[lat][min] = new Vec3i(
				this.harvestRange.getX() * -Math.abs(facingLat.getFrontOffsetX()),
				this.harvestRange.getX() * -Math.abs(facingLat.getFrontOffsetY()),
				this.harvestRange.getX() * -Math.abs(facingLat.getFrontOffsetZ())
		);
		selectionRanges[lat][max] = new Vec3i(
				this.harvestRange.getX() * Math.abs(facingLat.getFrontOffsetX()),
				this.harvestRange.getX() * Math.abs(facingLat.getFrontOffsetY()),
				this.harvestRange.getX() * Math.abs(facingLat.getFrontOffsetZ())
		);

		// Get Vertical (Y):
		if(facing != EnumFacing.DOWN && facing != EnumFacing.UP) {
			int vertOffset = this.harvestRange.getY() != 0 ? -1 : 0;
			selectionRanges[vert][min] = new Vec3i(0, vertOffset, 0);
			selectionRanges[vert][max] = new Vec3i(0, this.harvestRange.getY() + vertOffset, 0);
		}
		else {
			selectionRanges[vert][min] = new Vec3i(
					this.harvestRange.getY() * -Math.abs(facingH.getFrontOffsetX()),
					this.harvestRange.getY() * -Math.abs(facingH.getFrontOffsetY()),
					this.harvestRange.getY() * -Math.abs(facingH.getFrontOffsetZ())
			);
			selectionRanges[vert][max] = new Vec3i(
					this.harvestRange.getY() * Math.abs(facingH.getFrontOffsetX()),
					this.harvestRange.getY() * Math.abs(facingH.getFrontOffsetY()),
					this.harvestRange.getY() * Math.abs(facingH.getFrontOffsetZ())
			);
		}

		// Block and Random Area Harvesting:
		if(this.harvestShape.equalsIgnoreCase("block") || this.harvestShape.equalsIgnoreCase("random")) {
			boolean random = this.harvestShape.equalsIgnoreCase("random");

			// Longitude:
			for (int longX = selectionRanges[lon][min].getX(); longX <= selectionRanges[lon][max].getX(); longX++) {
				for (int longY = selectionRanges[lon][min].getY(); longY <= selectionRanges[lon][max].getY(); longY++) {
					for (int longZ = selectionRanges[lon][min].getZ(); longZ <= selectionRanges[lon][max].getZ(); longZ++) {

						// Latitude:
						for (int latX = selectionRanges[lat][min].getX(); latX <= selectionRanges[lat][max].getX(); latX++) {
							for (int latY = selectionRanges[lat][min].getY(); latY <= selectionRanges[lat][max].getY(); latY++) {
								for (int latZ = selectionRanges[lat][min].getZ(); latZ <= selectionRanges[lat][max].getZ(); latZ++) {

									// Vertical:
									for (int vertX = selectionRanges[vert][min].getX(); vertX <= selectionRanges[vert][max].getX(); vertX++) {
										for (int vertY = selectionRanges[vert][min].getY(); vertY <= selectionRanges[vert][max].getY(); vertY++) {
											for (int vertZ = selectionRanges[vert][min].getZ(); vertZ <= selectionRanges[vert][max].getZ(); vertZ++) {

												BlockPos destroyPos = harvestedPos.add(longX, longY, longZ).add(latX, latY, latZ).add(vertX, vertY, vertZ);
												if (this.shouldHarvestBlock(world, harvestedBlockState, harvestedPos, destroyPos) && (!random || world.rand.nextBoolean())) {
													world.destroyBlock(destroyPos, true);
												}

											}
										}
									}

								}
							}
						}

					}
				}
			}

			return;
		}

		// Cross Area Harvesting:
		if(this.harvestShape.equalsIgnoreCase("cross")) {

			// Longitude:
			for (int longX = selectionRanges[lon][min].getX(); longX <= selectionRanges[lon][max].getX(); longX++) {
				for (int longY = selectionRanges[lon][min].getY(); longY <= selectionRanges[lon][max].getY(); longY++) {
					for (int longZ = selectionRanges[lon][min].getZ(); longZ <= selectionRanges[lon][max].getZ(); longZ++) {

						// Latitude:
						for (int latX = selectionRanges[lat][min].getX(); latX <= selectionRanges[lat][max].getX(); latX++) {
							for (int latY = selectionRanges[lat][min].getY(); latY <= selectionRanges[lat][max].getY(); latY++) {
								for (int latZ = selectionRanges[lat][min].getZ(); latZ <= selectionRanges[lat][max].getZ(); latZ++) {
									BlockPos destroyPos = harvestedPos.add(longX, longY, longZ).add(latX, latY, latZ);
									if (this.shouldHarvestBlock(world, harvestedBlockState, harvestedPos, destroyPos)) {
										world.destroyBlock(destroyPos, true);
									}
								}
							}
						}

						// Vertical:
						for (int vertX = selectionRanges[vert][min].getX(); vertX <= selectionRanges[vert][max].getX(); vertX++) {
							for (int vertY = selectionRanges[vert][min].getY(); vertY <= selectionRanges[vert][max].getY(); vertY++) {
								for (int vertZ = selectionRanges[vert][min].getZ(); vertZ <= selectionRanges[vert][max].getZ(); vertZ++) {
									BlockPos destroyPos = harvestedPos.add(longX, longY, longZ).add(vertX, vertY, vertZ);
									if (this.shouldHarvestBlock(world, harvestedBlockState, harvestedPos, destroyPos)) {
										world.destroyBlock(destroyPos, true);
									}
								}
							}
						}

					}
				}
			}
		}
	}

	/**
	 * Returns if the target block position should be area harvested.
	 * @param world The world to check in.
	 * @param harvestedBlockState The initial block harvested to compare to.
	 * @param harvestedPos The initial block position that was harvested at.
	 * @param targetPos The target area harvesting position to check.
	 * @return True if the block should be area harvested.
	 */
	public boolean shouldHarvestBlock(World world, IBlockState harvestedBlockState, BlockPos harvestedPos, BlockPos targetPos) {
		if(harvestedPos.equals(targetPos)) {
			return false;
		}
		IBlockState targetBlockState = world.getBlockState(targetPos);
		if(targetBlockState.getBlock() != harvestedBlockState.getBlock()) {
			return false;
		}
		return this.canHarvestBlock(world.getBlockState(targetPos));
	}
}
