package com.lycanitesmobs.core.tileentity;

import com.lycanitesmobs.client.gui.EquipmentInfuserScreen;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.container.EquipmentInfuserContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class EquipmentInfuserTileEntity extends TileEntityBase {
	/** A list of item stacks in the infuser. **/
	protected NonNullList<ItemStack> itemStacks = NonNullList.withSize(2, ItemStack.EMPTY);

	@Override
	public Object getGUI(EntityPlayer player) {
		if(player.world.isRemote) {
			return new EquipmentInfuserScreen(this, player.inventory);
		}
		/*if(player instanceof EntityPlayerMP) {
			((EntityPlayerMP)player).connection.sendPacket(this.getUpdatePacket());
		}*/
		return new EquipmentInfuserContainer(this, player.inventory);
	}

	public String getName() {
		return LanguageManager.translate("tile.equipment_infuser");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public void onRemove() {
		// TODO Drop items.
		super.onRemove();
	}

	@Override
	public void update() {
		super.update();
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
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

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
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean receiveClientEvent(int eventID, int eventArg) {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		if(nbtTagCompound.hasKey("Items")) {
			ItemStackHelper.loadAllItems(nbtTagCompound, this.itemStacks);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		ItemStackHelper.saveAllItems(nbtTagCompound, this.itemStacks);
		return super.writeToNBT(nbtTagCompound);
	}
}
