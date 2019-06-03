package com.lycanitesmobs.core.item.egg;

import com.lycanitesmobs.arcticmobs.ArcticMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;

public class ItemArcticEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemArcticEgg() {
        super();
        setUnlocalizedName("arcticspawn");
        this.group = ArcticMobs.instance.group;
        this.itemName = "arcticspawn";
        this.texturePath = "arcticspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
