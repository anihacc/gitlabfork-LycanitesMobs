package com.lycanitesmobs.client.model;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;

public class EquipmentPartModelLoader implements ICustomModelLoader {
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		// Loaded by ModelManager instead.
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		if(LycanitesMobs.MODID.equals(modelLocation.getNamespace()) && "equipmentpart".equals(modelLocation.getPath())) {
			return true;
		}
		return false;
	}

	@Override
	public IUnbakedModel loadModel(ResourceLocation modelLocation) {
		LycanitesMobs.logDebug("", "Equipment Part Loader: " + modelLocation);
		return null;
	}
}
