package com.lycanitesmobs.core.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ContainerBase extends Container {
	public Map<Integer, String> specialSlots = new HashMap<Integer, String>();
	public int playerInventoryStart = -1;
	public int playerInventoryFinish = -1;
	public int inventoryStart = -1;
	public int inventoryFinish = -1;
	public int specialStart = -1;
	public int specialFinish = -1;
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public ContainerBase() {
		super();
	}
	
	
	// ==================================================
  	//                     Add Slot
  	// ==================================================
	/**
	 * @param inventory The inventory to draw.
	 * @param x The starting x position of the grid.
	 * @param y The starting y position of the grid.
	 * @param slotIndex The inventory index for this slot.
	 */
	public Slot addSlot(IInventory inventory, int slotIndex, int x, int y) {
		return this.addSlotToContainer(new SlotBase(inventory, slotIndex, x, y));
	}
	
	public void addPlayerSlots(InventoryPlayer playerInventory, int offsetX, int offsetY) {
		// Player Hotbar:
		this.playerInventoryStart = this.inventorySlots.size();
		this.addSlotGrid(playerInventory, 8, 142, 1, 0, 8);
		
		// Player Inventory:
		this.addSlotGrid(playerInventory, 8, 84, 3, 9, 35);
		this.playerInventoryFinish = this.inventorySlots.size() - 1;
	}
	
	
	// ==================================================
  	//                  Draw Slot Grids
  	// ==================================================
	/**
	 * @param inventory The inventory to draw.
	 * @param x The starting x position of the grid.
	 * @param y The starting y position of the grid.
	 * @param totalRows How many rows (horizontal strips) to draw.
	 * @param start The starting slot index to draw (0 is the first slot).
	 * @param finish The finishing slot index to draw (use -1 for the last slot index).
	 */
	public void addSlotGrid(IInventory inventory, int x, int y, int totalRows, int start, int finish) {
		// Assess Grid Shape:
		int totalSlots = (finish - start) + 1;
		if(totalSlots < 1)
			return;
		int excessSlots = totalSlots % totalRows;
		int totalColumns = (totalSlots - excessSlots) / totalRows;
		
		// Draw the Grid:
		int row;
		int slot = 0;
		for(row = 0; row < totalRows; row++) {
			for(int column = 0; column < totalColumns; column++) {
				this.addSlot(inventory, start + slot, x + (column * 18), y + (row * 18));
				slot++;
			}
		}
		row++;
		for(int column = 0; column < excessSlots; column++) {
			this.addSlot(inventory, start + slot, x + (column * 18), y + (row * 18));
			slot++;
		}
			
	}
	public void addSlotGrid(IInventory inventory, int x, int y, int totalRows) {
		this.addSlotGrid(inventory, x, y, totalRows, 0);
	}
	public void addSlotGrid(IInventory inventory, int x, int y, int totalRows, int start) {
		this.addSlotGrid(inventory, x, y, totalRows, start, inventory.getSizeInventory() - 1);
	}
	
	/**
	 * @param inventory The inventory to draw.
	 * @param x The starting x position of the grid.
	 * @param y The starting y position of the grid.
	 * @param columnMax How many columns (horizontal strips) to draw before starting a new row.
	 * @param start The starting slot index to draw (0 is the first slot).
	 * @param finish The finishing slot index to draw (use -1 for the last slot index).
	 */
	public void addSlotsByColumn(IInventory inventory, int x, int y, int columnMax, int start, int finish) {
		int row = 0;
		int column = 0;
		for(int slot = 0; slot <= finish; slot++) {
			if(column >= columnMax) {
				row++;
				column = 0;
			}
			this.addSlot(inventory, start + slot, x + (column * 18), y + (row * 18));
			column++;
		}
	}
	
	
	// ==================================================
  	//                   Auto Placing
  	// ==================================================
	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotID) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotID);
		if(slot == null || !slot.getHasStack())
			return itemStack;
		
        ItemStack slotStack = slot.getStack();
        itemStack = slotStack.copy();
        
        // Player Slots (Sort to Inventory):
        if(this.isPlayerSlot(slotID)) {
        	if(this.specialStart >= 0 && this.specialFinish >= 0) {
        		for(int i = this.specialStart; i <= this.specialFinish; i++) {
        			if(i < this.inventorySlots.size()) {
        				Slot targetSlot = this.inventorySlots.get(i);
	        			if(targetSlot.isItemValid(itemStack)) {
		        			if(!this.mergeItemStack(slotStack, i, i + 1, false)) {
			        			return ItemStack.EMPTY;
		        			}
	        			}
        			}
        		}
        	}
        	if(this.inventoryStart >= 0 && this.inventoryFinish >= 0 && this.inventoryStart < this.inventoryFinish) {
        		if(!this.mergeItemStack(slotStack, this.inventoryStart, this.inventoryFinish + 1, false))
        			return ItemStack.EMPTY;
        	}
        }
        
        // Inventory Slots (Sort to Player):
        else {
	        if(this.playerInventoryFinish >= 0)
	        	if(!this.mergeItemStack(slotStack, this.playerInventoryStart, this.playerInventoryFinish, true))
		        	return ItemStack.EMPTY;
        }
        
        if(slotStack.getCount() == 0)
            slot.putStack(ItemStack.EMPTY);
        else
            slot.onSlotChanged();
        
        return ItemStack.EMPTY;
	}

	public boolean isPlayerSlot(int slotID) {
		return slotID >= this.playerInventoryStart && slotID <= this.playerInventoryFinish;
	}
	
	public boolean sortToSpecialSlots(ItemStack slotStack) {
		return true;
	}
	
	public String getSpecialSlot(int slotID) {
		if(!this.specialSlots.containsKey(slotID))
			return null;
		return this.specialSlots.get(slotID);
	}
	
	
	// ==================================================
  	//                     Interact
  	// ==================================================
	@Override
	public boolean canInteractWith(PlayerEntity entityplayer) {
		return true;
	}
}
