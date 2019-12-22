package com.lycanitesmobs.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public class ModelLoader implements IModelLoader {
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		// Loaded by ModelManager instead.
	}

	@Override
	public IModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
		return null;
	}
}
