package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.ItemBase;


public class ItemTreat extends ItemBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemTreat(String setItemName, ModInfo group) {
        super();
		this.itemName = setItemName;
		this.modInfo = group;
        this.setMaxStackSize(16);
        this.setUnlocalizedName(this.itemName);
        this.setup();
    }
}
