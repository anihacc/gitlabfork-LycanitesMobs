package com.lycanitesmobs.core.model;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;

public class EquipmentPartModelLoader implements ICustomModelLoader {
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {

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
		return null; // TODO Unbaked models? No more reflection hack?
	}
}
