package com.lycanitesmobs.client.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.client.model.animation.ModelPartAnimation;
import com.lycanitesmobs.client.model.animation.TextureLayerAnimation;
import com.lycanitesmobs.client.renderer.IItemModelRenderer;
import com.lycanitesmobs.client.renderer.CreatureRenderer;
import com.lycanitesmobs.client.renderer.RenderProjectileModel;
import com.lycanitesmobs.client.renderer.layer.LayerItem;

import java.util.*;

public class ModelAnimation {

	/** A list of model texture layer definitions. **/
    public Map<String, TextureLayerAnimation> textureLayers = new HashMap<>();

	/** A list of part animations. **/
	public List<ModelPartAnimation> partAnimations = new ArrayList<>();


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
				TextureLayerAnimation animationLayer = new TextureLayerAnimation();
				animationLayer.loadFromJson(jsonObject);
				this.textureLayers.put(animationLayer.name, animationLayer);
			}
		}

		// Part Animations:
		if(json.has("partAnimations")) {
			Iterator<JsonElement> partAnimations = json.get("partAnimations").getAsJsonArray().iterator();
			while(partAnimations.hasNext()) {
				JsonObject jsonObject = partAnimations.next().getAsJsonObject();
				ModelPartAnimation partAnimation = new ModelPartAnimation();
				partAnimation.loadFromJson(jsonObject);
				this.partAnimations.add(partAnimation);
			}
		}
	}


	/**
	 * Adds creature layers from this Animation to the provided renderer.
	 * @param renderer The renderer to add the layers to.
	 */
	public void addCreatureLayers(CreatureRenderer renderer) {
		for(TextureLayerAnimation textureLayer : this.textureLayers.values()) {
			renderer.addLayer(textureLayer.createCreatureLayer(renderer));
		}
	}


	/**
	 * Adds projectile layers from this Animation to the provided renderer.
	 * @param renderer The renderer to add the layers to.
	 */
	public void addProjectileLayers(RenderProjectileModel renderer) {
		for(TextureLayerAnimation textureLayer : this.textureLayers.values()) {
			renderer.addLayer(textureLayer.createProjectileLayer(renderer));
		}
	}


	/**
	 * Adds item layers from this Animation to the provided renderer.
	 * @param renderer The renderer to add the layers to.
	 */
	public void addItemLayers(IItemModelRenderer renderer) {
		for(TextureLayerAnimation textureLayer : this.textureLayers.values()) {
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
			TextureLayerAnimation animationLayer = this.textureLayers.get("base");
			if(animationLayer != null) {
				return animationLayer.createItemLayer(renderer);
			}
		}
		return null;
	}
}
