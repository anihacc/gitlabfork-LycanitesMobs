package com.lycanitesmobs.core.block;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockSoulcube extends BlockBase {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockSoulcube(ModInfo group, String name) {
		super(Material.IRON);
        this.setCreativeTab(LycanitesMobs.blocksTab);
		
		// Properties:
		this.group = group;
		this.blockName = name;
		this.setup();
		
		// Stats:
		this.setHardness(5F);
        this.setResistance(10F);
		this.setHarvestLevel("pickaxe", 2);
		this.setSoundType(SoundType.GLASS);
	}
}
