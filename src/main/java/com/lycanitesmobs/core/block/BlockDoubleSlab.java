package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockDoubleSlab extends BlockPillar {
    protected String slabName;

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockDoubleSlab(Block.Properties properties, ModInfo group, String name, String slabName) {
		super(properties, group, name);
        this.slabName = slabName;
	}


    // ==================================================
    //                      Break
    // ==================================================
    //========== Drops ==========
    // TODO Slab Drops
}
