package com.lycanitesmobs.core.info;

import net.minecraft.creativetab.CreativeTabs;

public class ItemProperties {
	public CreativeTabs creativeTab;
	public int maxStackSize;
	public FoodInfo food;

	public void group(CreativeTabs group) {
		this.creativeTab = group;
	}

	public void maxStackSize(int maxStackSize) {
		this.maxStackSize = maxStackSize;
	}

	public void food(FoodInfo food) {
		this.food = food;
	}
}
