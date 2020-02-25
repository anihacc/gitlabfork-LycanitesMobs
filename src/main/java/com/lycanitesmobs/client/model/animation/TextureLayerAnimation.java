package com.lycanitesmobs.client.model.animation;

import com.google.gson.JsonObject;
import com.lycanitesmobs.client.renderer.IItemModelRenderer;
import com.lycanitesmobs.client.renderer.layer.LayerEffect;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.lycanitesmobs.client.renderer.RenderCreature;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

public class TextureLayerAnimation {

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
	public Vector2f scrollSpeed = new Vector2f(0, 0);

	/** The color fading speeds to use, if 0 the color isn't faded. **/
	public Vector4f colorFadeSpeed;


	/**
	 * Reads JSON data into this Animation Layer.
	 * @param json The JSON data to read from.
	 */
	public void loadFromJson(JsonObject json) {
		this.name = json.get("name").getAsString().toLowerCase();

		if(json.has("textureSuffix"))
			this.textureSuffix = json.get("textureSuffix").getAsString();

		if(json.has("subspeciesTexture"))
			this.subspeciesTexture = json.get("subspeciesTexture").getAsBoolean();

		if(json.has("glow"))
			this.glow = json.get("glow").getAsBoolean();

		if(json.has("blending"))
			this.blending = json.get("blending").getAsString().toLowerCase();

		float scrollSpeedX = 0;
		if(json.has("scrollSpeedX"))
			scrollSpeedX = json.get("scrollSpeedX").getAsFloat();
		float scrollSpeedY = 0;
		if(json.has("scrollSpeedY"))
			scrollSpeedY = json.get("scrollSpeedY").getAsFloat();
		this.scrollSpeed = new Vector2f(scrollSpeedX, scrollSpeedY);

		float colorFadeRed = 0;
		if(json.has("colorFadeRed"))
			colorFadeRed = json.get("colorFadeRed").getAsFloat();
		float colorFadeGreen = 0;
		if(json.has("colorFadeGreen"))
			colorFadeGreen = json.get("colorFadeGreen").getAsFloat();
		float colorFadeBlue = 0;
		if(json.has("colorFadeBlue"))
			colorFadeBlue = json.get("colorFadeBlue").getAsFloat();
		float colorFadeAlpha = 0;
		if(json.has("colorFadeAlpha"))
			colorFadeAlpha = json.get("colorFadeAlpha").getAsFloat();
		if(colorFadeRed != 0 || colorFadeGreen != 0 || colorFadeBlue != 0 || colorFadeAlpha != 0)
			this.colorFadeSpeed = new Vector4f(colorFadeRed, colorFadeGreen, colorFadeBlue, colorFadeAlpha);
	}


	/**
	 * Creates a new Creature Layer Renderer instance based on this Animation Layer.
	 * @param renderer The creature renderer to use for the layer.
	 * @return A new Layer Renderer.
	 */
	public LayerEffect createCreatureLayer(RenderCreature renderer) {
		int blendingId = LayerEffect.BLEND.NORMAL.id;
		if("add".equals(this.blending)) {
			blendingId = LayerEffect.BLEND.ADD.id;
		}
		else if("sub".equals(this.blending)) {
			blendingId = LayerEffect.BLEND.SUB.id;
		}

		LayerEffect renderLayer = new LayerEffect(renderer, this.textureSuffix, this.glow, blendingId, this.subspeciesTexture);
		renderLayer.name = this.name;
		renderLayer.scrollSpeed = this.scrollSpeed;
		return renderLayer;
	}


	/**
	 * Creates a new Item Layer Renderer instance based on this Animation Layer.
	 * @param renderer The item renderer to use for the layer.
	 * @return A new Layer Renderer.
	 */
	public LayerItem createItemLayer(IItemModelRenderer renderer) {
		int blendingId = LayerEffect.BLEND.NORMAL.id;
		if("additive".equals(this.blending)) {
			blendingId = LayerEffect.BLEND.ADD.id;
		}
		else if("subtractive".equals(this.blending)) {
			blendingId = LayerEffect.BLEND.SUB.id;
		}

		LayerItem renderLayer = new LayerItem(renderer, this.name);
		renderLayer.textureSuffix = this.textureSuffix;
		renderLayer.glow = this.glow;
		renderLayer.blending = blendingId;
		renderLayer.scrollSpeed = this.scrollSpeed;
		renderLayer.colorFadeSpeed = this.colorFadeSpeed;

		return renderLayer;
	}
}
