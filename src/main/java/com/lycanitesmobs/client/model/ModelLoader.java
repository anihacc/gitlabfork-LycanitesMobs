package com.lycanitesmobs.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public class ModelLoader implements IModelLoader {
	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		// Loaded by ModelManager instead.
	}

	@Override
	public IModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
		return null;
	}
}
