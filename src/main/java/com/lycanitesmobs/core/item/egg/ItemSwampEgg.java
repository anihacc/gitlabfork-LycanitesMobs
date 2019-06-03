package com.lycanitesmobs.core.item.egg;

import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.swampmobs.SwampMobs;

public class ItemSwampEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwampEgg() {
        super();
        setUnlocalizedName("swampspawn");
        this.group = SwampMobs.instance.group;
        this.itemName = "swampspawn";
        this.texturePath = "swampspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
