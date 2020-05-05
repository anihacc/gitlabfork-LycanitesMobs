package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

public class LMChargesGroup extends ItemGroup {

	// ========== Constructor ==========
	public LMChargesGroup(String modID) {
		super(modID);
	}


	// ========== Tab Icon ==========
	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack createIcon() {
		if(ObjectManager.getItem("hellfireballcharge") != null)
			return new ItemStack(ObjectManager.getItem("hellfireballcharge"));
		else if(ObjectManager.getItem("venomshotcharge") != null)
			return new ItemStack(ObjectManager.getItem("venomshotcharge"));
		else
			return new ItemStack(Items.FIRE_CHARGE);
	}
}