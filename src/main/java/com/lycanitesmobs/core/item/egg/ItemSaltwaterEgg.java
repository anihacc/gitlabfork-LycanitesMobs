package com.lycanitesmobs.core.item.egg;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;


public class ItemSaltwaterEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSaltwaterEgg() {
        super();
        setUnlocalizedName("saltwaterspawn");
        this.group = LycanitesMobs.modInfo;
        this.itemName = "saltwaterspawn";
        this.texturePath = "saltwaterspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
