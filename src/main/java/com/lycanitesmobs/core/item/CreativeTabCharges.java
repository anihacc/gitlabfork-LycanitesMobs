package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabCharges extends CreativeTabs {

	// ========== Constructor ==========
	public CreativeTabCharges(int tabID, String modID) {
		super(tabID, modID);
	}
	
	// ========== Tab Icon ==========
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getTabIconItem() {
		if(ObjectManager.getItem("hellfireballcharge") != null)
			return new ItemStack(ObjectManager.getItem("hellfireballcharge"));
		else if(ObjectManager.getItem("venomshotcharge") != null)
			return new ItemStack(ObjectManager.getItem("venomshotcharge"));
		else
			return new ItemStack(Items.FIRE_CHARGE);
	}
}