package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.localisation.LanguageManager;
import com.lycanitesmobs.core.model.ModelItemBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemInfo {

	// Core Info:
	/** The name of this item. Lowercase, no space, used for language entries and for generating the projectile id, etc. Required. **/
	protected String name;

	/** The entity class used by this item. Defaults to ItemGeneric but can be changed to special classes for unique behaviour, etc. **/
	public Class<? extends Item> itemClass;
	/** The model class used by this item. If null, the default vanilla json model loading is used. **/
	public Class<? extends ModelItemBase> modelClass;

	/** The group that this item belongs to. **/
	public ModInfo group;

	/**
	 * Constructor
	 * @param group The group that this item definition will belong to.
	 */
	public ItemInfo(ModInfo group) {
		this.group = group;
	}


	/** Loads this item from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		this.name = json.get("name").getAsString();
		try {
			this.itemClass = (Class<? extends Item>) Class.forName(json.get("itemClass").getAsString());
		}
		catch(Exception e) {
			LycanitesMobs.logWarning("", "[Projectile] Unable to find the Java Item Class: " + json.get("itemClass").getAsString() + " for " + this.getName());
		}
		try {
			this.modelClass = (Class<? extends ModelItemBase>) Class.forName(json.get("modelClass").getAsString());
		}
		catch(Exception e) {
			LycanitesMobs.logWarning("", "[Projectile] Unable to find the Java Model Class: " + json.get("modelClass").getAsString() + " for " + this.getName());
		}
	}


	/**
	 * Returns the name of this item, this is the unformatted lowercase name. Ex: cleansingcrystal
	 * @return Item name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the registry id of this item. Ex: elementalmobs:cleansingcrystal
	 * @return Item registry entity id.
	 */
	public String getEntityId() {
		return this.group.modid + ":" + this.getName();
	}

	/**
	 * Returns the resource location for this item.
	 * @return Item resource location.
	 */
	public ResourceLocation getResourceLocation() {
		return new ResourceLocation(this.group.modid, this.getName());
	}

	/**
	 * Returns the language key for this item. Ex: elementalmobs.cleansingcrystal
	 * @return Item language key.
	 */
	public String getLocalisationKey() {
		return this.group.modid + "." + this.getName();
	}

	/**
	 * Returns a translated title for this item. Ex: Cleansing Crystal
	 * @return The display name of this item.
	 */
	public String getTitle() {
		return LanguageManager.translate("item." + this.getLocalisationKey() + ".name");
	}

	/**
	 * Returns a translated description for this item.
	 * @return The display description of this item.
	 */
	public String getDescription() {
		return LanguageManager.translate("item." + this.getLocalisationKey() + ".description");
	}
}
