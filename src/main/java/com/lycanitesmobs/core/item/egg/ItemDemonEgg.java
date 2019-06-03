package com.lycanitesmobs.core.item.egg;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;


public class ItemDemonEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDemonEgg() {
        super();
        this.setUnlocalizedName("demonspawn");
        this.group = LycanitesMobs.modInfo;
        this.itemName = "demonspawn";
        this.texturePath = "demonspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
