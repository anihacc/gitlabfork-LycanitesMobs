package com.lycanitesmobs.core.item.egg;

import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.mountainmobs.MountainMobs;

public class ItemMountainEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemMountainEgg() {
        super();
        setUnlocalizedName("mountainspawn");
        this.group = MountainMobs.instance.group;
        this.itemName = "mountainspawn";
        this.texturePath = "mountainspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
