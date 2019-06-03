package com.lycanitesmobs.core.item.egg;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;


public class ItemElementalEgg extends ItemCustomSpawnEgg {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemElementalEgg() {
        super();
        this.setUnlocalizedName("elementalspawn");
        this.group = LycanitesMobs.modInfo;
        this.itemName = "elementalspawn";
        this.texturePath = "elementalspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
