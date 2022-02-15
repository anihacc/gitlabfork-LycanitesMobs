package com.lycanitesmobs.core.item.equipment.features;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;


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
	public String getDescription(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		return LanguageManager.translate("equipment.feature." + this.featureType) + " " + this.getSummary(itemStack, level);
	}

	@Override
	public String getSummary(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		String summary = LanguageManager.translate("equipment.harvest.type." + this.harvestType);
		if(this.harvestRange.distanceSq(new Vec3i(0, 0, 0)) > 0) {
			summary +=  " (" + LanguageManager.translate("equipment.harvest.shape." + this.harvestShape);
			summary += " " + this.getHarvestRangeString(level);
			summary += ")";
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
	
	public String getToolType() {
		return this.harvestType;
	}

	/**
	 * Returns if this feature can harvest the provided block or not.
	 * @param blockState The blockstate to check.
	 * @return True if the block can be destroyed by this feature.
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

		// Wool:
		if(material == Material.CLOTH) {
			return this.harvestType.equalsIgnoreCase("shears");
		}

		// Dirt:
		if(material == Material.GROUND || material == Material.SAND || material == Material.GRASS || material == Material.SNOW || material == Material.CRAFTED_SNOW || SPADE_HARVEST.contains(block)) {
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
					this.harvestRange.getY() * -Math.abs(facingH.getFrontOffsetX()) * 0.5F,
					this.harvestRange.getY() * -Math.abs(facingH.getFrontOffsetY()) * 0.5F,
					this.harvestRange.getY() * -Math.abs(facingH.getFrontOffsetZ()) * 0.5F
			);
			selectionRanges[vert][max] = new Vec3i(
					this.harvestRange.getY() * Math.abs(facingH.getFrontOffsetX()) * 0.5F,
					this.harvestRange.getY() * Math.abs(facingH.getFrontOffsetY()) * 0.5F,
					this.harvestRange.getY() * Math.abs(facingH.getFrontOffsetZ()) * 0.5F
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
		if(targetBlockState.getBlock() == Blocks.REDSTONE_ORE || targetBlockState.getBlock() == Blocks.LIT_REDSTONE_ORE) {
			if(harvestedBlockState.getBlock() == Blocks.REDSTONE_ORE || harvestedBlockState.getBlock() == Blocks.LIT_REDSTONE_ORE) {
				return this.canHarvestBlock(world.getBlockState(targetPos));
			}
		}
		if(targetBlockState.getBlock() != harvestedBlockState.getBlock()) {
			return false;
		}

		// Don't area harvest Tile Entities.
		if (world.getTileEntity(targetPos) != null) {
			return false;
		}

		return this.canHarvestBlock(world.getBlockState(targetPos));
	}

	/**
	 * Called when a player right clicks on a block.
	 * @param world The world the player is in.
	 * @param player The player using the equipment.
	 * @param pos The blockPos used at.
	 * @param itemStack The equipment itemstack.
	 * @param facing The use facing direction.
	 */
	public boolean onBlockUsed(World world, EntityPlayer player, BlockPos pos, ItemStack itemStack, EnumFacing facing) {
		if(!"hoe".equals(this.harvestType)) {
			return false;
		}

		if (!player.canPlayerEdit(pos.offset(facing), facing, itemStack)) {
			return false;
		}

		IBlockState blockState = world.getBlockState(pos);
		int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(itemStack, player, world, pos);
		if (hook != 0) return false;
		IBlockState iblockstate = world.getBlockState(pos);
		Block block = iblockstate.getBlock();
		if (facing != EnumFacing.DOWN && world.isAirBlock(pos.up())) {
			if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
				this.hoeBlock(itemStack, player, world, pos, Blocks.FARMLAND.getDefaultState());
				return true;
			}
			if (block == Blocks.DIRT) {
				switch ((BlockDirt.DirtType)iblockstate.getValue(BlockDirt.VARIANT)) {
					case DIRT:
						this.hoeBlock(itemStack, player, world, pos, Blocks.FARMLAND.getDefaultState());
						return true;
					case COARSE_DIRT:
						this.hoeBlock(itemStack, player, world, pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
						return true;
				}
			}
		}

		return false;
	}
	protected void hoeBlock(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, IBlockState state) {
		worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
		if (!worldIn.isRemote) {
			worldIn.setBlockState(pos, state, 11);
		}
	}

	/**
	 * Called when a player right clicks on an entity.
	 * @param player The player using the equipment.
	 * @param entity The entity the player is using the equipment on.
	 * @param itemStack The equipment itemstack.
	 */
	public boolean onEntityInteraction(EntityPlayer player, EntityLivingBase entity, ItemStack itemStack) {
		if(!"shears".equals(this.harvestType) || player.getEntityWorld().isRemote) {
			return false;
		}

		if (entity instanceof net.minecraftforge.common.IShearable) {
			net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable)entity;
			BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
			if (target.isShearable(itemStack, entity.world, pos)) {
				java.util.List<ItemStack> drops = target.onSheared(itemStack, entity.world, pos,
						net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.FORTUNE, itemStack));
				java.util.Random rand = new java.util.Random();
				for(ItemStack stack : drops) {
					net.minecraft.entity.item.EntityItem ent = entity.entityDropItem(stack, 1.0F);
					ent.motionY += rand.nextFloat() * 0.05F;
					ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
					ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
				}
			}
			return true;
		}

		return false;
	}
}
