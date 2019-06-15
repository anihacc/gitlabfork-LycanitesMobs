package com.lycanitesmobs.core.worldgen;

import com.lycanitesmobs.core.IWorldGenBase;
import com.lycanitesmobs.core.worldgen.lakes.OozeLakePlacement;
import com.lycanitesmobs.core.worldgen.lakes.PureLavaLakePlacement;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldGeneratorFluids extends LakesFeature {
    protected final IWorldGenBase oozeLakes;
    protected final IWorldGenBase pureLavaLakes;

    // ==================================================
    //                    Constructors
    // ==================================================
    public WorldGeneratorFluids() {
        this.oozeLakes = new OozeLakePlacement();
        this.pureLavaLakes = new PureLavaLakePlacement();
    }


    // ==================================================
    //                      Generate
    // ==================================================
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        this.oozeLakes.onWorldGen(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        this.pureLavaLakes.onWorldGen(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
    }
}
