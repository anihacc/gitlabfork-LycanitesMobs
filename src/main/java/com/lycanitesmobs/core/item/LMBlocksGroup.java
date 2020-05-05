package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

public class LMBlocksGroup extends ItemGroup {

	// ========== Constructor ==========
	public LMBlocksGroup(String modID) {
		super(modID);
	}
	
	// ========== Tab Icon ==========
	@Override
	@Environment(EnvType.CLIENT)
	public ItemStack createIcon() {
		if(ObjectManager.getBlock("shadowcrystal") != null)
			return new ItemStack(Item.getItemFromBlock(ObjectManager.getBlock("shadowcrystal")));
		else if(ObjectManager.getBlock("summoningpedestal") != null)
			return new ItemStack(Item.getItemFromBlock(ObjectManager.getBlock("summoningpedestal")));
		else if(ObjectManager.getBlock("demoncrystal") != null)
			return new ItemStack(Item.getItemFromBlock(ObjectManager.getBlock("demoncrystal")));
		else
			return new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN));
	}
}