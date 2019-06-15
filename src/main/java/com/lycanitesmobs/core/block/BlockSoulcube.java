package com.lycanitesmobs.core.block;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockSoulcube extends BlockBase {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockSoulcube(Block.Properties properties, String name) {
		super(properties);
		
		// Properties:
		this.group = LycanitesMobs.modInfo;
		this.blockName = name;
		this.setup();
		
		// Stats:
		//this.setHardness(5F);
        //this.setResistance(10F);
		//this.setHarvestLevel("pickaxe", 2);
		//this.setSoundType(SoundType.GLASS);
	}
}
