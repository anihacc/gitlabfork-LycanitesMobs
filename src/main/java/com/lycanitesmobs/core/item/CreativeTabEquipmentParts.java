package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabEquipmentParts extends CreativeTabs {

	// ========== Constructor ==========
	public CreativeTabEquipmentParts(int tabID, String modID) {
		super(tabID, modID);
	}
	
	// ========== Tab Icon ==========
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getTabIconItem() {
		if(ObjectManager.getItem("xaphanspine") != null)
			return new ItemStack(ObjectManager.getItem("xaphanspine"));
		else if(ObjectManager.getItem("geonachfist") != null)
			return new ItemStack(ObjectManager.getItem("geonachfist"));
		else if(ObjectManager.getItem("HellfireCharge") != null)
			return new ItemStack(ObjectManager.getItem("HellfireCharge"));
		else if(ObjectManager.getItem("JoustMeat") != null)
			return new ItemStack(ObjectManager.getItem("JoustMeat"));
		else if(ObjectManager.getItem("PoisonGland") != null)
			return new ItemStack(ObjectManager.getItem("PoisonGland"));
		else
			return new ItemStack(Items.BONE);
	}
}