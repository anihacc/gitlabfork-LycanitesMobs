package com.lycanitesmobs.core.container;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ContainerEquipmentForge extends ContainerBase {
	public TileEntityEquipmentForge equipmentForge;

	/**
	 * Constructor
	 * @param equipmentForge The Equipment Forge Tile Entity.
	 * @param playerInventory The Inventory of the accessing player.
	 */
	public ContainerEquipmentForge(TileEntityEquipmentForge equipmentForge, PlayerInventory playerInventory) {
		super(0);
		this.equipmentForge = equipmentForge;

		// Player Inventory
		this.addPlayerSlots(playerInventory, 0, 0);

		// Forge Inventory
		this.inventoryStart = this.inventorySlots.size();
		int slots = 0;
		if(equipmentForge.getSizeInventory() > 0) {
			int slotSize = 18;
			int x = 8 + slotSize;
			int y = 38;

			// Crafted Piece:
			SlotEquipment slotEquipmentPiece = new SlotEquipment(this, slots++, x + (slotSize * 6), y, "piece");
			this.addSlot(slotEquipmentPiece);

			// Base:
			SlotEquipment slotEquipmentBase = new SlotEquipment(this, slots++, x + slotSize, y, "base");
			this.addSlot(slotEquipmentBase);

			// Head:
			SlotEquipment slotEquipmentHead = new SlotEquipment(this, slots++, x + (slotSize * 2), y, "none");
			this.addSlot(slotEquipmentHead);
			slotEquipmentBase.addChildSlot(slotEquipmentHead);

			// Tips:
			SlotEquipment slotEquipmentTipA = new SlotEquipment(this, slots++, x + (slotSize * 3), y, "none");
			this.addSlot(slotEquipmentTipA);
			slotEquipmentHead.addChildSlot(slotEquipmentTipA);

			SlotEquipment slotEquipmentTipB = new SlotEquipment(this, slots++, x + (slotSize * 2), y - slotSize, "none");
			this.addSlot(slotEquipmentTipB);
			slotEquipmentHead.addChildSlot(slotEquipmentTipB);

			SlotEquipment slotEquipmentTipC = new SlotEquipment(this, slots++, x + (slotSize * 2), y + slotSize, "none");
			this.addSlot(slotEquipmentTipC);
			slotEquipmentHead.addChildSlot(slotEquipmentTipC);

			// Pommel:
			SlotEquipment slotEquipmentPommel = new SlotEquipment(this, slots++, x, y, "none");
			this.addSlot(slotEquipmentPommel);
			slotEquipmentBase.addChildSlot(slotEquipmentPommel);
		}
		this.inventoryFinish = this.inventoryStart + slots;
		//this.inventoryFinish = this.inventorySlots.size() - 1;
	}


	@Override
	public boolean canInteractWith(PlayerEntity player) {
		if(this.equipmentForge == null) {
			return false;
		}
		return true;
	}


	/**
	 * Called when an equipment piece slot's contents is changed.
	 * @param slotEquipment The equipment slot that changed. This must be a piece type slot.
	 */
	public void onEquipmentPieceSlotChanged(SlotEquipment slotEquipment) {
		if(this.equipmentForge == null) {
			return;
		}
		this.clearPartSlots();

		// Edit Equipment Piece:
		SlotEquipment slotBase = (SlotEquipment)this.getSlot(this.inventoryStart + 1);
		if(slotEquipment.getHasStack() && !slotBase.getHasStack() && slotEquipment.getStack().getItem() instanceof ItemEquipment) { // Only edit if not already creating a new piece.
			SlotEquipment slotHead = (SlotEquipment)this.getSlot(this.inventoryStart + 2);
			SlotEquipment slotTipA = (SlotEquipment)this.getSlot(this.inventoryStart + 3);
			SlotEquipment slotTipB = (SlotEquipment)this.getSlot(this.inventoryStart + 4);
			SlotEquipment slotTipC = (SlotEquipment)this.getSlot(this.inventoryStart + 5);
			SlotEquipment slotPommel = (SlotEquipment)this.getSlot(this.inventoryStart + 6);

			ItemEquipment itemEquipment = (ItemEquipment) slotEquipment.getStack().getItem();
			int axeIndex = 0;
			for(ItemStack partStack : itemEquipment.getEquipmentPartStacks(slotEquipment.getStack())) {
				ItemEquipmentPart itemEquipmentPart = itemEquipment.getEquipmentPart(partStack);
				if(itemEquipmentPart == null) {
					continue;
				}
				if("base".equals(itemEquipmentPart.slotType)) {
					slotBase.putStackWithoutUpdate(partStack);
				}
				else if("head".equals(itemEquipmentPart.slotType)) {
					slotHead.putStackWithoutUpdate(partStack);
				}
				else if("blade".equals(itemEquipmentPart.slotType) || "pike".equals(itemEquipmentPart.slotType)) {
					slotTipA.putStackWithoutUpdate(partStack);
				}
				else if("axe".equals(itemEquipmentPart.slotType)) {
					if(axeIndex++ == 0) {
						slotTipB.putStackWithoutUpdate(partStack);
					}
					else {
						slotTipC.putStackWithoutUpdate(partStack);
					}
				}
				else if("pommel".equals(itemEquipmentPart.slotType)) {
					slotPommel.putStackWithoutUpdate(partStack);
				}
			}
		}
	}


	/**
	 * Called when an equipment part slot's contents is changed.
	 * @param slotEquipment The equipment slot that changed. This must be any type of part slot.
	 */
	public void onEquipmentPartSlotChanged(SlotEquipment slotEquipment) {
		if(this.equipmentForge == null) {
			return;
		}

		// Slots:
		Slot slotPiece = this.getSlot(this.inventoryStart);
		Slot slotBase = this.getSlot(this.inventoryStart + 1);
		Slot slotHead = this.getSlot(this.inventoryStart + 2);
		Slot slotTipA = this.getSlot(this.inventoryStart + 3);
		Slot slotTipB = this.getSlot(this.inventoryStart + 4);
		Slot slotTipC = this.getSlot(this.inventoryStart + 5);
		Slot slotPommel = this.getSlot(this.inventoryStart + 6);

		// Create Equipment Piece:
		ItemEquipment itemEquipment = (ItemEquipment)ObjectManager.getItem("equipment");
		ItemStack pieceStack = new ItemStack(itemEquipment);

		// Add Parts:
		if(slotBase.getHasStack()) {
			itemEquipment.addEquipmentPart(pieceStack, slotBase.getStack(), 0);
		}
		if(slotHead.getHasStack()) {
			itemEquipment.addEquipmentPart(pieceStack, slotHead.getStack(), 1);
		}
		if(slotTipA.getHasStack()) {
			itemEquipment.addEquipmentPart(pieceStack, slotTipA.getStack(), 2);
		}
		if(slotTipB.getHasStack()) {
			itemEquipment.addEquipmentPart(pieceStack, slotTipB.getStack(), 3);
		}
		if(slotTipC.getHasStack()) {
			itemEquipment.addEquipmentPart(pieceStack, slotTipC.getStack(), 4);
		}
		if(slotPommel.getHasStack()) {
			itemEquipment.addEquipmentPart(pieceStack, slotPommel.getStack(), 5);
		}

		// Put Piece Stack:
		slotPiece.putStack(pieceStack);
	}


	/**
	 * Clears all slots except the piece slot.
	 */
	public void clearPartSlots() {
		for(int i = 1; i <= 6; i++) {
			Slot slot = this.getSlot(this.inventoryStart + i);
			if(slot instanceof SlotEquipment) {
				SlotEquipment slotEquipment = (SlotEquipment)slot;
				slotEquipment.putStackWithoutUpdate(ItemStack.EMPTY);
			}
		}
	}


	/**
	 * Returns true if all required parts have been added to make a valid piece of equipment.
	 * @return True if the equipment is valid.
	 */
	public boolean isEquipmentValid() {
		Slot slotBase = this.getSlot(this.inventoryStart + 1);
		Slot slotHead = this.getSlot(this.inventoryStart + 2);
		Slot slotTipA = this.getSlot(this.inventoryStart + 3);
		Slot slotTipB = this.getSlot(this.inventoryStart + 4);
		Slot slotTipC = this.getSlot(this.inventoryStart + 5);

		if(!slotBase.getHasStack() || !slotHead.getHasStack()) {
			return false;
		}

		return slotTipA.getHasStack() || slotTipB.getHasStack() || slotTipC.getHasStack();
	}


	/**
	 * Disabled until fixed later.
	 */
	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotID) {
		return ItemStack.EMPTY;
	}
}
