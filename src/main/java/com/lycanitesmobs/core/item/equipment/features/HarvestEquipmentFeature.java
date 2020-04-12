package com.lycanitesmobs.core.item.equipment.features;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class HarvestEquipmentFeature extends EquipmentFeature {
	/** List of blocks by harvest type. These are checked after Materials are checked. **/
	private static final Set<Block> SPADE_HARVEST = Sets.newHashSet(Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.GRAY_CONCRETE_POWDER);
	private static final Set<Block> PICKAXE_HARVEST = Sets.newHashSet(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.POWERED_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE);
	private static final Set<Block> AXE_HARVEST = Sets.newHashSet(Blocks.OAK_PLANKS, Blocks.BOOKSHELF, Blocks.CHEST, Blocks.PUMPKIN, Blocks.MELON, Blocks.LADDER, Blocks.OAK_BUTTON, Blocks.OAK_PRESSURE_PLATE);
	private static final Map<Block, BlockState> HOE_LOOKUP = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.getDefaultState(), Blocks.GRASS_PATH, Blocks.FARMLAND.getDefaultState(), Blocks.DIRT, Blocks.FARMLAND.getDefaultState(), Blocks.COARSE_DIRT, Blocks.DIRT.getDefaultState()));

	/** The type of tool to harvest as. Can be: pickaxe, axe, shovel, hoe, sword or shears. **/
	public String harvestType;

	/** The shape of the harvest. Can be block, cross or random. **/
	public String harvestShape = "block";

	/** How much harvest speed to add when harvesting compatible blocks. **/
	public float harvestSpeed = 1;

	/** The level of harvesting. -1 = Unable, 0 = Wood, 1 = Stone, 2 = Iron, 3 = Diamond. **/
	public int harvestLevel = 3;

	/** The additional block range of the harvest shape, relative to the harvesting direction, the central block is not affected by this. X = number of blocks both sides laterally (sideways). Y = Number of blocks vertically. Z = Number of blocks forwards. **/
	public Vec3i harvestRange = new Vec3i(0, 0, 0);


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
	}

	@Override
	public ITextComponent getDescription(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		return new TranslationTextComponent("equipment.feature." + this.featureType).appendText(" ")
				.appendSibling(this.getSummary(itemStack, level));
	}

	@Override
	public ITextComponent getSummary(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		ITextComponent summary = new StringTextComponent(this.harvestType);
		if(this.harvestRange.distanceSq(0, 0, 0, false) > 0) {
			summary.appendText(" (" + this.harvestShape);
			summary.appendText(" " + this.getHarvestRangeString(level));
			summary.appendText(")");
		}
		return summary;
	}

	public String getHarvestRangeString(int level) {
		String harvestRangeString = "" + ((this.harvestRange.getX()) * 2 + 1);
		harvestRangeString += "x" + (this.harvestRange.getY() + 1);
		harvestRangeString += "x" + (this.harvestRange.getZ() + 1);
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
	public ToolType getToolType() {
		return ToolType.get(this.harvestType);
	}

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
	 * @param harvestedBlockState The block state that was destroyed.
	 * @param harvestedPos The position of the destroyed block.
	 * @param livingEntity The entity that destroyed the block.
	 */
	public void onBlockDestroyed(World world, BlockState harvestedBlockState, BlockPos harvestedPos, LivingEntity livingEntity) {
		if(livingEntity == null || livingEntity.isShiftKeyDown()) { // isSneaking()
			return;
		}

		// Get Facing:
		Direction facingH = livingEntity.getHorizontalFacing();
		Direction facingLat = facingH.rotateY();
		Direction facing = facingH;
		if(livingEntity.rotationPitch > 45) {
			facing = Direction.DOWN;
		}
		else if(livingEntity.rotationPitch < -45) {
			facing = Direction.UP;
		}
		Vec3i[][] selectionRanges = new Vec3i[3][2];
		int lon = 0;
		int lat = 1;
		int vert = 2;
		int min = 0;
		int max = 1;

		// Get Longitudinal (Z):
		selectionRanges[lon][min] = new Vec3i(
				Math.min(0, this.harvestRange.getZ() * facing.getXOffset()),
				Math.min(0, this.harvestRange.getZ() * facing.getYOffset()),
				Math.min(0, this.harvestRange.getZ() * facing.getZOffset())
		);
		selectionRanges[lon][max] = new Vec3i(
				Math.max(0, this.harvestRange.getZ() * facing.getXOffset()),
				Math.max(0, this.harvestRange.getZ() * facing.getYOffset()),
				Math.max(0, this.harvestRange.getZ() * facing.getZOffset())
		);

		// Get Lateral (X):
		selectionRanges[lat][min] = new Vec3i(
				this.harvestRange.getX() * -Math.abs(facingLat.getXOffset()),
				this.harvestRange.getX() * -Math.abs(facingLat.getYOffset()),
				this.harvestRange.getX() * -Math.abs(facingLat.getZOffset())
		);
		selectionRanges[lat][max] = new Vec3i(
				this.harvestRange.getX() * Math.abs(facingLat.getXOffset()),
				this.harvestRange.getX() * Math.abs(facingLat.getYOffset()),
				this.harvestRange.getX() * Math.abs(facingLat.getZOffset())
		);

		// Get Vertical (Y):
		if(facing != Direction.DOWN && facing != Direction.UP) {
			int vertOffset = this.harvestRange.getY() != 0 ? -1 : 0;
			selectionRanges[vert][min] = new Vec3i(0, vertOffset, 0);
			selectionRanges[vert][max] = new Vec3i(0, this.harvestRange.getY() + vertOffset, 0);
		}
		else {
			selectionRanges[vert][min] = new Vec3i(
					this.harvestRange.getY() * -Math.abs(facingH.getXOffset()) * 0.5F,
					this.harvestRange.getY() * -Math.abs(facingH.getYOffset()) * 0.5F,
					this.harvestRange.getY() * -Math.abs(facingH.getZOffset()) * 0.5F
			);
			selectionRanges[vert][max] = new Vec3i(
					this.harvestRange.getY() * Math.abs(facingH.getXOffset()) * 0.5F,
					this.harvestRange.getY() * Math.abs(facingH.getYOffset()) * 0.5F,
					this.harvestRange.getY() * Math.abs(facingH.getZOffset()) * 0.5F
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
	public boolean shouldHarvestBlock(World world, BlockState harvestedBlockState, BlockPos harvestedPos, BlockPos targetPos) {
		if(harvestedPos.equals(targetPos)) {
			return false;
		}
		BlockState targetBlockState = world.getBlockState(targetPos);
		if(targetBlockState.getBlock() != harvestedBlockState.getBlock()) {
			return false;
		}
		return this.canHarvestBlock(world.getBlockState(targetPos));
	}

	/**
	 * Called when a player right clicks on a block.
	 * @param context The item use context.
	 */
	public boolean onBlockUsed(ItemUseContext context) {
		if(!"hoe".equals(this.harvestType)) {
			return false;
		}

		int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(context);
		if (hook != 0) return false;
		World world = context.getWorld();
		BlockPos blockPos = context.getPos();
		if (context.getFace() != Direction.DOWN && world.isAirBlock(blockPos.up())) {
			BlockState blockstate = HOE_LOOKUP.get(world.getBlockState(blockPos).getBlock());
			if (blockstate != null) {
				PlayerEntity playerentity = context.getPlayer();
				world.playSound(playerentity, blockPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				if (!world.isRemote) {
					world.setBlockState(blockPos, blockstate, 11);
				}
				return true;
			}
		}

		return false;
	}

	/**
	 * Called when a player right clicks on an entity.
	 * @param player The player using the equipment.
	 * @param entity The entity the player is using the equipment on.
	 * @param itemStack The equipment itemstack.
	 */
	public boolean onEntityInteraction(PlayerEntity player, LivingEntity entity, ItemStack itemStack) {
		if(!"shears".equals(this.harvestType) || player.getEntityWorld().isRemote) {
			return false;
		}

		if (entity instanceof net.minecraftforge.common.IShearable) {
			net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable)entity;
			BlockPos pos = new BlockPos(entity.getPosX(), entity.getPosY(), entity.getPosZ());
			if (target.isShearable(itemStack, entity.world, pos)) {
				java.util.List<ItemStack> drops = target.onSheared(itemStack, entity.world, pos,
						net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantments.FORTUNE, itemStack));
				java.util.Random rand = new java.util.Random();
				drops.forEach(d -> {
					net.minecraft.entity.item.ItemEntity ent = entity.entityDropItem(d, 1.0F);
					ent.setMotion(ent.getMotion().add((double)((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double)(rand.nextFloat() * 0.05F), (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
				});
			}
			return true;
		}

		return false;
	}
}
