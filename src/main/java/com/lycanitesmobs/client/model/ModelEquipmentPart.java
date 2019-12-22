package com.lycanitesmobs.client.model;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class ModelEquipmentPart extends ModelItemBase {



	// ==================================================
	//                    Constructor
	// ==================================================
	public ModelEquipmentPart(ItemEquipmentPart equipmentPart) {
		this.initModel(equipmentPart.itemName, equipmentPart.modInfo, "equipment/" + equipmentPart.itemName.replace("equipmentpart_", ""));
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

		String textureName = itemEquipmentPart.itemName.toLowerCase().replace("equipmentpart_", "") + suffix;
		if(TextureManager.getTexture(textureName) == null)
			TextureManager.addTexture(textureName, itemEquipmentPart.modInfo, "textures/equipment/" + textureName + ".png");
		return TextureManager.getTexture(textureName);
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

		Vec3d partColor = itemEquipmentPart.getColor(itemStack);
		return new Vector4f((float)partColor.getX(), (float)partColor.getY(), (float)partColor.getZ(), 1);
	}
}
