package com.lycanitesmobs.core.worldgen.mobevents;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.mobevent.MobEventPlayerServer;
import com.lycanitesmobs.core.mobevent.effects.StructureBuilder;
import com.lycanitesmobs.core.entity.creature.EntityAsmodeus;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AsmodeusStructureBuilder extends StructureBuilder {

	public AsmodeusStructureBuilder() {
		this.name = "asmodeus";
	}

	@Override
	public void build(World world, EntityPlayer player, BlockPos pos, int level, int ticks, int subspecies) {
		ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
		int originX = pos.getX();
		int originY = pos.getY();
		int originZ = pos.getZ();

		originX += 20;
		int height = 40;
		if(originY < 5)
			originY = 5;
		if(world.getHeight() <= height)
			originY = 5;
		else if(originY + height >= world.getHeight())
			originY = Math.max(5, world.getHeight() - height - 1);

		// Effects:
		if(ticks == 1) {
			for(int i = 0; i < 5; i++) {
				BaseProjectileEntity baseProjectileEntity = new EntityHellfireWall(world, originX, originY + (10 * i), originZ);
				baseProjectileEntity.projectileLife = 20 * 20;
				world.spawnEntity(baseProjectileEntity);
				if(worldExt != null) {
					worldExt.bossUpdate(baseProjectileEntity);
				}
			}
		}

		// Build Floor:
		if(ticks == 3 * 20) {
			this.buildArenaFloor(world, originX, originY, originZ);
		}

		// Build Walls:
		if(ticks == 5 * 20) {
			this.buildArenaWalls(world, originX, originY, originZ);
		}

		// Build Obstacles:
		if(ticks == 7 * 20) {
			this.buildObstacles(world, originX, originY, originZ);
		}

		// Explosions:
		if(ticks >= 10 * 20 && ticks % 10 == 0) {
			world.createExplosion(null, originX - 20 + world.rand.nextInt(40), originY + 25 + world.rand.nextInt(10), originZ - 20 + world.rand.nextInt(40), 2, true);
		}

		// Spawn Boss:
		if(ticks == 20 * 20) {
			BaseCreatureEntity baseCreatureEntity = new EntityAsmodeus(world);
			baseCreatureEntity.setLocationAndAngles(originX, originY + 1, originZ, 0, 0);
			world.spawnEntity(baseCreatureEntity);
			baseCreatureEntity.setArenaCenter(new BlockPos(originX, originY + 1, originZ));
			if(worldExt != null) {
				MobEventPlayerServer mobEventPlayerServer = worldExt.getMobEventPlayerServer(this.name);
				if(mobEventPlayerServer != null) {
					mobEventPlayerServer.mobEvent.onSpawn(baseCreatureEntity, world, player, pos, level, ticks, subspecies);
				}
			}
		}
	}


	// ==================================================
	//                     Arena Floor
	// ==================================================
	public void buildArenaFloor(World world, int originX, int originY, int originZ) {
		int radius = 80;
		int height = 30;
		int minX = originX - radius;
		int maxX = originX + radius;
		int minY = originY;
		int maxY = originY + height;
		int minZ = originZ - radius;
		int maxZ = originZ + radius;
		IBlockState floor = ObjectManager.getBlock("aberrantstonetile").getDefaultState();
		IBlockState light = ObjectManager.getBlock("aberrantcrystal").getDefaultState();
		IBlockState trimming = ObjectManager.getBlock("aberrantstonechiseled").getDefaultState();

		for(int x = minX; x <= maxX; x++) {
			for(int z = minZ; z <= maxZ; z++) {
				int topY = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY();
				for(int y = minY; y <= maxY; y++) {
					BlockPos buildPos = new BlockPos(x, y, z);
					if(y == minY) {
						if(x == minX || x == maxX || z == minZ || z == maxZ)
							world.setBlockState(buildPos, trimming, 2);
						else if(x % 6 == 0 && z % 6 == 0)
							world.setBlockState(buildPos, light, 2);
						else
							world.setBlockState(buildPos, floor, 2);
					}
					else {
						if (y > minY + 5 && y >= topY)
							break;
						world.setBlockState(buildPos, Blocks.AIR.getDefaultState(), 2);
					}
				}
			}
		}
	}


	// ==================================================
	//                     Arena Walls
	// ==================================================
	public void buildArenaWalls(World world, int originX, int originY, int originZ) {
		int radius = 80;
		int thickness = 4;
		int height = 20;
		int minX = originX - (radius + thickness);
		int maxX = originX + (radius + thickness);
		int minY = originY;
		int maxY = originY + height;
		int minZ = originZ - (radius + thickness);
		int maxZ = originZ + (radius + thickness);
		IBlockState base = ObjectManager.getBlock("aberrantstonebrick").getDefaultState();
		IBlockState light = ObjectManager.getBlock("aberrantcrystal").getDefaultState();
		IBlockState trimming = ObjectManager.getBlock("aberrantstonechiseled").getDefaultState();
		IBlockState top = ObjectManager.getBlock("aberrantstonepolished").getDefaultState();
		IBlockState fireBase = Blocks.OBSIDIAN.getDefaultState();
		IBlockState fire = ObjectManager.getBlock("hellfire").getDefaultState();

		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				if(x > minX + thickness && x < maxX - thickness && z  > minZ + thickness && z < maxZ - thickness)
					continue;
				for (int y = minY; y <= maxY; y++) {
					BlockPos buildPos = new BlockPos(x, y, z);
					if(y < maxY - 2) {
						if (y - 1 % 8 == 0)
							world.setBlockState(buildPos, light, 2);
						else if (y % 8 == 0)
							world.setBlockState(buildPos, trimming, 2);
						else
							world.setBlockState(buildPos, base, 2);
					}
					else if(y == maxY - 2)
						world.setBlockState(buildPos, top, 2);
					else if(y == maxY - 1)
						world.setBlockState(buildPos, fireBase, 2);
					else
						world.setBlockState(buildPos, fire, 2);
				}
			}
		}
	}


	// ==================================================
	//                   Arena Obstacles
	// ==================================================
	public void buildObstacles(World world, int originX, int originY, int originZ) {
		int gap = 20;
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				if(x >= -1 && x <= 1 && z >= -1 && z <= 1)
					continue;
				this.buildPillar(world, originX + (x * gap), originY, originZ + (z * gap));
			}
		}
	}

	/** Builds an actual pillar. **/
	public void buildPillar(World world, int originX, int originY, int originZ) {
		int radius = 2;
		int height = 30;
		int minX = originX - radius;
		int maxX = originX + radius;
		int minY = originY;
		int maxY = originY + height;
		int minZ = originZ - radius;
		int maxZ = originZ + radius;
		IBlockState base = ObjectManager.getBlock("aberrantstonepillar").getDefaultState();
		IBlockState light = ObjectManager.getBlock("aberrantcrystal").getDefaultState();
		IBlockState trimming = ObjectManager.getBlock("aberrantstonechiseled").getDefaultState();
		IBlockState top = ObjectManager.getBlock("aberrantstonepolished").getDefaultState();
		IBlockState fireBase = Blocks.OBSIDIAN.getDefaultState();
		IBlockState fire = ObjectManager.getBlock("hellfire").getDefaultState();

		for(int x = minX; x <= maxX; x++) {
			for(int z = minZ; z <= maxZ; z++) {
				for(int y = minY; y <= maxY; y++) {
					BlockPos buildPos = new BlockPos(x, y, z);
					if(y < maxY - 2) {
						if (y % 7 == 0)
							world.setBlockState(buildPos, light, 2);
						else if (y % 8 == 0)
							world.setBlockState(buildPos, trimming, 2);
						else
							world.setBlockState(buildPos, base, 2);
					}
					else if(y == maxY - 2)
						world.setBlockState(buildPos, top, 2);
					else if(y == maxY - 1)
						world.setBlockState(buildPos, fireBase, 2);
					else
						world.setBlockState(buildPos, fire, 2);
				}
			}
		}
	}
}
