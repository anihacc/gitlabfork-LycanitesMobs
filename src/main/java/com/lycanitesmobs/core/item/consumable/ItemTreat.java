package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.core.info.CreatureType;
import com.lycanitesmobs.core.item.BaseItem;
import net.minecraft.item.Item;


public class ItemTreat extends BaseItem {
	protected CreatureType creatureType;

	/**
	 * Constructor
	 * @param creatureType The creature type this treat is used to tame.
	 */
    public ItemTreat(Item.Properties properties, CreatureType creatureType) {
		super(properties);
		this.itemName = creatureType.getTreatName();
		this.modInfo = creatureType.modInfo;
		this.creatureType = creatureType;
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
