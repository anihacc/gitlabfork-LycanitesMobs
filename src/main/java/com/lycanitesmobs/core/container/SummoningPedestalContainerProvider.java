package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SummoningPedestalContainerProvider implements MenuProvider {
	public TileEntitySummoningPedestal summoningPedestal;

	public SummoningPedestalContainerProvider(@Nonnull TileEntitySummoningPedestal summoningPedestal) {
		this.summoningPedestal = summoningPedestal;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
		return new SummoningPedestalContainer(windowId, playerInventory, this.summoningPedestal);
	}

	@Override
	public Component getDisplayName() {
		return this.summoningPedestal.getName();
	}
}
