package com.lycanitesmobs.core.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.core.model.animation.ModelAnimationLayer;
import com.lycanitesmobs.core.renderer.RenderCreature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModelAnimation {

	/** A list of model texture layer definitions. **/
    public List<ModelAnimationLayer> textureLayers = new ArrayList<>();

	/** A list of part animations. **/
	//public List<ModelAnimationLayers> partAnimations = new ArrayList<>();


	/**
	 * Reads JSON data into this ObjPart.
	 * @param json The JSON data to read from.
	 */
	public void loadFromJson(JsonObject json) {

		// Texture Layers:
		if(json.has("texture_layers")) {
			Iterator<JsonElement> textureLayers = json.get("texture_layers").getAsJsonArray().iterator();
			while(textureLayers.hasNext()) {
				JsonObject jsonObject = textureLayers.next().getAsJsonObject();
				ModelAnimationLayer animationLayer = new ModelAnimationLayer();
				animationLayer.loadFromJson(jsonObject);
				this.textureLayers.add(animationLayer);
			}
		}
	}


	/**
	 * Adds creature layers from this Animation to the provided renderer.
	 * @param renderer The renderer to add the layers to.
	 */
	public void addCreatureLayers(RenderCreature renderer) {
		for(ModelAnimationLayer textureLayer : this.textureLayers) {
			renderer.addLayer(textureLayer.createCreatureLayer(renderer));
		}
	}
}
