package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.core.info.CreatureType;
import com.lycanitesmobs.core.item.ItemBase;


public class ItemTreat extends ItemBase {
	protected CreatureType creatureType;

	/**
	 * Constructor
	 * @param creatureType The creature type this treat is used to tame.
	 */
    public ItemTreat(CreatureType creatureType) {
        super();
		this.itemName = creatureType.getTreatName();
		this.modInfo = creatureType.modInfo;
		this.creatureType = creatureType;
        this.setMaxStackSize(16);
        this.setUnlocalizedName(this.itemName);
        this.setup();
    }

	/**
	 * Gets the creature type that this treat can tame.
	 * @return The creature type of this treat item.
	 */
	public CreatureType getCreatureType() {
    	return this.creatureType;
	}
}
