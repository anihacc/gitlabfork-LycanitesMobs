package com.lycanitesmobs.core.item.egg;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;

public class ItemDesertEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDesertEgg() {
        super();
        setUnlocalizedName("desertspawn");
        this.group = LycanitesMobs.modInfo;
        this.itemName = "desertspawn";
        this.texturePath = "desertspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
