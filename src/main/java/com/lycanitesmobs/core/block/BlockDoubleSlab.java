package com.lycanitesmobs.core.block;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;


import java.util.List;
import java.util.Random;

public class BlockDoubleSlab extends BlockPillar {
    protected String slabName;

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockDoubleSlab(Material material, ModInfo group, String name, String slabName) {
		super(material, group, name);
        this.slabName = slabName;
	}


    // ==================================================
    //                      Info
    // ==================================================
    @Override
    public String getLocalizedName() {
        return LanguageManager.translate(this.getUnlocalizedName() + ".name");
    }

    @Override
    public void addInformation(ItemStack stack,  World world, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(this.getDescription(stack, world));
    }

    public String getDescription(ItemStack itemStack,  World world) {
        return "\u00a7a" + LanguageManager.translate(this.getUnlocalizedName() + ".description");
    }


    // ==================================================
    //                      Break
    // ==================================================
    //========== Drops ==========
    @Override
    public Item getItemDropped(IBlockState state, Random random, int zero) {
        Block slabBlock = ObjectManager.getBlock(this.slabName);
        if(slabBlock != null)
            return Item.getItemFromBlock(slabBlock);
        return super.getItemDropped(state, random, zero);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public int quantityDropped(Random random) {
        return ObjectManager.getBlock(this.slabName) != null ? 2 : 1;
    }
}
