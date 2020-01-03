package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.item.ChargeItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;

public class EquipmentInfuserChargeSlot extends BaseSlot {
	public EquipmentInfuserContainer container;

	/**
	 * Constructor
	 * @param container The Equipment Infuser Container using this slot.
	 * @param slotIndex THe index of this slot.
	 * @param x The x display position.
	 * @param y The y display position.
	 */
	public EquipmentInfuserChargeSlot(EquipmentInfuserContainer infuserContainer, int slotIndex, int x, int y) {
		super(infuserContainer.equipmentInfuser, slotIndex, x, y);
		this.container = infuserContainer;
	}

	@Override
	public boolean isItemValid(ItemStack itemStack) {
		return itemStack.getItem() instanceof ChargeItem || itemStack.getItem() instanceof ItemDye || itemStack.getItem() == Items.WATER_BUCKET || itemStack.getItem() == Items.BUCKET;
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
		return 64;
    }

	/**
	 * Called when an ItemStack is inserted into this slot.
	 * @param itemStack The ItemStack being inserted.
	 */
	@Override
	public void putStack(ItemStack itemStack) {
		super.putStack(itemStack);
		this.container.attemptInfusion();
	}

	/**
	 * Called when an ItemStack is removed into this slot.
	 * @param itemStack The ItemStack being inserted.
	 */
	@Override
	public ItemStack onTake(EntityPlayer player, ItemStack itemStack) {
		return super.onTake(player, itemStack);
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return true;
	}
}
