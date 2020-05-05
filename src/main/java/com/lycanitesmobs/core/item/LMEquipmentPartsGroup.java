package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

public class LMEquipmentPartsGroup extends ItemGroup {
	private ItemStack iconStack = ItemStack.EMPTY;
	private boolean fallbackIcon = false;

	public LMEquipmentPartsGroup(String modID) {
		super(modID);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public ItemStack createIcon() {
		this.fallbackIcon = false;
		if(ObjectManager.getItem("equipmentpart_eechetikarm") != null)
			return new ItemStack(ObjectManager.getItem("equipmentpart_eechetikarm"));
		if(ObjectManager.getItem("equipmentpart_darklingskull") != null)
			return new ItemStack(ObjectManager.getItem("equipmentpart_darklingskull"));
		if(ObjectManager.getItem("equipmentpart_grueclaw") != null)
			return new ItemStack(ObjectManager.getItem("equipmentpart_grueclaw"));
		if(ObjectManager.getItem("equipmentpart_xaphanspine") != null)
			return new ItemStack(ObjectManager.getItem("equipmentpart_xaphanspine"));
		if(ObjectManager.getItem("equipmentpart_geonachfist") != null)
			return new ItemStack(ObjectManager.getItem("equipmentpart_geonachfist"));

		this.fallbackIcon = true;
		if(ObjectManager.getItem("geistliver") != null)
			return new ItemStack(ObjectManager.getItem("geistliver"));
		return new ItemStack(Items.BONE);
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getIcon() {
		if (this.iconStack.isEmpty() || this.fallbackIcon) {
			this.iconStack = this.createIcon();
		}
		return this.iconStack;
	}
}