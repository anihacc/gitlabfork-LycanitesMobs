package com.lycanitesmobs.core.worldgen.fluid;

import com.lycanitesmobs.core.IWorldGenBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldGeneratorOoze implements IWorldGenerator {
    protected final IWorldGenBase oozeLakes;

    // ==================================================
    //                    Constructors
    // ==================================================
    public WorldGeneratorOoze() {
        this.oozeLakes = new WorldGenOozeLakes();
    }


    // ==================================================
    //                      Generate
    // ==================================================
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        this.oozeLakes.onWorldGen(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
    }
}
