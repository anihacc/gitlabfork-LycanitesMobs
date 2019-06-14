package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LMCreaturesGroup extends ItemGroup {
	
	// ========== Constructor ==========
	public LMCreaturesGroup(int tabID, String modID) {
		super(tabID, modID);
	}
	
	// ========== Tab Icon ==========
	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack createIcon() {
		if(ObjectManager.getItem("beastspawn") != null)
			return new ItemStack(ObjectManager.getItem("beastspawn"));
		else if(ObjectManager.getItem("demonspawn") != null)
			return new ItemStack(ObjectManager.getItem("demonspawn"));
		else if(ObjectManager.getItem("avianspawn") != null)
			return new ItemStack(ObjectManager.getItem("avianspawn"));
		else if(ObjectManager.getItem("arthropodspawn") != null)
			return new ItemStack(ObjectManager.getItem("arthropodspawn"));
		else
			return new ItemStack(Items.CREEPER_SPAWN_EGG);
	}
}