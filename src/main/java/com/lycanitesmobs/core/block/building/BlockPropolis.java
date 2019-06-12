package com.lycanitesmobs.core.block.building;

import com.lycanitesmobs.core.entity.creature.EntityVespidQueen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockPropolis extends BlockVeswax {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockPropolis(Block.Properties properties) {
		super(properties);
        //this.setCreativeTab(LycanitesMobs.blocksTab);
		
		// Properties:
		this.blockName = "propolis";
		
		// Stats:
		//this.setHardness(0.6F);
		//this.setHarvestLevel("shovel", 0);
		//this.setSoundType(SoundType.GROUND);
	}
}
