package com.lycanitesmobs.core.item.equipment;

import com.lycanitesmobs.core.info.CreatureType;
import com.lycanitesmobs.core.item.ItemBase;


public class CreatureSaddleItem extends ItemBase {
	protected CreatureType creatureType;

	/**
	 * Constructor
	 * @param creatureType The creature type this saddle is used for.
	 */
    public CreatureSaddleItem(CreatureType creatureType) {
        super();
		this.itemName = creatureType.getSaddleName();
		this.modInfo = creatureType.modInfo;
		this.creatureType = creatureType;
        this.setMaxStackSize(16);
        this.setUnlocalizedName(this.itemName);
        this.setup();
    }

	/**
	 * Gets the creature type that this saddle is used for.
	 * @return The creature type of this saddle item.
	 */
	public CreatureType getCreatureType() {
    	return this.creatureType;
	}
}
