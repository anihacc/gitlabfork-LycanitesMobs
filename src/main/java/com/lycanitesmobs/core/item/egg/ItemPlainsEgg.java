package com.lycanitesmobs.core.item.egg;

import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.plainsmobs.PlainsMobs;

public class ItemPlainsEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemPlainsEgg() {
        super();
        setUnlocalizedName("plainsspawn");
        this.group = PlainsMobs.instance.group;
        this.itemName = "plainsspawn";
        this.texturePath = "plainsspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
