package com.lycanitesmobs.core.renderer;

import net.minecraft.util.ResourceLocation;

import java.util.List;

public interface IItemModelRenderer {
	void bindItemTexture(ResourceLocation location);

	List<LayerItem> addLayer(LayerItem renderLayer);
}
