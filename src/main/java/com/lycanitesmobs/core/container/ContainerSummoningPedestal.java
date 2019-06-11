package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.inventory.ContainerBase;
import com.lycanitesmobs.core.inventory.SlotBase;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerSummoningPedestal extends ContainerBase {
    public TileEntitySummoningPedestal summoningPedestal;

    // ========================================
    //                Constructor
    // ========================================
    public ContainerSummoningPedestal(TileEntitySummoningPedestal summoningPedestal, InventoryPlayer playerInventory) {
        super();
        this.summoningPedestal = summoningPedestal;
        this.inventoryStart = this.inventorySlots.size();

        // Player Inventory
        this.playerInventoryStart = this.inventorySlots.size();
        this.addSlotGrid(playerInventory, 8, 171, 1, 0, 8);

        // Pedestal Inventory
        int slots = 0;
        if(summoningPedestal.getSizeInventory() > 0) {
            this.addSlotToContainer(new SlotBase(summoningPedestal, slots++, 93, 43));
        }
        this.inventoryFinish = this.inventoryStart + slots;
    }


    // ========================================
    //                  Interact
    // ========================================
    @Override
    public boolean canInteractWith(PlayerEntity player) {
        if(this.summoningPedestal == null)
            return false;
        return player == this.summoningPedestal.getPlayer();
    }


    // ========================================
    //                 Inventory
    // ========================================
    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int slotID) {
        return ItemStack.EMPTY;
    }
}
