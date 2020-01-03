package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabItems extends CreativeTabs {
	
	// ========== Constructor ==========
	public CreativeTabItems(int tabID, String modID) {
		super(tabID, modID);
	}
	
	// ========== Tab Icon ==========
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getTabIconItem() {
		if(ObjectManager.getItem("soulgazer") != null)
			return new ItemStack(ObjectManager.getItem("soulgazer"));
		else if(ObjectManager.getItem("poisongland") != null)
			return new ItemStack(ObjectManager.getItem("poisongland"));
		else
			return new ItemStack(Items.EMERALD);
	}
}