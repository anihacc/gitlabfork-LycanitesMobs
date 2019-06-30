package com.lycanitesmobs.core.worldgen;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.config.ConfigDungeons;
import com.lycanitesmobs.core.dungeon.instance.DungeonInstance;
import com.mojang.datafixers.Dynamic;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.ScatteredStructurePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

public class DungeonStructure extends Structure<NoFeatureConfig> {
	public static IStructurePieceType PIECE_TYPE = IStructurePieceType.register(Piece::new, "LMDungeonPiece");

	public DungeonStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	public boolean hasStartAt(@Nonnull ChunkGenerator<?> chunkGenerator, @Nonnull Random random, int chunkX, int chunkZ) {
		//LycanitesMobs.logDebug("", "Has Start At: " + chunkX + " " + chunkZ);
		return true;
	}

	@Override
	public String getStructureName() {
		return "Lycanites Mobs Dungeon";
	}

	@Override
	public int getSize() {
		return 20;
	}

	@Override
	public IStartFactory getStartFactory() {
		return Start::new;
	}

	@Nullable
	public BlockPos findNearest(World world, ChunkGenerator<? extends GenerationSettings> chunkGenerator, BlockPos blockPos, int p_211405_4_, boolean p_211405_5_) {
		if (!chunkGenerator.getBiomeProvider().hasStructure(this)) {
			return null;
		}

		ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(world);
		if(extendedWorld == null) {
			return null;
		}

		double closestDistance = Double.MAX_VALUE;
		DungeonInstance closestDungeon = null;
		List<DungeonInstance> nearbyDungeons = extendedWorld.getNearbyDungeonInstances(new ChunkPos(blockPos), 240);
		for(DungeonInstance dungeonInstance : nearbyDungeons) {
			double distance = dungeonInstance.originPos.distanceSq(blockPos);
			if(distance < closestDistance) {
				closestDistance = distance;
				closestDungeon = dungeonInstance;
			}
		}
		return closestDungeon.originPos;
	}

	/**
	 * The structure building start class.
	 */
	public static class Start extends StructureStart {

		public Start(Structure<?> structure, int chunkX, int chunkZ, Biome biome, MutableBoundingBox boundingBox, int reference, long seed) {
			super(structure, chunkX, chunkZ, biome, boundingBox, reference, seed);
			//LycanitesMobs.logDebug("", "Starting At: " + chunkX + " " + chunkZ);
		}

		@Override
		public void init(@Nonnull ChunkGenerator<?> generator, @Nonnull TemplateManager templateManager, int chunkX, int chunkZ, Biome biome) {
			this.components.add(new Piece(this.rand, chunkX * 16, chunkZ * 16));
		}
	}

	/**
	 * The structure building piece class.
	 */
	public static class Piece extends ScatteredStructurePiece {
		public boolean enabled;
		public int dungeonDistance;

		public Piece(Random random, int chunkX, int chunkZ) {
			super(PIECE_TYPE, random, chunkX, 64, chunkZ, 7, 7, 9);
		}

		protected Piece(TemplateManager templateManager, CompoundNBT nbt) {
			super(PIECE_TYPE, nbt);
			this.enabled = ConfigDungeons.INSTANCE.dungeonsEnabled.get();
			this.dungeonDistance = ConfigDungeons.INSTANCE.dungeonDistance.get();
		}

		@Override
		protected void readAdditional(CompoundNBT tagCompound) {

		}

		@Override
		public boolean addComponentParts(IWorld worldWriter, @Nonnull Random random, @Nonnull MutableBoundingBox structureBoundingBoxIn, @Nonnull ChunkPos chunkPos) {
			boolean enabled = ConfigDungeons.INSTANCE.dungeonsEnabled.get();
			int dungeonDistance = ConfigDungeons.INSTANCE.dungeonDistance.get();

			World world = worldWriter.getWorld();
			ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(world);
			if(!enabled || extendedWorld == null) {
				return false;
			}
			try {
				int dungeonSizeMax = dungeonDistance;
				List<DungeonInstance> nearbyDungeons = extendedWorld.getNearbyDungeonInstances(chunkPos, dungeonSizeMax * 2);

				// Create New Instances:
				if (nearbyDungeons.isEmpty()) {
					for (int x = -1; x <= 1; x++) {
						for (int z = -1; z <= 1; z++) {
							if (x == 0 && z == 0) {
								continue;
							}
							if (x != 0 && z != 0) {
								continue;
							}
							LycanitesMobs.logDebug("Dungeon", "Creating A New Dungeon At Chunk: X" + (chunkPos.x + (dungeonSizeMax * x)) + " Z" + (chunkPos.z + (dungeonSizeMax * z)));
							DungeonInstance dungeonInstance = new DungeonInstance();
							int yPos = worldWriter.getSeaLevel();
							BlockPos dungeonPos = new ChunkPos(chunkPos.x + (dungeonSizeMax * x), chunkPos.z + (dungeonSizeMax * z)).getBlock(7, yPos, 7);
							dungeonInstance.setOrigin(dungeonPos);
							extendedWorld.addDungeonInstance(dungeonInstance, new UUID(random.nextLong(), random.nextLong()));
							dungeonInstance.init(world);
							LycanitesMobs.logDebug("Dungeon", "Dungeon (Structure) Created: " + dungeonInstance.toString());
						}
					}
				}

				// Build Dungeons:
				nearbyDungeons = extendedWorld.getNearbyDungeonInstances(chunkPos, 0);
				for(DungeonInstance dungeonInstance : nearbyDungeons) {
					dungeonInstance.buildChunk(worldWriter, world, chunkPos, random);
				}
			}
			catch(Exception e) {
				LycanitesMobs.logWarning("", "An exception occurred when trying to generate a dungeon.");
				e.printStackTrace();
			}
			return true;
		}
	}}
