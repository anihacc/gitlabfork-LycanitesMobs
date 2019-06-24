package com.lycanitesmobs.core.item;

import com.lycanitesmobs.core.info.CreatureInfo;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

public class ItemColorCustomSpawnEgg implements IItemColor {
	@Override
	public int getColor(ItemStack itemStack, int tintIndex) {
		if(!(itemStack.getItem() instanceof ItemCustomSpawnEgg))
			return 16777215;

		ItemCustomSpawnEgg itemCustomSpawnEgg = (ItemCustomSpawnEgg)itemStack.getItem();
		CreatureInfo creatureInfo = itemCustomSpawnEgg.getCreatureInfo(itemStack);
		return creatureInfo != null ? (tintIndex == 0 ? creatureInfo.eggBackColor : creatureInfo.eggForeColor) : 16777215;
	}
}
