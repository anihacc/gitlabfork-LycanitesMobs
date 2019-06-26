package com.lycanitesmobs.core.item;

public class GenericItem extends BaseItem {
	public String modelName;

	public GenericItem(Properties properties, String itemName) {
		super(properties);
		this.itemName = itemName;
		super.setup();
	}
}
