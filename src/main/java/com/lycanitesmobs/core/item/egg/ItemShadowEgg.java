package com.lycanitesmobs.core.item.egg;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;


public class ItemShadowEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemShadowEgg() {
        super();
        setUnlocalizedName("shadowspawn");
        this.group = LycanitesMobs.modInfo;
        this.itemName = "shadowspawn";
        this.texturePath = "shadowspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
