package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.item.ChargeItem;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.tileentity.EquipmentInfuserTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;

public class EquipmentInfuserContainer extends BaseContainer {
	public EquipmentInfuserTileEntity equipmentInfuser;
	EquipmentInfuserPartSlot partSlot;
	EquipmentInfuserChargeSlot chargeSlot;

	/**
	 * Constructor
	 * @param equipmentInfuser The Equipment Forge Tile Entity.
	 * @param playerInventory The Inventory of the accessing player.
	 */
	public EquipmentInfuserContainer(EquipmentInfuserTileEntity equipmentInfuser, InventoryPlayer playerInventory) {
		super();
		this.equipmentInfuser = equipmentInfuser;

		// Player Inventory
		this.addPlayerSlots(playerInventory, 0, 0);

		// Forge Inventory
		this.inventoryStart = this.inventorySlots.size();
		int slots = 0;
		if(equipmentInfuser.getSizeInventory() > 0) {
			int y = 28;

			this.chargeSlot = new EquipmentInfuserChargeSlot(this, slots++, 50, y);
			this.addSlotToContainer(this.chargeSlot);

			this.partSlot = new EquipmentInfuserPartSlot(this, slots++, 110, y);
			this.addSlotToContainer(this.partSlot);
		}
		this.inventoryFinish = this.inventoryStart + slots;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		if(this.equipmentInfuser == null) {
			return false;
		}
		return true;
	}

	/**
	 * Called by either the Charge slot or the Part slot, performs infusion if possible.
	 */
	public void attemptInfusion() {
		// Equipment Part:
		if(this.partSlot.getStack().getItem() instanceof ItemEquipmentPart) {
			ItemEquipmentPart equipmentPart = (ItemEquipmentPart) this.partSlot.getStack().getItem();

			// Charge Experience:
			if (this.chargeSlot.getStack().getItem() instanceof ChargeItem) {
				if (equipmentPart.getLevel(this.partSlot.getStack()) >= equipmentPart.levelMax) {
					return;
				}
				ChargeItem chargeItem = (ChargeItem) this.chargeSlot.getStack().getItem();
				if (equipmentPart.isLevelingChargeItem(this.chargeSlot.getStack())) {
					int experienceGained = equipmentPart.getExperienceFromChargeItem(this.chargeSlot.getStack());
					equipmentPart.addExperience(this.partSlot.getStack(), experienceGained);
					this.chargeSlot.decrStackSize(1);
					this.attemptInfusion();
				}
				return;
			}

			// Dye Part:
			if (this.chargeSlot.getStack().getItem() instanceof ItemDye) {
				int dyeColor = ItemDye.DYE_COLORS[this.chargeSlot.getStack().getMetadata()];
				int i = (dyeColor & 16711680) >> 16;
				int j = (dyeColor & '\uff00') >> 8;
				int k = (dyeColor & 255) >> 0;
				equipmentPart.setColor(this.partSlot.getStack(), (float)i / 255.0F, (float)j / 255.0F, (float)k / 255.0F);
				this.chargeSlot.decrStackSize(1);
				return;
			}

			// Remove Part Dye:
			if (this.chargeSlot.getStack().getItem() == Items.WATER_BUCKET) {
				equipmentPart.setColor(this.partSlot.getStack(), 1, 1, 1);
				this.chargeSlot.putStack(new ItemStack(Items.BUCKET));
				return;
			}
		}

		// Experience Bottle:
		if (this.partSlot.getStack().getItem() == Items.GLASS_BOTTLE) {
			if (this.chargeSlot.getStack().getItem() instanceof ChargeItem) {
				this.partSlot.putStack(new ItemStack(Items.EXPERIENCE_BOTTLE));
				this.chargeSlot.decrStackSize(1);
			}
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
