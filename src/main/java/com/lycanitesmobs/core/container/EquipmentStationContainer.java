package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.info.ItemManager;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.tileentity.EquipmentStationTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class EquipmentStationContainer extends BaseContainer {
	public EquipmentStationTileEntity equipmentStation;
	EquipmentStationEquipmentSlot equipmentSlot;
	EquipmentStationRepairSlot repairSlot;

	/**
	 * Constructor
	 * @param equipmentStation The Equipment Forge Tile Entity.
	 * @param playerInventory The Inventory of the accessing player.
	 */
	public EquipmentStationContainer(EquipmentStationTileEntity equipmentStation, InventoryPlayer playerInventory) {
		super();
		this.equipmentStation = equipmentStation;

		// Player Inventory
		this.addPlayerSlots(playerInventory, 0, 0);

		// Forge Inventory
		this.inventoryStart = this.inventorySlots.size();
		int slots = 0;
		if(equipmentStation.getSizeInventory() > 0) {
			int y = 28;

			this.repairSlot = new EquipmentStationRepairSlot(this, slots++, 50, y);
			this.addSlotToContainer(this.repairSlot);

			this.equipmentSlot = new EquipmentStationEquipmentSlot(this, slots++, 110, y);
			this.addSlotToContainer(this.equipmentSlot);
		}
		this.inventoryFinish = this.inventoryStart + slots;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		if(this.equipmentStation == null || !this.equipmentStation.isUsableByPlayer(player)) {
			return false;
		}
		return true;
	}

	/**
	 * Called by either the Repair slot or the Equipment slot, performs a repair if possible.
	 */
	public void attemptRepair() {
		if(this.equipmentSlot.getStack().isEmpty() || !(this.equipmentSlot.getStack().getItem() instanceof ItemEquipment)) {
			return;
		}

		int sharpness = ItemManager.getInstance().getEquipmentSharpnessRepair(this.repairSlot.getStack());
		int mana = ItemManager.getInstance().getEquipmentManaRepair(this.repairSlot.getStack());
		if (sharpness <= 0 && mana <= 0) {
			return;
		}

		ItemEquipment equipment = (ItemEquipment) this.equipmentSlot.getStack().getItem();
		boolean repaired = false;
		if (sharpness > 0) {
			repaired = equipment.addSharpness(this.equipmentSlot.getStack(), sharpness);
		}
		if (mana > 0) {
			repaired = equipment.addMana(this.equipmentSlot.getStack(), mana) || repaired;
		}

		if (repaired) {
			this.repairSlot.decrStackSize(1);
			this.attemptRepair();
		}
	}

	/**
	 * Disabled until fixed later.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		return ItemStack.EMPTY;
	}
}
