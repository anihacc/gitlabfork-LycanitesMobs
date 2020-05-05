package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.client.renderer.layer.LayerItem;
import net.minecraft.util.Identifier;

import java.util.List;

public interface IItemModelRenderer {
	void bindItemTexture(Identifier location);

	List<LayerItem> addLayer(LayerItem renderLayer);
}
