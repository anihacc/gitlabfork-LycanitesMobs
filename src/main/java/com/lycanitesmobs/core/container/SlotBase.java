package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.inventory.InventoryCreature;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SlotBase extends Slot {
	
    // ==================================================
  	//                    Constructor
  	// ==================================================
	public SlotBase(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}
	
	
    // ==================================================
  	//                    Validation
  	// ==================================================
	public boolean isItemValid(ItemStack itemStack) {
		if(this.inventory == null) {
			return true;
		}
		return this.inventory.isItemValidForSlot(this.getSlotIndex(), itemStack);
    }
	
	public int getSlotStackLimit() {
		if(this.inventory instanceof InventoryCreature)
			if(((InventoryCreature)this.inventory).getTypeFromSlot(this.getSlotIndex()) != null)
				return 1;
        return this.inventory.getInventoryStackLimit();
    }
}
