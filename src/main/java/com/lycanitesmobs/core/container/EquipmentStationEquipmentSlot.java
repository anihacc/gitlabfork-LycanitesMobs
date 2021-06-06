package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EquipmentStationEquipmentSlot extends BaseSlot {
	public EquipmentStationContainer container;

	/**
	 * Constructor
	 * @param container The Equipment Station Container using this slot.
	 * @param slotIndex THe index of this slot.
	 * @param x The x display position.
	 * @param y The y display position.
	 */
	public EquipmentStationEquipmentSlot(EquipmentStationContainer container, int slotIndex, int x, int y) {
		super(container.equipmentStation, slotIndex, x, y);
		this.container = container;
	}


	@Override
	public boolean isItemValid(ItemStack itemStack) {
		return itemStack.getItem() instanceof ItemEquipment;
	}


	/**
	 * Returns true if this slot has an item stack.
	 * @return True if this slot has a valid item stack.
	 */
	@Override
	public boolean getHasStack() {
		return super.getHasStack();
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
    }


	/**
	 * Called when an ItemStack is inserted into this slot.
	 * @param itemStack The ItemStack being inserted.
	 */
	@Override
	public void putStack(ItemStack itemStack) {
		super.putStack(itemStack);
		this.container.attemptRepair();
	}


	@Override
	public ItemStack onTake(EntityPlayer player, ItemStack itemStack) {
		return super.onTake(player, itemStack);
	}


	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return true;
	}
}
