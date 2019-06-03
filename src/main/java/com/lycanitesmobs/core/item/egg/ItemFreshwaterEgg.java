package com.lycanitesmobs.core.item.egg;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;


public class ItemFreshwaterEgg extends ItemCustomSpawnEgg {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemFreshwaterEgg() {
        super();
        setUnlocalizedName("freshwaterspawn");
        this.group = LycanitesMobs.modInfo;
        this.itemName = "freshwaterspawn";
        this.texturePath = "freshwaterspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
