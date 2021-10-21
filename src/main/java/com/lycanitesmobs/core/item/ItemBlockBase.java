package com.lycanitesmobs.core.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

public class ItemBlockBase extends BlockItem {

	public ItemBlockBase(Block block, Item.Properties properties) {
		super(block, properties);
	}

	@Override
	public Component getName(ItemStack stack) {
		return this.getBlock().getName();
	}
}
