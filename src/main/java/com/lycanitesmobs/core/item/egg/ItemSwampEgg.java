package com.lycanitesmobs.core.item.egg;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;


public class ItemSwampEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwampEgg() {
        super();
        setUnlocalizedName("swampspawn");
        this.group = LycanitesMobs.modInfo;
        this.itemName = "swampspawn";
        this.texturePath = "swampspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
