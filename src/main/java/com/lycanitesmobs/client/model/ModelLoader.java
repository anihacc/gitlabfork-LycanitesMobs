package com.lycanitesmobs.client.model;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;

public class ModelLoader implements ICustomModelLoader {
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		// Loaded by ModelManager instead.
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		return LycanitesMobs.MODID.equals(modelLocation.getNamespace());
	}

	@Override
	public IUnbakedModel loadModel(ResourceLocation modelLocation) {
		LycanitesMobs.logDebug("", "Model Loader Loading: " + modelLocation);
		return null;
	}
}
