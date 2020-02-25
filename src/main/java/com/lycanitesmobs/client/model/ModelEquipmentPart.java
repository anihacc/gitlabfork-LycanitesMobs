package com.lycanitesmobs.client.model;

import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Vector4f;

public class ModelEquipmentPart extends ModelItemBase {



	// ==================================================
	//                    Constructor
	// ==================================================
	public ModelEquipmentPart(String name, ModInfo groupInfo) {
		this.initModel(name, groupInfo, "equipment/" + name);
	}


	// ==================================================
	//                   Get Texture
	// ==================================================
	@Override
	public ResourceLocation getTexture(ItemStack itemStack, LayerItem layer) {
		if(!(itemStack.getItem() instanceof ItemEquipmentPart)) {
			return null;
		}
		ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)itemStack.getItem();

		String suffix = "";
		if(layer != null && layer.textureSuffix != null && !layer.textureSuffix.isEmpty()) {
			suffix = "_" + layer.textureSuffix;
		}
		return itemEquipmentPart.getTexture(itemStack, suffix);
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	@Override
	public Vector4f getPartColor(String partName, ItemStack itemStack, LayerItem layer, float loop) {
		if(!(itemStack.getItem() instanceof ItemEquipmentPart)) {
			return super.getPartColor(partName, itemStack, layer, loop);
		}
		ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)itemStack.getItem();

		if(layer != null) {
			return layer.getPartColor(partName, itemStack, loop);
		}

		return itemEquipmentPart.getColor(itemStack);
	}
}
