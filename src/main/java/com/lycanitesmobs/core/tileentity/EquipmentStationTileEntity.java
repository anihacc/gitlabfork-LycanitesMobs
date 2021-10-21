package com.lycanitesmobs.core.tileentity;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class EquipmentStationTileEntity extends TileEntityBase {
	/** A list of item stacks in the station. **/
	protected NonNullList<ItemStack> itemStacks = NonNullList.withSize(2, ItemStack.EMPTY);

	public Component getName() {
		return new TranslatableComponent("block.lycanitesmobs.equipment_station");
	}

	@Override
	public BlockEntityType<?> getType() {
		return ObjectManager.tileEntityTypes.get(this.getClass());
	}

	@Override
	public void setRemoved() {
		// TODO Drop parts or piece.
		super.setRemoved();
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
	public ItemStack getItem(int index) {
		return this.itemStacks.get(index);
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
	 */
	@Override
	public ItemStack removeItem(int index, int count) {
		return ContainerHelper.removeItem(this.itemStacks, index, count);
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(this.itemStacks, index);
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setItem(int index, ItemStack stack) {
		this.itemStacks.set(index, stack);
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
	}

	@Override
	public int getContainerSize() {
		return this.itemStacks.size();
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
	 */
	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public void startOpen(Player player) {

	}

	@Override
	public void stopOpen(Player player) {

	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
	 * guis use Slot.isItemValid
	 */
	@Override
	public boolean canPlaceItem(int index, ItemStack itemStack) {
		ItemStack existingStack = this.getItem(index);
		return existingStack.isEmpty();
	}

	@Override
	public void clearContent() {

	}

	@Override
	public boolean triggerEvent(int eventID, int eventArg) {
		return false;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag syncData = new CompoundTag();
		return new ClientboundBlockEntityDataPacket(this.getBlockPos(), 1, syncData);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		super.onDataPacket(net, packet);
	}

	@Override
	public void onGuiButton(int buttonId) {

	}

	@Override
	public void load(BlockState blockState, CompoundTag nbtTagCompound) {
		super.load(blockState, nbtTagCompound);
		if(nbtTagCompound.contains("Items")) {
			ContainerHelper.loadAllItems(nbtTagCompound, this.itemStacks);
		}
	}

	@Override
	public CompoundTag save(CompoundTag nbtTagCompound) {
		ContainerHelper.saveAllItems(nbtTagCompound, this.itemStacks);
		return super.save(nbtTagCompound);
	}
}
