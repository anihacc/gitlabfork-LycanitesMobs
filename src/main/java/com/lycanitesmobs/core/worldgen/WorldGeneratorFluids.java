package com.lycanitesmobs.core.worldgen;

import com.lycanitesmobs.core.IWorldGenBase;
import com.lycanitesmobs.core.worldgen.fluid.WorldGenOozeLakes;
import com.lycanitesmobs.core.worldgen.fluid.WorldGenPureLavaLakes;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldGeneratorFluids implements IWorldGenerator {
    protected final IWorldGenBase oozeLakes;
    protected final IWorldGenBase pureLavaLakes;

    // ==================================================
    //                    Constructors
    // ==================================================
    public WorldGeneratorFluids() {
        this.oozeLakes = new WorldGenOozeLakes();
        this.pureLavaLakes = new WorldGenPureLavaLakes();
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
