package com.lycanitesmobs.core.block;


import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class BlockMaker {
    public static List<BlockMakerEntry> STONE_ENTRIES = new ArrayList<BlockMakerEntry>();

    public static class BlockMakerEntry {
        public String stoneName;
        public Object creationItem;
        public Object creationBlock;

        public BlockMakerEntry(String stoneName, Object creationItem, Object creationBlock) {
            this.stoneName = stoneName;
            this.creationItem = creationItem;
            this.creationBlock = creationBlock;
        }
    }

    // ==================================================
    //                Add Stone Blocks
    // ==================================================
    /** Creates a set of stone blocks such as tiles, bricks, pillars, etc as well as a crystal light source. The block name is added to a list that is used to automatically add each recipe at Post Init.
     * @param group The group info to add each block with.
     * @param stoneName The name of the stone block, such as "demon" or "shadow", etc. (stone and crystal are appended).
     * @param creationItem The block, item or item stack used to create this stone block from vanilla stone, such as how Nether Warts are used for demonstone (can be null for none).
     * @param creationBlock The block (can be item or item stack also) used in the base crafting recipe, usually cobblestone (null will deault to cobblestone).
     * **/
    public static void addStoneBlocks(ModInfo group, String stoneName, Object creationItem, Object creationBlock) {
        Block.Properties properties = Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2F, 10F);
        Block.Properties crystalProperties = Block.Properties.create(Material.GLASS).sound(SoundType.GLASS).hardnessAndResistance(0.3F, 10F).lightValue(15);

        BlockBase stoneBlock = new BlockBase(properties, group, stoneName + "stone");
        ObjectManager.addBlock(stoneName + "stone", stoneBlock);
        ObjectManager.addBlock(stoneName + "stone_stairs", new BlockStairsCustom(properties, stoneBlock));
        ObjectManager.addBlock(stoneName + "stone_slab", new BlockSlabCustom(properties, stoneBlock));

        BlockBase stoneBrickBlock = new BlockBase(properties, group, stoneName + "stonebrick");
        ObjectManager.addBlock(stoneName + "stonebrick", stoneBrickBlock);
        ObjectManager.addBlock(stoneName + "stonebrick_stairs", new BlockStairsCustom(properties, stoneBrickBlock));
        ObjectManager.addBlock(stoneName + "stonebrick_slab", new BlockSlabCustom(properties, stoneBrickBlock));
        ObjectManager.addBlock(stoneName + "stonebrick_fence", new BlockFenceCustom(properties, stoneBrickBlock));
        ObjectManager.addBlock(stoneName + "stonebrick_wall", new BlockWallCustom(properties, stoneBrickBlock));

        BlockBase stoneTileBlock = new BlockBase(properties, group, stoneName + "stonetile");
        ObjectManager.addBlock(stoneName + "stonetile", stoneTileBlock);
        ObjectManager.addBlock(stoneName + "stonetile_stairs", new BlockStairsCustom(properties, stoneTileBlock));
        ObjectManager.addBlock(stoneName + "stonetile_slab", new BlockSlabCustom(properties, stoneTileBlock));

        ObjectManager.addBlock(stoneName + "stonepolished", new BlockBase(properties, group, stoneName + "stonepolished"));
        ObjectManager.addBlock(stoneName + "stonechiseled", new BlockBase(properties, group, stoneName + "stonechiseled"));
        ObjectManager.addBlock(stoneName + "stonepillar", new BlockPillar(properties, group, stoneName + "stonepillar"));

        ObjectManager.addBlock(stoneName + "crystal", new BlockBase(crystalProperties, group, stoneName + "crystal"));

        STONE_ENTRIES.add(new BlockMakerEntry(stoneName, creationItem, creationBlock));
    }

    public static void addStoneBlocks(ModInfo group, String stoneName, Object creationItem) {
        addStoneBlocks(group, stoneName, creationItem, Blocks.COBBLESTONE);
    }
}
