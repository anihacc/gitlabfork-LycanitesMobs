package com.lycanitesmobs.demonmobs.item;

import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.demonmobs.DemonMobs;

public class ItemDemonEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDemonEgg() {
        super();
        this.setUnlocalizedName("demonspawn");
        this.group = DemonMobs.instance.group;
        this.itemName = "demonspawn";
        this.texturePath = "demonspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
