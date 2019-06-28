package com.lycanitesmobs.core.container;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;

public class EquipmentForgeContainer extends BaseContainer {
	public static final ContainerType<EquipmentForgeContainer> TYPE = (ContainerType<EquipmentForgeContainer>)IForgeContainerType.create(CreatureContainer::new).setRegistryName(LycanitesMobs.MODID, "equipment_forge");
	public TileEntityEquipmentForge equipmentForge;

	/**
	 * Client Constructor
	 * @param windowId The window id for the gui screen to use.
	 * @param playerInventory The accessing player's inventory.
	 * @param extraData A packet sent from the server to create the Container from.
	 */
	public EquipmentForgeContainer(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
		this(windowId, playerInventory, (TileEntityEquipmentForge)playerInventory.player.getEntityWorld().getTileEntity(BlockPos.fromLong(extraData.readLong())));
	}

	/**
	 * Main Constructor
	 * @param equipmentForge The Equipment Forge Tile Entity.
	 * @param playerInventory The Inventory of the accessing player.
	 */
	public EquipmentForgeContainer(int windowId, PlayerInventory playerInventory, TileEntityEquipmentForge equipmentForge) {
		super(TYPE, windowId);
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
			EquipmentSlot equipmentSlotPiece = new EquipmentSlot(this, slots++, x + (slotSize * 6), y, "piece");
			this.addSlot(equipmentSlotPiece);

			// Base:
			EquipmentSlot equipmentSlotBase = new EquipmentSlot(this, slots++, x + slotSize, y, "base");
			this.addSlot(equipmentSlotBase);

			// Head:
			EquipmentSlot equipmentSlotHead = new EquipmentSlot(this, slots++, x + (slotSize * 2), y, "none");
			this.addSlot(equipmentSlotHead);
			equipmentSlotBase.addChildSlot(equipmentSlotHead);

			// Tips:
			EquipmentSlot equipmentSlotTipA = new EquipmentSlot(this, slots++, x + (slotSize * 3), y, "none");
			this.addSlot(equipmentSlotTipA);
			equipmentSlotHead.addChildSlot(equipmentSlotTipA);

			EquipmentSlot equipmentSlotTipB = new EquipmentSlot(this, slots++, x + (slotSize * 2), y - slotSize, "none");
			this.addSlot(equipmentSlotTipB);
			equipmentSlotHead.addChildSlot(equipmentSlotTipB);

			EquipmentSlot equipmentSlotTipC = new EquipmentSlot(this, slots++, x + (slotSize * 2), y + slotSize, "none");
			this.addSlot(equipmentSlotTipC);
			equipmentSlotHead.addChildSlot(equipmentSlotTipC);

			// Pommel:
			EquipmentSlot equipmentSlotPommel = new EquipmentSlot(this, slots++, x, y, "none");
			this.addSlot(equipmentSlotPommel);
			equipmentSlotBase.addChildSlot(equipmentSlotPommel);
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
	 * @param equipmentSlot The equipment slot that changed. This must be a piece type slot.
	 */
	public void onEquipmentPieceSlotChanged(EquipmentSlot equipmentSlot) {
		if(this.equipmentForge == null) {
			return;
		}
		this.clearPartSlots();

		// Edit Equipment Piece:
		EquipmentSlot slotBase = (EquipmentSlot)this.getSlot(this.inventoryStart + 1);
		if(equipmentSlot.getHasStack() && !slotBase.getHasStack() && equipmentSlot.getStack().getItem() instanceof ItemEquipment) { // Only edit if not already creating a new piece.
			EquipmentSlot slotHead = (EquipmentSlot)this.getSlot(this.inventoryStart + 2);
			EquipmentSlot slotTipA = (EquipmentSlot)this.getSlot(this.inventoryStart + 3);
			EquipmentSlot slotTipB = (EquipmentSlot)this.getSlot(this.inventoryStart + 4);
			EquipmentSlot slotTipC = (EquipmentSlot)this.getSlot(this.inventoryStart + 5);
			EquipmentSlot slotPommel = (EquipmentSlot)this.getSlot(this.inventoryStart + 6);

			ItemEquipment itemEquipment = (ItemEquipment) equipmentSlot.getStack().getItem();
			int axeIndex = 0;
			for(ItemStack partStack : itemEquipment.getEquipmentPartStacks(equipmentSlot.getStack())) {
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
	 * @param equipmentSlot The equipment slot that changed. This must be any type of part slot.
	 */
	public void onEquipmentPartSlotChanged(EquipmentSlot equipmentSlot) {
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
			if(slot instanceof EquipmentSlot) {
				EquipmentSlot equipmentSlot = (EquipmentSlot)slot;
				equipmentSlot.putStackWithoutUpdate(ItemStack.EMPTY);
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