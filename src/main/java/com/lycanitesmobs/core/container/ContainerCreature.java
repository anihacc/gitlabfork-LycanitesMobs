package com.lycanitesmobs.core.container;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.extensions.IForgeContainerType;

public class ContainerCreature extends ContainerBase {
	public static final ContainerType<ContainerCreature> TYPE = (ContainerType<ContainerCreature>)IForgeContainerType.create(ContainerCreature::new).setRegistryName(LycanitesMobs.MODID, "creature");
	public EntityCreatureBase creature;

	/**
	 * Client Constructor
	 * @param windowId The window id for the gui screen to use.
	 * @param playerInventory The accessing player's inventory.
	 * @param extraData A packet sent from the server to create the Container from.
	 */
	public ContainerCreature(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
		this(windowId, playerInventory, (EntityCreatureBase)playerInventory.player.getEntityWorld().getEntityByID(extraData.readInt()));
	}

	/**
	 * Main Constructor
	 * @param windowId The window id for the gui screen to use.
	 * @param playerInventory The accessing player's inventory.
	 * @param creature The creature to access.
	 */
	public ContainerCreature(int windowId, PlayerInventory playerInventory, EntityCreatureBase creature) {
		super(TYPE, windowId);
		this.creature = creature;

		// Player Inventory:
		this.addPlayerSlots(playerInventory, 0, 0);

		// Creature Equipment:
		this.specialStart = this.inventorySlots.size();
		this.drawCreatureEquipment(creature, 8, 18);
		this.specialFinish = this.inventorySlots.size() - 1;

		// Creature Inventory
		this.inventoryStart = this.inventorySlots.size();
		if(creature.inventory.getItemSlotsSize() > 0)
			this.addSlotsByColumn(creature.inventory, 8 + (18 * 4), 18, 5, 0, creature.inventory.getActiveItemSlotsSize() - 1);
		this.inventoryFinish = this.inventorySlots.size() - 1;
	}


	
	
	// ==================================================
  	//                    Draw Slots
  	// ==================================================
	public void drawCreatureEquipment(EntityCreatureBase creature, int equipX, int equipY) {
		// Creature Accessories:
		if(creature instanceof EntityCreatureRideable) {
			this.addSlot(creature.inventory, creature.inventory.getSlotFromType("saddle"), equipX, equipY);
			equipY += 18;
		}
		if(creature.getBagSize() > 0) {
			this.addSlot(creature.inventory, creature.inventory.getSlotFromType("bag"), equipX, equipY);
			equipY += 18;
		}

		// Weapon and Dye slots will go here.
		
		// Creature Armor:
		equipX += 18;
		equipY = 18;
		if(creature.inventory.useAdvancedArmor()) {
			this.addSlot(creature.inventory, creature.inventory.getSlotFromType("head"), equipX, equipY);
			equipY += 18;
		}
		this.addSlot(creature.inventory, creature.inventory.getSlotFromType("chest"), equipX, equipY);
		equipY += 18;
		if(creature.inventory.useAdvancedArmor()) {
			this.addSlot(creature.inventory, creature.inventory.getSlotFromType("legs"), equipX, equipY);
			equipY += 18;
			this.addSlot(creature.inventory, creature.inventory.getSlotFromType("feet"), equipX, equipY);
			equipY += 18;
		}
	}
	
	
	// ==================================================
  	//                  Container Closed
  	// ==================================================
	@Override
	public void onContainerClosed(PlayerEntity player) {
		super.onContainerClosed(player);
	}
}
