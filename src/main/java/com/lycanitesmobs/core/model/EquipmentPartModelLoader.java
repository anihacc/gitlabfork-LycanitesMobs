package com.lycanitesmobs.core.model;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.equipment.EquipmentPartManager;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;

public class EquipmentPartModelLoader implements ICustomModelLoader {
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		for(ItemEquipmentPart itemEquipmentPart : EquipmentPartManager.getInstance().equipmentParts.values()) {
			AssetManager.addItemModel(itemEquipmentPart.itemName, new ModelEquipmentPart(itemEquipmentPart.itemName, itemEquipmentPart.modInfo));
		}
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		if(LycanitesMobs.modid.equals(modelLocation.getNamespace()) && "equipmentpart".equals(modelLocation.getPath())) {
			return true;
		}
		return false;
	}

	@Override
	public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception {
		LycanitesMobs.logDebug("", "Equipment Part Loader: " + modelLocation);
		return null; // TODO Unbaked models? No more reflection hack?
	}
}
