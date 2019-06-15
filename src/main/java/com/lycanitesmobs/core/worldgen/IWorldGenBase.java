package com.lycanitesmobs.core.worldgen;

import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.ChunkGenerator;

import java.util.Random;

public interface IWorldGenBase {

    public void onWorldGen(Random random, int chunkX, int chunkZ, World world, ChunkGenerator chunkGenerator, AbstractChunkProvider chunkProvider);

}
