package com.lycanitesmobs.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class ItemBlockBase extends BlockItem {

	public ItemBlockBase(Block block, Item.Properties properties) {
		super(block, properties);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return this.getBlock().getTranslatedName();
	}
}
