package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CreatureContainerProvider implements INamedContainerProvider {
	public EntityCreatureBase creature;

	public CreatureContainerProvider(@Nonnull EntityCreatureBase creature) {
		this.creature = creature;
	}

	@Nullable
	@Override
	public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new ContainerCreature(windowId, playerInventory, this.creature);
	}

	@Override
	public ITextComponent getDisplayName() {
		return this.creature.getDisplayName();
	}
}
