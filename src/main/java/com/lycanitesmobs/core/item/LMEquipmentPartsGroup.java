package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LMEquipmentPartsGroup extends ItemGroup {

	// ========== Constructor ==========
	public LMEquipmentPartsGroup(int tabID, String modID) {
		super(tabID, modID);
	}
	
	// ========== Tab Icon ==========
	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack createIcon() {
		if(ObjectManager.getItem("darklingskull") != null)
			return new ItemStack(ObjectManager.getItem("darklingskull"));
		else if(ObjectManager.getItem("grueclaw") != null)
			return new ItemStack(ObjectManager.getItem("grueclaw"));
		else if(ObjectManager.getItem("xaphanspine") != null)
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