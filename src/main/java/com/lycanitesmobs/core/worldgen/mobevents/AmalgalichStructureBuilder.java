package com.lycanitesmobs.core.worldgen.mobevents;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.creature.EntityAmalgalich;
import com.lycanitesmobs.core.entity.projectile.EntityShadowfireBarrier;
import com.lycanitesmobs.core.mobevent.MobEventPlayerServer;
import com.lycanitesmobs.core.mobevent.effects.StructureBuilder;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AmalgalichStructureBuilder extends StructureBuilder {

	public AmalgalichStructureBuilder() {
		this.name = "amalgalich";
	}

	@Override
	public void build(World world, EntityPlayer player, BlockPos pos, int level, int ticks, int variant) {
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
				BaseProjectileEntity baseProjectileEntity = new EntityShadowfireBarrier(world, originX, originY + (10 * i), originZ);
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

		// Build Obstacles:
		if(ticks == 5 * 20) {
			this.buildObstacles(world, originX, originY, originZ);
		}

		// Explosions:
		if(ticks >= 10 * 20 && ticks % 10 == 0) {
			world.createExplosion(null, originX - 20 + world.rand.nextInt(40), originY + 25 + world.rand.nextInt(10), originZ - 20 + world.rand.nextInt(40), 2, true);
		}

		// Spawn Boss:
		if(ticks == 20 * 20) {
			BaseCreatureEntity baseCreatureEntity = new EntityAmalgalich(world);
			baseCreatureEntity.setLocationAndAngles(originX, originY + 1, originZ, 0, 0);
			world.spawnEntity(baseCreatureEntity);
			baseCreatureEntity.setArenaCenter(new BlockPos(originX, originY + 1, originZ));
			if(worldExt != null) {
				MobEventPlayerServer mobEventPlayerServer = worldExt.getMobEventPlayerServer(this.name);
				if(mobEventPlayerServer != null) {
					mobEventPlayerServer.mobEvent.onSpawn(baseCreatureEntity, world, player, pos, level, ticks, variant);
				}
			}
		}
	}


	// ==================================================
	//                     Arena Floor
	// ==================================================
	public void buildArenaFloor(World world, int originX, int originY, int originZ) {
		int radius = 60;
		int height = 40;
		Block primaryBlock = ObjectManager.getBlock("shadowstonetile");
		Block secondaryBlock = ObjectManager.getBlock("shadowcrystal");
		double secondaryChance = 0.05D;

		int stripNumber = 1;
		for(int x = originX - radius; x < originX + radius; x++) {
			float stripNormal = (float)stripNumber / (float)radius;
			if(stripNumber > radius)
				stripNormal = (float)(radius - (stripNumber - radius)) / (float)radius;
			int stripRadius = Math.round(radius * (float) Math.sin(Math.toRadians(90 * stripNormal)));

			for(int z = originZ - stripRadius; z < originZ + stripRadius; z++) {
				int y = originY;
				// Build Floor:
				Block buildBlock = primaryBlock;
				if(world.rand.nextDouble() <= secondaryChance)
					buildBlock = secondaryBlock;
				world.setBlockState(new BlockPos(x, y, z), buildBlock.getDefaultState(), 2);
				world.setBlockState(new BlockPos(x, y - 1, z), buildBlock.getDefaultState(), 2);
				world.setBlockState(new BlockPos(x, y - 2, z), buildBlock.getDefaultState(), 2);
				y++;
				while(y <= originY + height && y < world.getHeight()) {
					world.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState(), 2);
					y++;
				}
			}

			stripNumber++;
		}
	}


	// ==================================================
	//                   Arena Obstacles
	// ==================================================
	public void buildObstacles(World world, int originX, int originY, int originZ) {
		double angle = 0;
		int radius = 50;
		List<int[]> decorationCoords = new ArrayList<>();

		while(angle < 360) {
			angle += 5 + (5 * world.rand.nextDouble());
			double angleRadians = Math.toRadians(angle);
			double x = radius * Math.cos(angleRadians) - Math.sin(angleRadians);
			double z = radius * Math.sin(angleRadians) + Math.cos(angleRadians);
			decorationCoords.add(this.buildPillar(world, originX + (int) Math.ceil(x), originY, originZ + (int) Math.ceil(z)));
		}
	}

	/** Builds an actual pillar. **/
	public int[] buildPillar(World world, int originX, int originY, int originZ) {
		int radiusMax = 6;
		int height = 30;
		Block primaryBlock = ObjectManager.getBlock("shadowstonebrick");
		Block secondaryBlock = ObjectManager.getBlock("shadowstone");
		Block tetriaryBlock = ObjectManager.getBlock("shadowstonechiseled");
		Block capBlock = Blocks.OBSIDIAN;
		Block hazardBlock = ObjectManager.getBlock("shadowfire");
		double secondaryChance = 0.4D;
		double tetriaryChance = 0.05D;
		int[] decorationCoord = new int[] {originX, originY, originZ};

		int radius = radiusMax;
		int radiusHeight = height - 10;
		for(int y = originY; y <= originY + height; y++) {
			boolean lowerRadius = --radiusHeight <= 0;
			int stripNumber = 1;
			for(int x = originX - radius; x <= originX + radius; x++) {
				float stripNormal = (float)stripNumber / (float)radius;
				if(stripNumber > radius)
					stripNormal = (float)(radius - (stripNumber - radius)) / (float)radius;
				int stripRadius = Math.round(radius * (float) Math.sin(Math.toRadians(90 * stripNormal)));

				for(int z = originZ - stripRadius; z <= originZ + stripRadius; z++) {
					if(lowerRadius || y == originY + height)
						world.setBlockState(new BlockPos(x, y, z), hazardBlock.getDefaultState(), 2);
					else if(radiusHeight - 1 <= 0 || y == originY + height - 1)
						world.setBlockState(new BlockPos(x, y, z), capBlock.getDefaultState(), 2);
					else if(world.rand.nextDouble() > secondaryChance)
						world.setBlockState(new BlockPos(x, y, z), primaryBlock.getDefaultState(), 2);
					else if(world.rand.nextDouble() > tetriaryChance)
						world.setBlockState(new BlockPos(x, y, z), secondaryBlock.getDefaultState(), 2);
					else
						world.setBlockState(new BlockPos(x, y, z), tetriaryBlock.getDefaultState(), 2);
				}

				stripNumber++;
			}
			if(lowerRadius) {
				radiusHeight = radiusMax;
				radius--;
			}
		}

		return decorationCoord;
	}

	/** Adds decoration to a pillar. **/
	public void buildDecoration(World world, int originX, int originY, int originZ) {
		Block primaryBlock = Blocks.OBSIDIAN;
		Block hazardBlock = ObjectManager.getBlock("shadowfire");
		world.setBlockState(new BlockPos(originX, originY + 1, originZ), primaryBlock.getDefaultState(), 2);
		world.setBlockState(new BlockPos(originX, originY + 2, originZ), primaryBlock.getDefaultState(), 2);
		world.setBlockState(new BlockPos(originX, originY + 3, originZ), hazardBlock.getDefaultState(), 2);
		world.setBlockState(new BlockPos(originX + 1, originY + 1, originZ), primaryBlock.getDefaultState(), 2);
		world.setBlockState(new BlockPos(originX + 1, originY + 2, originZ), hazardBlock.getDefaultState(), 2);
		world.setBlockState(new BlockPos(originX - 1, originY + 1, originZ), primaryBlock.getDefaultState(), 2);
		world.setBlockState(new BlockPos(originX - 1, originY + 2, originZ), hazardBlock.getDefaultState(), 2);
		world.setBlockState(new BlockPos(originX, originY + 1, originZ + 1), primaryBlock.getDefaultState(), 2);
		world.setBlockState(new BlockPos(originX, originY + 2, originZ + 1), hazardBlock.getDefaultState(), 2);
		world.setBlockState(new BlockPos(originX, originY + 1, originZ - 1), primaryBlock.getDefaultState(), 2);
		world.setBlockState(new BlockPos(originX, originY + 2, originZ - 1), hazardBlock.getDefaultState(), 2);
	}
}
