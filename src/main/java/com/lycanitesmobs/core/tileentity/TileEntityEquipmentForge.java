package com.lycanitesmobs.core.tileentity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.container.EquipmentForgeContainer;
import com.lycanitesmobs.client.gui.EquipmentForgeScreen;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.NonNullList;
import com.lycanitesmobs.client.localisation.LanguageManager;

public class TileEntityEquipmentForge extends TileEntityBase {
	/** A list of item stacks in the forge. **/
	protected NonNullList<ItemStack> itemStacks = NonNullList.withSize(ItemEquipment.PART_LIMIT + 1, ItemStack.EMPTY);

	/** The level of the forge. **/
	protected int level = 1;


	@Override
	public void onRemove() {
		// TODO Drop parts or piece.
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
		if(!(itemStack.getItem() instanceof ItemEquipment) && !(itemStack.getItem() instanceof ItemEquipmentPart)) {
			return false;
		}

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


	// ========================================
	//              Client Events
	// ========================================
	@Override
	public boolean receiveClientEvent(int eventID, int eventArg) {
		return false;
	}


	// ========================================
	//             Network Packets
	// ========================================
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound syncData = new NBTTagCompound();

		// Server to Client:
		if(!this.getWorld().isRemote) {
			syncData.setInteger("ForgeLevel", this.level);
		}

		return new SPacketUpdateTileEntity(this.getPos(), 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		if(!this.getWorld().isRemote)
			return;

		NBTTagCompound syncData = packet.getNbtCompound();
		if(syncData.hasKey("ForgeLevel"))
			this.level = syncData.getInteger("ForgeLevel");
	}

	@Override
	public void onGuiButton(byte buttonId) {
		LycanitesMobs.logDebug("", "Received button packet id: " + buttonId);
	}


	// ========================================
	//                 NBT Data
	// ========================================
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.hasKey("ForgeLevel")) {
			this.level = nbtTagCompound.getInteger("ForgeLevel");
		}
		if(nbtTagCompound.hasKey("Items")) {
			ItemStackHelper.loadAllItems(nbtTagCompound, this.itemStacks);
		}
		super.readFromNBT(nbtTagCompound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		nbtTagCompound.setInteger("ForgeLevel", this.level);
		ItemStackHelper.saveAllItems(nbtTagCompound, this.itemStacks);
		return super.writeToNBT(nbtTagCompound);
	}


	// ========================================
	//                Open GUI
	// ========================================
	@Override
	public Object getGUI(EntityPlayer player) {
		if(player.world.isRemote) {
			return new EquipmentForgeScreen(this, player.inventory);
		}
		if(player instanceof EntityPlayerMP) {
			((EntityPlayerMP)player).connection.sendPacket(this.getUpdatePacket());
		}
		return new EquipmentForgeContainer(this, player.inventory);
	}


	// ========================================
	//              Equipment Forge
	// ========================================
	/**
	 * Returns the level of this Equipment Forge.
	 */
	public int getLevel() {
		return this.level;
	}

	/**
	 * Sets the level of this Equipment Forge.
	 * @param level The level to set the forge to. Higher levels allow for working with higher level Equipment Parts.
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Gets the name of this Equipment Forge.
	 */
	public String getName() {
		String levelName = "lesser";
		if(this.level == 2) {
			levelName = "greater";
		}
		else if(this.level >= 3) {
			levelName = "master";
		}
		return LanguageManager.translate("tile.equipmentforge_" + levelName + ".name");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}
}
