package com.lycanitesmobs.core.item.egg;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;


public class ItemJungleEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemJungleEgg() {
        super();
        setUnlocalizedName("junglespawn");
        this.group = LycanitesMobs.modInfo;
        this.itemName = "junglespawn";
        this.texturePath = "junglespawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
