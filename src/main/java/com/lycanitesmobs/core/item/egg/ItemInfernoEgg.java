package com.lycanitesmobs.core.item.egg;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;

public class ItemInfernoEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemInfernoEgg() {
        super();
        setUnlocalizedName("infernospawn");
        this.group = LycanitesMobs.modInfo;
        this.itemName = "infernospawn";
        this.texturePath = "infernospawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
