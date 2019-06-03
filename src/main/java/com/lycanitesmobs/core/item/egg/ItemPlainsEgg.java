package com.lycanitesmobs.core.item.egg;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;


public class ItemPlainsEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemPlainsEgg() {
        super();
        setUnlocalizedName("plainsspawn");
        this.group = LycanitesMobs.modInfo;
        this.itemName = "plainsspawn";
        this.texturePath = "plainsspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
