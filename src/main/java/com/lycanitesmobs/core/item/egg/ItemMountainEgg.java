package com.lycanitesmobs.core.item.egg;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;


public class ItemMountainEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemMountainEgg() {
        super();
        setUnlocalizedName("mountainspawn");
        this.group = LycanitesMobs.modInfo;
        this.itemName = "mountainspawn";
        this.texturePath = "mountainspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
