package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.renderer.IItemModelRenderer;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class LayerItemDye extends LayerItem {

	public LayerItemDye(IItemModelRenderer renderer, String name) {
		super(renderer, name);
	}

	@Override
	public Vector4f getPartColor(String partName, ItemStack itemStack, float loop) {
		if(!(itemStack.getItem() instanceof ItemEquipmentPart)) {
			return super.getPartColor(partName, itemStack, loop);
		}
		ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)itemStack.getItem();
		Vec3d partColor = itemEquipmentPart.getColor(itemStack);
		return new Vector4f((float)partColor.getX(), (float)partColor.getY(), (float)partColor.getZ(), 1);
	}
}
