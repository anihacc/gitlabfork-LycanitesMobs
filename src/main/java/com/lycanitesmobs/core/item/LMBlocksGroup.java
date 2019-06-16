package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LMBlocksGroup extends ItemGroup {

	// ========== Constructor ==========
	public LMBlocksGroup(int tabID, String modID) {
		super(tabID, modID);
	}
	
	// ========== Tab Icon ==========
	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack createIcon() {
		if(ObjectManager.getBlock("summoningpedestal") != null)
			return new ItemStack(Item.getItemFromBlock(ObjectManager.getBlock("summoningpedestal")));
		else if(ObjectManager.getBlock("demoncrystal") != null)
			return new ItemStack(Item.getItemFromBlock(ObjectManager.getBlock("demoncrystal")));
		else if(ObjectManager.getBlock("shadowcrystal") != null)
			return new ItemStack(Item.getItemFromBlock(ObjectManager.getBlock("shadowcrystal")));
		else
			return new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN));
	}
}