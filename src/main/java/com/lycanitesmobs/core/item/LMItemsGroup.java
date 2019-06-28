package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LMItemsGroup extends ItemGroup {
	
	// ========== Constructor ==========
	public LMItemsGroup(String modID) {
		super(modID);
	}


	// ========== Tab Icon ==========
	@OnlyIn(Dist.CLIENT)
	@Override
	public ItemStack createIcon() {
		if(ObjectManager.getItem("soulgazer") != null)
			return new ItemStack(ObjectManager.getItem("soulgazer"));
		else if(ObjectManager.getItem("HellfireCharge") != null)
			return new ItemStack(ObjectManager.getItem("HellfireCharge"));
		else if(ObjectManager.getItem("JoustMeat") != null)
			return new ItemStack(ObjectManager.getItem("JoustMeat"));
		else if(ObjectManager.getItem("PoisonGland") != null)
			return new ItemStack(ObjectManager.getItem("PoisonGland"));
		else
			return new ItemStack(Items.EMERALD);
	}
}