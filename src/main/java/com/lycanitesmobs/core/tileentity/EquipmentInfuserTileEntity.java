package com.lycanitesmobs.core.tileentity;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class EquipmentInfuserTileEntity extends TileEntityBase implements IInventory {
	/** A list of item stacks in the infuser. **/
	protected NonNullList<ItemStack> itemStacks = NonNullList.withSize(2, ItemStack.EMPTY);

	public ITextComponent getName() {
		return new TranslationTextComponent("block.lycanitesmobs.equipment_infuser");
	}

	@Override
	public TileEntityType<?> getType() {
		return ObjectManager.tileEntityTypes.get(this.getClass());
	}

	@Override
	public void remove() {
		// TODO Drop parts or piece.
		super.remove();
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.itemStacks) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the stack in the given slot.
	 */
	@Override
	public ItemStack getStackInSlot(int index) {
		return this.itemStacks.get(index);
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int index, int count) {
		return ItemStackHelper.getAndSplit(this.itemStacks, index, count);
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(this.itemStacks, index);
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.itemStacks.set(index, stack);
		if (stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}
	}

	@Override
	public int getSizeInventory() {
		return this.itemStacks.size();
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return false;
	}

	@Override
	public void openInventory(PlayerEntity player) {

	}

	@Override
	public void closeInventory(PlayerEntity player) {

	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
	 * guis use Slot.isItemValid
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack itemStack) {
		ItemStack existingStack = this.getStackInSlot(index);
		return existingStack.isEmpty();
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean receiveClientEvent(int eventID, int eventArg) {
		return false;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT syncData = new CompoundNBT();
		return new SUpdateTileEntityPacket(this.getPos(), 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		super.onDataPacket(net, packet);
	}

	@Override
	public void onGuiButton(int buttonId) {

	}

	@Override
	public void read(CompoundNBT nbtTagCompound) {
		super.read(nbtTagCompound);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbtTagCompound) {
		return super.write(nbtTagCompound);
	}
}
