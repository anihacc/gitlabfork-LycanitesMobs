package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.tileentity.EquipmentInfuserTileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EquipmentInfuserContainerProvider implements MenuProvider {
	public EquipmentInfuserTileEntity equipmentInfuser;

	public EquipmentInfuserContainerProvider(@Nonnull EquipmentInfuserTileEntity equipmentInfuser) {
		this.equipmentInfuser = equipmentInfuser;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
		return new EquipmentInfuserContainer(windowId, playerInventory, this.equipmentInfuser);
	}

	@Override
	public Component getDisplayName() {
		return this.equipmentInfuser.getName();
	}
}
