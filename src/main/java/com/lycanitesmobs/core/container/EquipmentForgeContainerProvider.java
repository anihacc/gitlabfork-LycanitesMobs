package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EquipmentForgeContainerProvider implements MenuProvider {
	public TileEntityEquipmentForge equipmentForge;

	public EquipmentForgeContainerProvider(@Nonnull TileEntityEquipmentForge equipmentForge) {
		this.equipmentForge = equipmentForge;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
		return new EquipmentForgeContainer(windowId, playerInventory, this.equipmentForge);
	}

	@Override
	public Component getDisplayName() {
		return this.equipmentForge.getName();
	}
}
