package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.item.ItemBase;


public class ItemTreat extends ItemBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemTreat(String setItemName, GroupInfo group) {
        super();
		this.itemName = setItemName;
		this.group = group;
        this.setMaxStackSize(16);
        this.textureName = this.itemName.toLowerCase();
        this.setUnlocalizedName(this.itemName);
        this.setup();
    }
}
