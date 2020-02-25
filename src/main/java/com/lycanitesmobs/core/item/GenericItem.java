package com.lycanitesmobs.core.item;

import com.lycanitesmobs.core.info.ItemProperties;

public class GenericItem extends ItemBase {
	public String modelName;
	public ItemProperties properties;

	public GenericItem(ItemProperties properties, String itemName) {
		super();
		this.itemName = itemName;
		this.properties = properties;
		super.setup();
	}

	@Override
	public void setup() {
		this.setRegistryName(this.modInfo.modid, this.itemName);
		this.setUnlocalizedName(this.itemName);
		this.setCreativeTab(this.properties.creativeTab);
		this.setMaxStackSize(this.properties.maxStackSize);
	}
}
