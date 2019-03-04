package com.lycanitesmobs.core.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.model.animation.ModelAnimationLayer;
import com.lycanitesmobs.core.renderer.IItemModelRenderer;
import com.lycanitesmobs.core.renderer.LayerItem;
import com.lycanitesmobs.core.renderer.RenderCreature;

import java.util.*;

public class ModelAnimation {

	/** A list of model texture layer definitions. **/
    public Map<String, ModelAnimationLayer> textureLayers = new HashMap<>();

	/** A list of part animations. **/
	//public List<ModelAnimationLayers> partAnimations = new ArrayList<>();


	/**
	 * Reads JSON data into this ObjPart.
	 * @param json The JSON data to read from.
	 */
	public void loadFromJson(JsonObject json) {

		// Texture Layers:
		if(json.has("textureLayers")) {
			Iterator<JsonElement> textureLayers = json.get("textureLayers").getAsJsonArray().iterator();
			while(textureLayers.hasNext()) {
				JsonObject jsonObject = textureLayers.next().getAsJsonObject();
				ModelAnimationLayer animationLayer = new ModelAnimationLayer();
				animationLayer.loadFromJson(jsonObject);
				this.textureLayers.put(animationLayer.name, animationLayer);
			}
		}
	}


	/**
	 * Adds creature layers from this Animation to the provided renderer.
	 * @param renderer The renderer to add the layers to.
	 */
	public void addCreatureLayers(RenderCreature renderer) {
		for(ModelAnimationLayer textureLayer : this.textureLayers.values()) {
			renderer.addLayer(textureLayer.createCreatureLayer(renderer));
		}
	}


	/**
	 * Adds item layers from this Animation to the provided renderer.
	 * @param renderer The renderer to add the layers to.
	 */
	public void addItemLayers(IItemModelRenderer renderer) {
		for(ModelAnimationLayer textureLayer : this.textureLayers.values()) {
			renderer.addLayer(textureLayer.createItemLayer(renderer));
		}
	}


	/**
	 * Returns a layer to use for the base texture or null if none is provided.
	 * @param renderer The renderer to add the layers to.
	 * @return Null or a base layer.
	 */
	public LayerItem getBaseLayer(IItemModelRenderer renderer) {
		if(this.textureLayers.containsKey("base")) {
			ModelAnimationLayer animationLayer = this.textureLayers.get("base");
			if(animationLayer != null) {
				return animationLayer.createItemLayer(renderer);
			}
		}
		return null;
	}
}