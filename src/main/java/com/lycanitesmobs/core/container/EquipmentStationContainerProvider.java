package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.tileentity.EquipmentStationTileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EquipmentStationContainerProvider implements MenuProvider {
	public EquipmentStationTileEntity equipmentStation;

	public EquipmentStationContainerProvider(@Nonnull EquipmentStationTileEntity equipmentStation) {
		this.equipmentStation = equipmentStation;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
		return new EquipmentStationContainer(windowId, playerInventory, this.equipmentStation);
	}

	@Override
	public Component getDisplayName() {
		return this.equipmentStation.getName();
	}
}
