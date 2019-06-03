package com.lycanitesmobs.core.item.egg;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;


public class ItemForestEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemForestEgg() {
        super();
        setUnlocalizedName("forestspawn");
        this.group = LycanitesMobs.modInfo;
        this.itemName = "forestspawn";
        this.texturePath = "forestspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
