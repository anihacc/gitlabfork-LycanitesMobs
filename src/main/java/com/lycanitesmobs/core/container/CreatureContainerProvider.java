package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CreatureContainerProvider implements MenuProvider {
	public BaseCreatureEntity creature;

	public CreatureContainerProvider(@Nonnull BaseCreatureEntity creature) {
		this.creature = creature;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
		return new CreatureContainer(windowId, playerInventory, this.creature);
	}

	@Override
	public Component getDisplayName() {
		return this.creature.getDisplayName();
	}
}
