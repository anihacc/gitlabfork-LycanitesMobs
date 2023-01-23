package com.lycanitesmobs.core.dungeon.instance;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.block.effect.BlockFrostCloud;
import com.lycanitesmobs.core.block.effect.BlockPoisonCloud;
import com.lycanitesmobs.core.dungeon.definition.DungeonSector;
import com.lycanitesmobs.core.dungeon.definition.DungeonTheme;
import com.lycanitesmobs.core.dungeon.definition.SectorLayer;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.spawner.MobSpawn;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SectorInstance {
	/** Sector Instances makeup an entire Dungeon Layout. **/

	/** The Dungeon Layout that this instance belongs to. **/
	public DungeonLayout layout;

	/** The Dungeon Sector that this instance is using. **/
	public DungeonSector dungeonSector;

	/** The connector that this sector is connected to, cannot be null. **/
	public SectorConnector parentConnector;

	/** A list of connectors that this sector provides to connect to other sectors with. **/
	public List<SectorConnector> connectors = new ArrayList<>();

	/** The room size of this Sector Instance, this includes the inside and inner floor, walls and ceiling. Used for building and sector to sector collision. **/
	protected Vec3i roomSize;

	/** The occupied size of this Sector Instance, includes the room size plus additional space taken up by sector layers, structures, padding, etc. **/
	protected Vec3i occupiedSize;

	/** The theme this Sector Instance is using. **/
	public DungeonTheme theme;

	/** The random light block for this sector instance to use. **/
	public IBlockState lightBlock;

	/** The random torch block for this sector instance to use. **/
	public IBlockState torchBlock;

	/** The random stairs block for this sector instance to use. **/
	public IBlockState stairBlock;

	/** The random pit block for this sector instance to use. **/
	public IBlockState pitBlock;

	public IBlockState airBlock;

	/** How many chunks this sector has been built into. When this equals the total chunks this sector occupies it is considered fully built. **/
	public int chunksBuilt = 0;


	/**
	 * Constructor
	 * @param layout The Dungeon Layout to create this instance for.
	 * @param dungeonSector The Dungeon Sector to create this instance from.
	 * @param random The instance of Random to use.
	 */
	public SectorInstance(DungeonLayout layout, DungeonSector dungeonSector, Random random) {
		this.layout = layout;
		this.dungeonSector = dungeonSector;

		// Size:
		this.roomSize = this.dungeonSector.getRandomSize(random);
		this.occupiedSize = new Vec3i(
				this.roomSize.getX() + Math.max(1, this.dungeonSector.padding.getX()),
				this.roomSize.getY() + this.dungeonSector.padding.getY(),
				this.roomSize.getZ() + Math.max(1, this.dungeonSector.padding.getZ())
		);

		// Structures:
		// TODO Structures
	}


	/**
	 * Connects this sector to the provided connector. Should be called before init.
	 * @param parentConnector The connector that this sector is connecting from.
	 */
	public void connect(SectorConnector parentConnector) {
		this.parentConnector = parentConnector;
	}


	/**
	 * Initialises this Sector Instance. Must be connected to a parent connector.
	 * @param random The instance of Random to use.
	 */
	public void init(Random random) {
		if(this.parentConnector == null) {
			throw new RuntimeException("[Dungeon] Tried to initialise a Sector Instance with a null Parent Connector: " + this);
		}

		// Close Parent Connector:
		this.parentConnector.childSector = this;
		this.parentConnector.closed = true;
		this.layout.openConnectors.remove(this.parentConnector);

		// Theme:
		if(this.dungeonSector.changeTheme || this.parentConnector.parentSector == null) {
			this.theme = this.layout.dungeonInstance.schematic.getRandomTheme(random);
		}
		else {
			this.theme = this.parentConnector.parentSector.theme;
		}
		this.lightBlock = this.theme.getLight('B', random);
		this.torchBlock = this.theme.getTorch('B', random);
		this.stairBlock = this.theme.getStairs('B', random);
		this.pitBlock = this.theme.getPit('B', random);
		this.airBlock = this.theme.getAir('B', random);

		// Create Child Connectors:
		BlockPos boundsMin = this.getRoomBoundsMin();
		BlockPos boundsMax = this.getRoomBoundsMax();
		Vec3i size = this.getRoomSize();
		int centerX = boundsMin.getX() + Math.round((float)size.getX() / 2);
		int centerZ = boundsMin.getZ() + Math.round((float)size.getZ() / 2);

		// Upper Exit:
		int upperConnectorY = this.parentConnector.position.getY() + this.getRoomSize().getY();
		if(upperConnectorY < 255) {
			BlockPos blockPos = new BlockPos(centerX, upperConnectorY, centerZ);
			this.addConnector(blockPos, this.parentConnector.level + 1, EnumFacing.UP);
		}

		if("corridor".equalsIgnoreCase(this.dungeonSector.type) || "room".equalsIgnoreCase(this.dungeonSector.type) || "tower".equalsIgnoreCase(this.dungeonSector.type) || "entrance".equalsIgnoreCase(this.dungeonSector.type) || "bossRoom".equalsIgnoreCase(this.dungeonSector.type)) {

			// Front/Back Exit:
			BlockPos frontPos = this.parentConnector.position;
			EnumFacing frontFacing = EnumFacing.SOUTH;
			BlockPos backPos = this.parentConnector.position;
			EnumFacing backFacing = EnumFacing.NORTH;
			if(this.parentConnector.facing == EnumFacing.SOUTH || this.parentConnector.facing == EnumFacing.UP) {
				frontPos = new BlockPos(this.getConnectorOffset(random, size.getX(), boundsMin.getX()), this.parentConnector.position.getY(), boundsMax.getZ() + 1);
				frontFacing = EnumFacing.SOUTH;
				backPos = new BlockPos(this.getConnectorOffset(random, size.getX(), boundsMin.getX()), this.parentConnector.position.getY(), boundsMin.getZ() - 1);
				backFacing = EnumFacing.NORTH;
			}
			else if(this.parentConnector.facing == EnumFacing.EAST) {
				frontPos = new BlockPos(boundsMax.getX() + 1, this.parentConnector.position.getY(), this.getConnectorOffset(random, size.getZ(), boundsMin.getZ()));
				frontFacing = EnumFacing.EAST;
				backPos = new BlockPos(boundsMin.getX() - 1, this.parentConnector.position.getY(), this.getConnectorOffset(random, size.getZ(), boundsMin.getZ()));
				backFacing = EnumFacing.WEST;
			}
			else if(this.parentConnector.facing == EnumFacing.NORTH) {
				frontPos = new BlockPos(this.getConnectorOffset(random, size.getX(), boundsMin.getX()), this.parentConnector.position.getY(), boundsMin.getZ() - 1);
				frontFacing = EnumFacing.NORTH;
				backPos = new BlockPos(this.getConnectorOffset(random, size.getX(), boundsMin.getX()), this.parentConnector.position.getY(), boundsMax.getZ() + 1);
				backFacing = EnumFacing.SOUTH;
			}
			else if(this.parentConnector.facing == EnumFacing.WEST) {
				frontPos = new BlockPos(boundsMin.getX() - 1, this.parentConnector.position.getY(), this.getConnectorOffset(random, size.getZ(), boundsMin.getZ()));
				frontFacing = EnumFacing.WEST;
				backPos = new BlockPos(boundsMax.getX() + 1, this.parentConnector.position.getY(), this.getConnectorOffset(random, size.getZ(), boundsMin.getZ()));
				backFacing = EnumFacing.EAST;
			}
			this.addConnector(frontPos, this.parentConnector.level, frontFacing);
			if("tower".equalsIgnoreCase(this.dungeonSector.type)) {
				this.addConnector(backPos, this.parentConnector.level, backFacing);
			}

			// Side Exits:
			if("room".equalsIgnoreCase(this.dungeonSector.type) || "tower".equalsIgnoreCase(this.dungeonSector.type)) {
				BlockPos leftPos = this.parentConnector.position;
				EnumFacing leftFacing = EnumFacing.WEST;
				BlockPos rightPos = this.parentConnector.position;
				EnumFacing rightFacing = EnumFacing.EAST;
				if(this.parentConnector.facing == EnumFacing.SOUTH || this.parentConnector.facing == EnumFacing.NORTH || this.parentConnector.facing == EnumFacing.UP) {
					leftPos = new BlockPos(boundsMin.getX() - 1, this.parentConnector.position.getY(), this.getConnectorOffset(random, size.getZ(), boundsMin.getZ()));
					leftFacing = EnumFacing.WEST;
					rightPos = new BlockPos(boundsMax.getX() + 1, this.parentConnector.position.getY(), this.getConnectorOffset(random, size.getZ(), boundsMin.getZ()));
					rightFacing = EnumFacing.EAST;
				}
				else if(this.parentConnector.facing == EnumFacing.EAST || this.parentConnector.facing == EnumFacing.WEST) {
					leftPos = new BlockPos(this.getConnectorOffset(random, size.getX(), boundsMin.getX()), this.parentConnector.position.getY(), boundsMax.getZ() + 1);
					leftFacing = EnumFacing.SOUTH;
					rightPos = new BlockPos(this.getConnectorOffset(random, size.getX(), boundsMin.getX()), this.parentConnector.position.getY(), boundsMin.getZ() - 1);
					rightFacing = EnumFacing.NORTH;
				}
				this.addConnector(leftPos, this.parentConnector.level, leftFacing);
				this.addConnector(rightPos, this.parentConnector.level, rightFacing);
			}
		}
		if("stairs".equalsIgnoreCase(this.dungeonSector.type)) {

			// Lower Exit:
			int y = this.parentConnector.position.getY() - (size.getY() * 2);
			if(y > 0) {
				BlockPos blockPos = new BlockPos(centerX, y, boundsMax.getZ() + 1);
				if (this.parentConnector.facing == EnumFacing.EAST) {
					blockPos = new BlockPos(boundsMax.getX() + 1, y, centerZ);
				}
				else if (this.parentConnector.facing == EnumFacing.NORTH) {
					blockPos = new BlockPos(centerX, y, boundsMin.getZ() - 1);
				}
				else if (this.parentConnector.facing == EnumFacing.WEST) {
					blockPos = new BlockPos(boundsMin.getX() - 1, y, centerZ);
				}
				this.addConnector(blockPos, this.parentConnector.level - 1, this.parentConnector.facing);
			}
		}

		//LycanitesMobs.logDebug("Dungeon", "Initialised Sector Instance - Bounds: " + this.getOccupiedBoundsMin() + " to " + this.getOccupiedBoundsMax());
	}


	/**
	 * Gets a random offset for positioning a sector connector.
	 * @param random The instance of random to use.
	 * @param length The length to get a random offset from such as the x/z size of the sector instance.
	 * @param start The world position to add the offset to.
	 * @return
	 */
	public int getConnectorOffset(Random random, int length, int start) {
		int entrancePadding = 2;
		if(!"room".equalsIgnoreCase(this.dungeonSector.type) || length <= entrancePadding * 2) {
			return start + Math.round((float)length / 2);
		}
		return start + entrancePadding + random.nextInt(length - (entrancePadding * 2)) + 1;
	}


	/**
	 * Adds a new child Sector Connector to this Sector Instance.
	 * @param blockPos The position of the connector.
	 * @param level The level that the connector is on.
	 * @param facing The facing of the sector.
	 * @return The newly created Sector Connector.
	 */
	public SectorConnector addConnector(BlockPos blockPos, int level, EnumFacing facing) {
		SectorConnector connector = new SectorConnector(blockPos, this, level, facing);
		this.connectors.add(connector);
		return connector;
	}


	/**
	 * Returns a random child connector for a Sector Instance to connect to.
	 * @param random The instance of Random to use.
	 * @return A random connector.
	 */
	public SectorConnector getRandomConnector(Random random, SectorInstance sectorInstance) {
		List<SectorConnector> openConnectors = this.getOpenConnectors(sectorInstance);
		if(openConnectors.isEmpty()) {
			return null;
		}
		if(openConnectors.size() == 1) {
			return openConnectors.get(0);
		}
		return openConnectors.get(random.nextInt(openConnectors.size()));
	}


	/**
	 * Returns a list of open connectors where they are not set to closed and have no child Sector Instance connected.
	 * @param sectorInstance The sector to get the open connectors for. If null, collision checks are skipped.
	 * @return A list of open connectors.
	 */
	public List<SectorConnector> getOpenConnectors(SectorInstance sectorInstance) {
		List<SectorConnector> openConnectors = new ArrayList<>();
		for(SectorConnector connector : this.connectors) {
			if(connector.canConnect(this.layout, sectorInstance)) {
				openConnectors.add(connector);
			}
		}
		return openConnectors;
	}


	/**
	 * Returns a list of every ChunkPos that this Sector Instance occupies.
	 * Applies an offset to occupied bounds for generating a chunk position.
	 * @return A list of ChunkPos.
	 */
	public List<ChunkPos> getChunkPositions() {
		ChunkPos minChunkPos = new ChunkPos(this.getOccupiedBoundsMin().add(-9, 0, -9));
		ChunkPos maxChunkPos = new ChunkPos(this.getOccupiedBoundsMax().add(-7, 0, -7));
		List<ChunkPos> chunkPosList = new ArrayList<>();
		for(int x = minChunkPos.x; x <= maxChunkPos.x; x++) {
			for(int z = minChunkPos.z; z <= maxChunkPos.z; z++) {
				chunkPosList.add(new ChunkPos(x, z));
			}
		}
		return chunkPosList;
	}


	/**
	 * Returns a list of other sectors near this sector instance.
	 * @return A list of nearby sector instances.
	 */
	public List<SectorInstance> getNearbySectors() {
		List<SectorInstance> nearbySectors = new ArrayList<>();
		for(SectorInstance nearbySector : this.layout.sectors) {
			if(!nearbySectors.contains(nearbySector)) {
				nearbySectors.add(nearbySector);
			}
		}
		/*for(ChunkPos chunkPos : this.getChunkPositions()) {
			if(this.layout.sectorChunkMap.containsKey(chunkPos)) {
				for(SectorInstance nearbySector : this.layout.sectorChunkMap.get(chunkPos)) {
					if(!nearbySectors.contains(nearbySector)) {
						nearbySectors.add(nearbySector);
					}
				}
			}
		}*/
		return nearbySectors;
	}


	/**
	 * Returns true if this sector instance collides with the provided sector instance.
	 * @param sectorInstance The sector instance to check for collision with.
	 * @return True on collision.
	 */
	public boolean collidesWith(SectorInstance sectorInstance) {
		if(sectorInstance == this || sectorInstance == this.parentConnector.parentSector) {
			return false;
		}

		BlockPos boundsMin = this.getOccupiedBoundsMin();
		BlockPos boundsMax = this.getOccupiedBoundsMax();
		BlockPos targetMin = sectorInstance.getOccupiedBoundsMin();
		BlockPos targetMax = sectorInstance.getOccupiedBoundsMax();

		if(boundsMin.getY() != targetMin.getY() && !sectorInstance.dungeonSector.type.equals("stairs")) {
			return false; // Temporary simple y collision check (y doesn't take negative layer padding into account yet).
		}

		boolean withinX = boundsMin.getX() >= targetMin.getX() && boundsMin.getX() <= targetMax.getX();
		if(!withinX)
			withinX = boundsMax.getX() >= targetMin.getX() && boundsMax.getX() <= targetMax.getX();
		if(!withinX)
			return false;

//		boolean withinY = boundsMin.getY() >= targetMin.getY() && boundsMin.getY() <= targetMax.getY();
//		if(!withinY)
//			withinY = boundsMax.getY() >= targetMin.getY() && boundsMax.getY() <= targetMax.getY();
//		if(!withinY)
//			return false;

		boolean withinZ = boundsMin.getZ() >= targetMin.getZ() && boundsMin.getZ() <= targetMax.getZ();
		if(!withinZ)
			withinZ = boundsMax.getZ() >= targetMin.getZ() && boundsMax.getZ() <= targetMax.getZ();
		if(!withinZ)
			return false;

		return true;
	}


	/**
	 * Returns the room size of this sector. X and Z are swapped when facing EAST or WEST.
	 * This is how large the room to be built is excluding extra blocks added for layers or structures, etc.
	 * Used for building this sector.
	 * @return A vector of the room size.
	 */
	public Vec3i getRoomSize() {
		if(this.parentConnector.facing == EnumFacing.EAST || this.parentConnector.facing == EnumFacing.WEST) {
			return new Vec3i(this.roomSize.getZ(), this.roomSize.getY(), this.roomSize.getX());
		}
		return this.roomSize;
	}


	/**
	 * Returns the collision size of this sector. X and Z are swapped when facing EAST or WEST.
	 * This is how large this sector is including extra blocks added for layers or structures, etc.
	 * Used for detecting what chunks this sector needs to generate in and sector collision detection.
	 * @return A vector of the collision size.
	 */
	public Vec3i getOccupiedSize() {
		if(this.parentConnector.facing == EnumFacing.EAST || this.parentConnector.facing == EnumFacing.WEST) {
			return new Vec3i(this.occupiedSize.getZ(), this.occupiedSize.getY(), this.occupiedSize.getX());
		}
		return this.occupiedSize;
	}


	/**
	 * Returns the minimum xyz position that this Sector Instance from the provided bounds size.
	 * @param boundsSize The xyz size to use when calculating bounds.
	 * @return The minimum bounds position (corner).
	 */
	public BlockPos getBoundsMin(Vec3i boundsSize) {
		BlockPos bounds = new BlockPos(this.parentConnector.position);
		if(this.parentConnector.facing == EnumFacing.UP) {
			bounds = bounds.add(
					-(int)Math.ceil((double)boundsSize.getX() / 2),
					0,
					-(int)Math.ceil((double)boundsSize.getZ() / 2)
			);
		}
		else if(this.parentConnector.facing == EnumFacing.SOUTH) {
			bounds = bounds.add(
					-(int)Math.ceil((double)boundsSize.getX() / 2),
					0,
					0
			);
		}
		else if(this.parentConnector.facing == EnumFacing.EAST) {
			bounds = bounds.add(
					0,
					0,
					-(int)Math.ceil((double)boundsSize.getZ() / 2)
			);
		}
		else if(this.parentConnector.facing == EnumFacing.NORTH) {
			bounds = bounds.add(
					-(int)Math.ceil((double)boundsSize.getX() / 2),
					0,
					-boundsSize.getZ()
			);
		}
		else if(this.parentConnector.facing == EnumFacing.WEST) {
			bounds = bounds.add(
					-boundsSize.getX(),
					0,
					-(int)Math.ceil((double)boundsSize.getZ() / 2)
			);
		}

		return bounds;
	}


	/**
	 * Returns the maximum xyz position that this Sector Instance from the provided bounds size.
	 * @param boundsSize The xyz size to use when calculating bounds.
	 * @return The maximum bounds position (corner).
	 */
	public BlockPos getBoundsMax(Vec3i boundsSize) {
		BlockPos bounds = new BlockPos(this.parentConnector.position);
		if(this.parentConnector.facing == EnumFacing.UP) {
			bounds = bounds.add(
					(int)Math.ceil((double)boundsSize.getX() / 2),
					boundsSize.getY(),
					(int)Math.ceil((double)boundsSize.getZ() / 2)
			);
		}
		else if(this.parentConnector.facing == EnumFacing.SOUTH) {
			bounds = bounds.add(
					(int)Math.floor((double)boundsSize.getX() / 2),
					boundsSize.getY(),
					boundsSize.getZ()
			);
		}
		else if(this.parentConnector.facing == EnumFacing.EAST) {
			bounds = bounds.add(
					boundsSize.getX(),
					boundsSize.getY(),
					(int)Math.floor((double)boundsSize.getZ() / 2)
			);
		}
		else if(this.parentConnector.facing == EnumFacing.NORTH) {
			bounds = bounds.add(
					(int)Math.floor((double)boundsSize.getX() / 2),
					boundsSize.getY(),
					0
			);
		}
		else if(this.parentConnector.facing == EnumFacing.WEST) {
			bounds = bounds.add(
					0,
					boundsSize.getY(),
					(int)Math.floor((double)boundsSize.getZ() / 2)
			);
		}
		return bounds;
	}


	/**
	 * Returns the minimum xyz position that this Sector Instance occupies.
	 * @return The minimum bounds position (corner).
	 */
	public BlockPos getOccupiedBoundsMin() {
		BlockPos occupiedBoundsMin = this.getBoundsMin(this.getOccupiedSize());
		if("stairs".equals(this.dungeonSector.type)) {
			occupiedBoundsMin = occupiedBoundsMin.subtract(new Vec3i(0, this.getRoomSize().getY() * 2, 0));
		}
		return occupiedBoundsMin;
	}


	/**
	 * Returns the maximum xyz position that this Sector Instance occupies.
	 * @return The maximum bounds position (corner).
	 */
	public BlockPos getOccupiedBoundsMax() {
		return this.getBoundsMax(this.getOccupiedSize());
	}


	/**
	 * Returns the minimum xyz position that this Sector Instance builds from.
	 * @return The minimum bounds position (corner).
	 */
	public BlockPos getRoomBoundsMin() {
		return this.getBoundsMin(this.getRoomSize());
	}


	/**
	 * Returns the maximum xyz position that this Sector Instance builds to.
	 * @return The maximum bounds position (corner).
	 */
	public BlockPos getRoomBoundsMax() {
		return this.getBoundsMax(this.getRoomSize());
	}


	/**
	 * Returns the rounded center block position of this sector.
	 * @return The center block position.
	 */
	public BlockPos getCenter() {
		BlockPos startPos = this.getRoomBoundsMin();
		BlockPos stopPos = this.getRoomBoundsMax();

		Vec3i size = this.getRoomSize();
		int centerX = startPos.getX() + Math.round((float)size.getX() / 2);
		int centerZ = startPos.getZ() + Math.round((float)size.getZ() / 2);

		return new BlockPos(centerX, startPos.getY(), centerZ);
	}


	/**
	 * Places a block state in the world from this sector.
	 * @param world The world to place a block in.
	 * @param chunkPos The chunk position to build within.
	 * @param blockPos The position to place the block at.
	 * @param blockState The block state to place.
	 * @param random The instance of random, used for random mob spawns or loot on applicable blocks, etc.
	 */
	public void placeBlock(World world, ChunkPos chunkPos, BlockPos blockPos, IBlockState blockState, EnumFacing facing, Random random) {
		// Restrict To Chunk Position:
		int chunkOffset = 8;
		if(blockPos.getX() < chunkPos.getXStart() + chunkOffset || blockPos.getX() > chunkPos.getXEnd() + chunkOffset) {
			return;
		}
		if(blockPos.getY() <= 0 || blockPos.getY() >= world.getHeight()) {
			return;
		}
		if(blockPos.getZ() < chunkPos.getZStart() + chunkOffset || blockPos.getZ() > chunkPos.getZEnd() + chunkOffset) {
			return;
		}


		// Block State and Flags:
		int flags = 3;

		// Torch:
		if(blockState.getBlock() == Blocks.TORCH) {
			blockState = blockState.withProperty(BlockTorch.FACING, facing);
			flags = 0;
		}

		// Chest:
		if(blockState.getBlock() == Blocks.CHEST) {
			blockState = blockState.withProperty(BlockChest.FACING, facing);
		}

		// Don't Update:
		if(blockState.getBlock() == Blocks.AIR || blockState.getBlock() instanceof BlockFluidBase ||
				blockState.getBlock() instanceof BlockFire || blockState.getBlock() instanceof BlockFireBase ||
				blockState.getBlock() instanceof BlockPoisonCloud || blockState.getBlock() instanceof BlockFrostCloud) {
			flags = 0;
		}


		// Set The Block:
		world.setBlockState(blockPos, blockState, flags);


		// Tile Entities:

		// Spawner:
		if(blockState.getBlock() == Blocks.MOB_SPAWNER) {
			TileEntity tileEntity = world.getTileEntity(blockPos);
			if(tileEntity != null && tileEntity instanceof TileEntityMobSpawner) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)tileEntity;
				MobSpawn mobSpawn = this.layout.dungeonInstance.schematic.getRandomMobSpawn(this.parentConnector.level, false, random);
				if(mobSpawn != null) {
					ResourceLocation entityId = EntityList.getKey(mobSpawn.entityClass);
					if (entityId != null) {
						spawner.getSpawnerBaseLogic().setEntityId(entityId);
					}
				}
			}
			return;
		}

		// Chest:
		if(blockState.getBlock() == Blocks.CHEST) {
			TileEntity tileEntity = world.getTileEntity(blockPos);
			if(tileEntity instanceof TileEntityChest) {

				// Apply Loot Table:
				TileEntityChest chest = (TileEntityChest)tileEntity;
				ResourceLocation lootTable = this.layout.dungeonInstance.schematic.getRandomLootTable(this.parentConnector.level, random);
				if(lootTable != null) {
					chest.setLootTable(lootTable, Objects.hash(blockPos.hashCode(), random));
				}

				// Add Specific Items:
				// TODO Add a random amount of additional specific items.
			}
		}
	}


	/**
	 * Builds this sector. Wont build at y level 0 or below, beyond world height or outside of the chunk.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 */
	public void build(World world, ChunkPos chunkPos, Random random) {
		this.clearArea(world, chunkPos, random);
		this.buildFloor(world, chunkPos, random, 0);
		this.buildWalls(world, chunkPos, random);
		this.buildCeiling(world, chunkPos, random);
		if("stairs".equalsIgnoreCase(this.dungeonSector.type)) {
			this.buildStairs(world, chunkPos, random);
			this.buildFloor(world, chunkPos, random, -(this.getRoomSize().getY() * 2));
		}
		if("tower".equalsIgnoreCase(this.dungeonSector.type)) {
			this.buildStairs(world, chunkPos, random);
		}
		this.buildEntrances(world, chunkPos, random);
		this.chunksBuilt++;
		if("bossRoom".equalsIgnoreCase(this.dungeonSector.type)) {
			MobSpawn mobSpawn = this.layout.dungeonInstance.schematic.getRandomMobSpawn(this.parentConnector.level, true, random);
			if(mobSpawn != null) {
				this.spawnMob(world, chunkPos, this.getCenter().add(0, 1, 0), mobSpawn, random);
			}
		}
	}


	/**
	 * Sets the area of this sector to air for building in from within the chunk position.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 */
	public void clearArea(World world, ChunkPos chunkPos, Random random) {
		// Get Start and Stop Positions:
		BlockPos startPos = this.getRoomBoundsMin();
		BlockPos stopPos = this.getRoomBoundsMax();
		int startX = Math.min(startPos.getX(), stopPos.getX());
		int stopX = Math.max(startPos.getX(), stopPos.getX());
		int startY = Math.min(startPos.getY(), stopPos.getY());
		int stopY = Math.max(startPos.getY(), stopPos.getY());
		int startZ = Math.min(startPos.getZ(), stopPos.getZ());
		int stopZ = Math.max(startPos.getZ(), stopPos.getZ());

		if("stairs".equalsIgnoreCase(this.dungeonSector.type)) {
			startY = Math.max(1, startPos.getY() - (this.getRoomSize().getY() * 2));
		}

		final BlockPos.MutableBlockPos buildPos = new BlockPos.MutableBlockPos();
		for(int x = startX; x <= stopX; x++) {
			for(int y = startY; y <= stopY; y++) {
				for(int z = startZ; z <= stopZ; z++) {
					buildPos.setPos(x, y, z);
					IBlockState blockState = this.airBlock;
					if(blockState != null)
						this.placeBlock(world, chunkPos, buildPos, blockState, EnumFacing.SOUTH, random);
				}
			}
		}
	}


	/**
	 * Builds the floor of this sector from within the chunk position.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 * @param offsetY The Y offset to build the floor at, useful for multiple floor sectors.
	 */
	public void buildFloor(World world, ChunkPos chunkPos, Random random, int offsetY) {
		// Get Start and Stop Positions:
		BlockPos startPos = this.getRoomBoundsMin().add(0, offsetY, 0);
		BlockPos stopPos = this.getRoomBoundsMax().add(0, offsetY, 0);
		int startX = Math.min(startPos.getX(), stopPos.getX());
		int stopX = Math.max(startPos.getX(), stopPos.getX());
		int startY = Math.min(startPos.getY(), stopPos.getY());
		int startZ = Math.min(startPos.getZ(), stopPos.getZ());
		int stopZ = Math.max(startPos.getZ(), stopPos.getZ());

		final BlockPos.MutableBlockPos buildPos = new BlockPos.MutableBlockPos();
		for(int layerIndex : this.dungeonSector.floor.layers.keySet()) {
			int y = startY + layerIndex;
			if(y <= 0 || y >= world.getHeight()) {
				continue;
			}
			SectorLayer layer = this.dungeonSector.floor.layers.get(layerIndex);
			for(int x = startX; x <= stopX; x++) {
				List<Character> row = layer.getRow(x - startX, stopX - startX);
				for(int z = startZ; z <= stopZ; z++) {
					char buildChar = layer.getColumn(x - startX, stopX - startX, z - startZ, stopZ - startZ, row);
					buildPos.setPos(x, y, z);
					IBlockState blockState = this.theme.getFloor(this, buildChar, random);
					if(blockState != null)
						this.placeBlock(world, chunkPos, buildPos, blockState, EnumFacing.UP, random);
				}
			}
		}
	}


	/**
	 * Builds the walls of this sector from within the chunk position.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 */
	public void buildWalls(World world, ChunkPos chunkPos, Random random) {
		// Get Start and Stop Positions:
		BlockPos startPos = this.getRoomBoundsMin();
		BlockPos stopPos = this.getRoomBoundsMax();

		Vec3i size = this.getRoomSize();
		int startX = Math.min(startPos.getX(), stopPos.getX());
		int stopX = Math.max(startPos.getX(), stopPos.getX());
		int startY = Math.min(startPos.getY() + 1, stopPos.getY());
		int stopY = Math.max(startPos.getY() - 1, stopPos.getY());
		int startZ = Math.min(startPos.getZ(), stopPos.getZ());
		int stopZ = Math.max(startPos.getZ(), stopPos.getZ());

		if("stairs".equalsIgnoreCase(this.dungeonSector.type)) {
			startY = Math.max(1, startPos.getY() - (size.getY() * 2));
		}

		final BlockPos.MutableBlockPos buildPos = new BlockPos.MutableBlockPos();
		for(int layerIndex : this.dungeonSector.wall.layers.keySet()) {
			SectorLayer layer = this.dungeonSector.wall.layers.get(layerIndex);
			for(int y = startY; y <= stopY; y++) {
				// Y Limit:
				if(y <= 0 || y >= world.getHeight()) {
					continue;
				}

				// Get Row:
				int progressY = y - startY;
				int fullY = stopY - startY;
				List<Character> row = layer.getRow(progressY, fullY);

				// Build Front/Back:
				for(int x = startX; x <= stopX; x++) {
					char buildChar = layer.getColumn(progressY, fullY, x - startX, stopX - startX, row);
					IBlockState blockState = this.theme.getWall(this, buildChar, random);
					if(blockState != null) {
						this.placeBlock(world, chunkPos, buildPos.setPos(x, y, startZ + layerIndex), blockState, EnumFacing.SOUTH, random);
						this.placeBlock(world, chunkPos, buildPos.setPos(x, y, stopZ - layerIndex), blockState, EnumFacing.NORTH, random);
					}
				}

				// Build Left/Right:
				for(int z = startZ; z <= stopZ; z++) {
					char buildChar = layer.getColumn(progressY, fullY, z - startZ, stopZ - startZ, row);
					IBlockState blockState = this.theme.getWall(this, buildChar, random);
					if(blockState != null) {
						this.placeBlock(world, chunkPos, buildPos.setPos(startX + layerIndex, y, z), blockState, EnumFacing.EAST, random);
						this.placeBlock(world, chunkPos, buildPos.setPos(stopX - layerIndex, y, z), blockState, EnumFacing.WEST, random);
					}
				}
			}
		}
	}


	/**
	 * Builds the ceiling of this sector from within the chunk position.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 */
	public void buildCeiling(World world, ChunkPos chunkPos, Random random) {
		// Get Start and Stop Positions:
		BlockPos startPos = this.getRoomBoundsMin();
		BlockPos stopPos = this.getRoomBoundsMax();
		int startX = Math.min(startPos.getX(), stopPos.getX());
		int stopX = Math.max(startPos.getX(), stopPos.getX());
		int stopY = Math.max(startPos.getY(), stopPos.getY());
		int startZ = Math.min(startPos.getZ(), stopPos.getZ());
		int stopZ = Math.max(startPos.getZ(), stopPos.getZ());

		final BlockPos.MutableBlockPos buildPos = new BlockPos.MutableBlockPos();
		for(int layerIndex : this.dungeonSector.ceiling.layers.keySet()) {
			int y = stopY + layerIndex;
			if(y <= 0 || y >= world.getHeight()) {
				continue;
			}
			SectorLayer layer = this.dungeonSector.ceiling.layers.get(layerIndex);
			for(int x = startX; x <= stopX; x++) {
				List<Character> row = layer.getRow(x - startX, stopX - startX);
				for(int z = startZ; z <= stopZ; z++) {
					char buildChar = layer.getColumn(x - startX, stopX - startX, z - startZ, stopZ - startZ, row);
					buildPos.setPos(x, y, z);
					IBlockState blockState = this.theme.getCeiling(this, buildChar, random);
					if(blockState != null)
						this.placeBlock(world, chunkPos, buildPos, blockState, EnumFacing.DOWN, random);
				}
			}
		}
	}


	/**
	 * Builds the entrances of this sector from within the chunk position.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 */
	public void buildEntrances(World world, ChunkPos chunkPos, Random random) {
		this.parentConnector.buildEntrance(world, chunkPos, random);
	}


	/**
	 * Builds a set of stairs leading down to a lower room to start the next level.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 */
	public void buildStairs(World world, ChunkPos chunkPos, Random random) {
		// Get Start and Stop Positions:
		BlockPos startPos = this.getRoomBoundsMin();
		BlockPos stopPos = this.getRoomBoundsMax();

		Vec3i size = this.getRoomSize();
		int centerX = startPos.getX() + Math.round((float)size.getX() / 2);
		int centerZ = startPos.getZ() + Math.round((float)size.getZ() / 2);

		int stairsHeight = this.parentConnector.parentSector.getOccupiedSize().getY() - 1;
		if(this.dungeonSector.type.equalsIgnoreCase("stairs")) {
			stairsHeight = size.getY() * 2;
		}
		int startX = centerX - 1;
		int stopX = centerX + 1;
		int startY = Math.min(startPos.getY(), stopPos.getY());
		int stopY = Math.max(1, startPos.getY() - stairsHeight);

		int startZ = centerZ - 1;
		int stopZ = centerZ + 1;

		IBlockState floorBlockState = this.theme.getFloor(this, 'B', random);
		IBlockState stairsBlockState = this.stairBlock;

		final BlockPos.MutableBlockPos buildPos = new BlockPos.MutableBlockPos();
		for(int y = startY; y >= stopY; y--) {
			for(int x = startX; x <= stopX; x++) {
				for(int z = startZ; z <= stopZ; z++) {
					IBlockState blockState = this.airBlock;

					// Center:
					if(x == centerX && z == centerZ) {
						blockState = this.theme.getWall(this, 'B', random);
					}

					// Spiral Stairs:
					int step = y % 8;
					int offsetX = x - startX;
					int offsetZ = z - startZ;
					if(step % 4 == 3) {
						if (offsetX == 0 && offsetZ == 0) {
							blockState = floorBlockState;
						}
						else if (offsetX == 0 && offsetZ == 1) {
							blockState = stairsBlockState;
						}
					}
					if(step % 4 == 2) {
						if (offsetX == 0 && offsetZ == 2) {
							blockState = floorBlockState;
						}
						else if (offsetX == 1 && offsetZ == 2) {
							blockState = stairsBlockState.withRotation(Rotation.COUNTERCLOCKWISE_90);
						}
					}
					if(step % 4 == 1) {
						if (offsetX == 2 && offsetZ == 2) {
							blockState = floorBlockState;
						}
						else if (offsetX == 2 && offsetZ == 1) {
							blockState = stairsBlockState.withRotation(Rotation.CLOCKWISE_180);
						}
					}
					if(step % 4 == 0) {
						if (offsetX == 2 && offsetZ == 0) {
							blockState = floorBlockState;
						}
						else if (offsetX == 1 && offsetZ == 0) {
							blockState = stairsBlockState.withRotation(Rotation.CLOCKWISE_90);
						}
					}

					buildPos.setPos(x, y, z);
					this.placeBlock(world, chunkPos, buildPos, blockState, EnumFacing.UP, random);
				}
			}
		}
	}


	/**
	 * Spawns a mob in this sector.
	 * @param world The world to spawn a mob in.
	 * @param chunkPos The chunk position to spawn within.
	 * @param blockPos The position to spawn the mob at.
	 * @param mobSpawn The Mob Spawn entry to use.
	 * @param random The instance of random, used for mob vacations where applicable.
	 */
	public void spawnMob(World world, ChunkPos chunkPos, BlockPos blockPos, MobSpawn mobSpawn, Random random) {
		// Restrict To Chunk Position:
		int chunkOffset = 8;
		if(blockPos.getX() < chunkPos.getXStart() + chunkOffset || blockPos.getX() > chunkPos.getXEnd() + chunkOffset) {
			return;
		}
		if(blockPos.getY() <= 0 || blockPos.getY() >= world.getHeight()) {
			return;
		}
		if(blockPos.getZ() < chunkPos.getZStart() + chunkOffset || blockPos.getZ() > chunkPos.getZEnd() + chunkOffset) {
			return;
		}

		// Spawn Mob:
		LycanitesMobs.logDebug("Dungeon", "Spawning mob " + mobSpawn + " at: " + blockPos + " level: " + this.parentConnector.level);
		EntityLiving entityLiving = mobSpawn.createEntity(world);
		entityLiving.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());

		if(entityLiving instanceof BaseCreatureEntity) {
			BaseCreatureEntity entityCreature = (BaseCreatureEntity)entityLiving;
			entityCreature.setHome(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Math.max(3, Math.max(this.roomSize.getX(), this.roomSize.getZ())));
		}

		mobSpawn.onSpawned(entityLiving, null);
		world.spawnEntity(entityLiving);
	}


	/**
	 * Formats this object into a String.
	 * @return A formatted string description of this object.
	 */
	@Override
	public String toString() {
		String bounds = "";
		String size = "";
		if(this.parentConnector != null) {
			bounds = " Bounds: " + this.getOccupiedBoundsMin() + " to " + this.getOccupiedBoundsMax();
			size = " Occupies: " + this.getOccupiedSize();
		}
		return "Sector Instance Type: " + (this.dungeonSector == null ? "Unset" : this.dungeonSector.type) + " Parent Connector Pos: " + (this.parentConnector == null ? "Unset" : this.parentConnector.position) + size + bounds;
	}
}
