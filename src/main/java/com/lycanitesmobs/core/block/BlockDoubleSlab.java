package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.world.level.block.Block;

public class BlockDoubleSlab extends BlockPillar {
    protected String slabName;

	public BlockDoubleSlab(Block.Properties properties, ModInfo group, String name, String slabName) {
		super(properties, group, name);
        this.slabName = slabName;
	}
}
