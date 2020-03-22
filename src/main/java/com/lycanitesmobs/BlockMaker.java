package com.lycanitesmobs;


import com.lycanitesmobs.core.block.*;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class BlockMaker {
    // ==================================================
    //                Add Stone Blocks
    // ==================================================
    /** Creates a set of stone blocks such as tiles, bricks, pillars, etc as well as a crystal light source. The block name is added to a list that is used to automatically add each recipe at Post Init.
     * @param group The group info to add each block with.
     * @param stoneName The name of the stone block, such as "demon" or "shadow", etc. (stone and crystal are appended).
     * **/
    public static void addStoneBlocks(ModInfo group, String stoneName) {
        float hardness = 2F;
        float resistance = 10F;

        BlockBase stoneBlock = (BlockBase) new BlockBase(Material.ROCK, group, stoneName + "stone").setHardness(hardness).setResistance(resistance).setCreativeTab(LycanitesMobs.blocksTab);
        ObjectManager.addBlock(stoneName + "stone", stoneBlock);
        ObjectManager.addBlock(stoneName + "stone_stairs", new BlockStairsCustom(stoneBlock).setCreativeTab(LycanitesMobs.blocksTab));
        Block stoneSlabDoubleBlock = new BlockDoubleSlab(Material.ROCK, group, stoneName + "stone_slab_double", stoneName + "stone_slab").setHardness(hardness).setResistance(resistance);
        ObjectManager.addBlock(stoneName + "stone_slab_double", stoneSlabDoubleBlock);
        ObjectManager.addBlock(stoneName + "stone_slab", new BlockSlabCustom(stoneBlock, stoneSlabDoubleBlock).setCreativeTab(LycanitesMobs.blocksTab));

        BlockBase stoneBrickBlock = (BlockBase) new BlockBase(Material.ROCK, group, stoneName + "stonebrick").setHardness(hardness).setResistance(resistance).setCreativeTab(LycanitesMobs.blocksTab);
        ObjectManager.addBlock(stoneName + "stonebrick", stoneBrickBlock);
        ObjectManager.addBlock(stoneName + "stonebrick_stairs", new BlockStairsCustom(stoneBrickBlock).setCreativeTab(LycanitesMobs.blocksTab));
        ObjectManager.addBlock(stoneName + "stonebrick_slab", new BlockSlabCustom(stoneBrickBlock, stoneBrickBlock).setCreativeTab(LycanitesMobs.blocksTab));
        ObjectManager.addBlock(stoneName + "stonebrick_fence", new BlockFenceCustom(stoneBrickBlock).setCreativeTab(LycanitesMobs.blocksTab));
        ObjectManager.addBlock(stoneName + "stonebrick_wall", new BlockWallCustom(stoneBrickBlock).setCreativeTab(LycanitesMobs.blocksTab));

        BlockBase stoneTileBlock = (BlockBase) new BlockBase(Material.ROCK, group, stoneName + "stonetile").setHardness(hardness).setResistance(resistance).setCreativeTab(LycanitesMobs.blocksTab);
        ObjectManager.addBlock(stoneName + "stonetile", stoneTileBlock);
        ObjectManager.addBlock(stoneName + "stonetile_stairs", new BlockStairsCustom(stoneTileBlock).setCreativeTab(LycanitesMobs.blocksTab));
        ObjectManager.addBlock(stoneName + "stonetile_slab", new BlockSlabCustom(stoneTileBlock, stoneTileBlock).setCreativeTab(LycanitesMobs.blocksTab));

        ObjectManager.addBlock(stoneName + "stonepolished", new BlockBase(Material.ROCK, group, stoneName + "stonepolished").setHardness(hardness).setResistance(resistance).setCreativeTab(LycanitesMobs.blocksTab));
        ObjectManager.addBlock(stoneName + "stonechiseled", new BlockBase(Material.ROCK, group, stoneName + "stonechiseled").setHardness(hardness).setResistance(resistance).setCreativeTab(LycanitesMobs.blocksTab));
        ObjectManager.addBlock(stoneName + "stonepillar", new BlockPillar(Material.ROCK, group, stoneName + "stonepillar").setHardness(hardness).setResistance(resistance).setCreativeTab(LycanitesMobs.blocksTab));
        ObjectManager.addBlock(stoneName + "crystal", new BlockBase(Material.GLASS, group, stoneName + "crystal").setBlockStepSound(SoundType.GLASS).setHardness(0.3F).setResistance(resistance).setLightLevel(1.0F).setCreativeTab(LycanitesMobs.blocksTab));
    }
}
