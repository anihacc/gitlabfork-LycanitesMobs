package com.lycanitesmobs.core.model.animation;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.renderer.IItemModelRenderer;
import com.lycanitesmobs.core.renderer.LayerEffect;
import com.lycanitesmobs.core.renderer.RenderCreature;
import net.minecraft.util.math.Vec2f;

import java.util.ArrayList;
import java.util.List;

public class ModelAnimationLayer {

	/** The name of this layer. If set to "base" this will apply animations to the base texture layer. **/
    public String name = "base";

    /** The texture suffix for loading sub textures. If empty, the base texture is used. **/
	public String textureSuffix = "";

	/** If true, the subspecies name is included when finding the texture. This only applies to subspecies colors as subspecies skins load a different model. **/
	public boolean subspeciesTexture = true;

	/** If true, lighting is disabled when rendering this layer for glowing in the dark, etc. **/
	public boolean glow = false;

	/** The texture blending style to use. Can be "normal", "additive" or "subtractive". **/
	public String blending = "normal";

	/** The scrolling speeds to use, if 0 the texture isn't scrolled in that direction. **/
	public Vec2f scrollSpeed = new Vec2f(0, 0);


	/**
	 * Reads JSON data into this Animation Layer.
	 * @param json The JSON data to read from.
	 */
	public void loadFromJson(JsonObject json) {
		if(json.has("name"))
			this.name = json.get("name").getAsString().toLowerCase();

		if(json.has("texture_suffix"))
			this.textureSuffix = json.get("texture_suffix").getAsString();

		if(json.has("subspecies_texture"))
			this.subspeciesTexture = json.get("subspecies_texture").getAsBoolean();

		if(json.has("glow"))
			this.glow = json.get("glow").getAsBoolean();

		if(json.has("blending"))
			this.blending = json.get("blending").getAsString().toLowerCase();

		float scrollSpeedX = 0;
		if(json.has("scroll_speed_x"))
			scrollSpeedX = json.get("scroll_speed_x").getAsFloat();
		float scrollSpeedY = 0;
		if(json.has("scroll_speed_y"))
			scrollSpeedY = json.get("scroll_speed_y").getAsFloat();
		this.scrollSpeed = new Vec2f(scrollSpeedX, scrollSpeedY);
	}


	/**
	 * Creates a new Creature Layer Renderer instance based on this Animation Layer.
	 * @param renderer The creature renderer to use for the layer.
	 * @return A new Layer Renderer.
	 */
	public LayerEffect createCreatureLayer(RenderCreature renderer) {
		int blendingId = LayerEffect.BLEND.NORMAL.id;
		if("additive".equals(this.blending)) {
			blendingId = LayerEffect.BLEND.ADD.id;
		}
		else if("subtractive".equals(this.blending)) {
			blendingId = LayerEffect.BLEND.SUB.id;
		}

		LayerEffect renderLayer = new LayerEffect(renderer, this.textureSuffix, this.glow, blendingId, this.subspeciesTexture);
		renderLayer.scrollSpeed = this.scrollSpeed;
		return renderLayer;
	}


	/**
	 * Creates a new Item Layer Renderer instance based on this Animation Layer.
	 * @param renderer The item renderer to use for the layer.
	 * @return A new Layer Renderer.
	 */
	public void createItemLayer(IItemModelRenderer renderer) {
		// TODO Add Item Layers
	}
}
