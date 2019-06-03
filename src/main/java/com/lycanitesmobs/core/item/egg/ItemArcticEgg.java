package com.lycanitesmobs.core.item.egg;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;

public class ItemArcticEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemArcticEgg() {
        super();
        setUnlocalizedName("arcticspawn");
        this.group = LycanitesMobs.modInfo;
        this.itemName = "arcticspawn";
        this.texturePath = "arcticspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
