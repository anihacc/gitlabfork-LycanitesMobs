package com.lycanitesmobs.core.info.projectile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import com.lycanitesmobs.core.helpers.JSONHelper;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProjectileInfo {

	// Core Info:
	/** The name of this projectile. Lowercase, no space, used for language entries and for generating the projectile id, etc. Required. **/
	protected String name;

	/** The entity class used by this projectile. Defaults to EntityProjectileGeneric but can be changed to special classes for unique behaviour, etc. **/
	public Class<? extends Entity> entityClass;
	/** The model class used by this projectile. **/
	public Class<? extends ModelBase> modelClass;

	/** The group that this projectile belongs to. **/
	public ModInfo group;

	// Stats:
	public double width = 0.8D;
	public double height = 1.8D;
	/** The base amount of damage that this projectile deals. **/
	public double damage = 2.0D;
	/** The base amount of damage caused by this projectile that can ignore armor and similar defenses. **/
	public double pierce = 1.0D;
	/** The base amount of speed that this projectile travels at. **/
	public double speed = 32.0D;

	/** A list of effects that this projectile has. **/
	public List<ProjectileEffect> effects = new ArrayList<>();

	/** If true, this projectile wont be destroyed when hitting the water or underwater. **/
	public boolean waterproof = false;
	/** If true, this projectile wont be destroyed when hitting the lava or submerged in lava. **/
	public boolean lavaproof = false;
	/** If true, this projectile will destroy long grass and similar blocks. **/
	public boolean cutGrass = false;

	// Type:
	/** The names of the Elements of this projectile, affects buffs and debuffs amongst other things. **/
	protected List<String> elementNames = new ArrayList<>();
	/** The Elements of this projectile, affects buffs and debuffs amongst other things. **/
	public List<ElementInfo> elements = new ArrayList<>();

	// Item:
	/** The item used to fire this projectile from a dispenser. **/
	public Item dispenserItem;
	/** The dispenser behaviour used by this projectile. If null, this item cannot be fired from a dispenser. **/
	public DispenserBehaviorBase dispenserBehaviour;


	/**
	 * Constructor
	 * @param group The group that this projectile definition will belong to.
	 */
	public ProjectileInfo(ModInfo group) {
		this.group = group;
	}


	/** Loads this projectile from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		this.name = json.get("name").getAsString();
		try {
			this.entityClass = (Class<? extends Entity>) Class.forName(json.get("entityClass").getAsString());
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "[Projectile] Unable to find the Java Entity Class: " + json.get("entityClass").getAsString() + " for " + this.getName());
		}
		try {
			this.modelClass = (Class<? extends ModelBase>) Class.forName(json.get("modelClass").getAsString());
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "[Projectile] Unable to find the Java Model Class: " + json.get("modelClass").getAsString() + " for " + this.getName());
		}

		if(json.has("width"))
			this.width = json.get("width").getAsDouble();
		if(json.has("height"))
			this.height = json.get("height").getAsDouble();
		if(json.has("damage"))
			this.damage = json.get("damage").getAsDouble();
		if(json.has("pierce"))
			this.pierce = json.get("pierce").getAsDouble();
		if(json.has("speed"))
			this.speed = json.get("speed").getAsDouble();

		if(json.has("effects")) {
			JsonArray jsonArray = json.get("effects").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				JsonObject effectJson = jsonIterator.next().getAsJsonObject();
				ProjectileEffect projectileEffect = ProjectileEffect.createFromJSON(effectJson);
				this.effects.add(projectileEffect);
			}
		}

		if(json.has("waterproof"))
			this.waterproof = json.get("waterproof").getAsBoolean();
		if(json.has("lavaproof"))
			this.lavaproof = json.get("lavaproof").getAsBoolean();
		if(json.has("cutGrass"))
			this.cutGrass = json.get("cutGrass").getAsBoolean();

		if(json.has("elements")) {
			this.elementNames = JSONHelper.getJsonStrings(json.get("elements").getAsJsonArray());
		}
	}


	/**
	 * Returns the name of this projectile, this is the unformatted lowercase name. Ex: chaosorb
	 * @return Projectile name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the registry id of this projectile. Ex: elementalmobs:chaosorb
	 * @return Projectile registry entity id.
	 */
	public String getEntityId() {
		return this.group.filename + ":" + this.getName();
	}

	/**
	 * Returns the resource location for this projectile.
	 * @return Projectile resource location.
	 */
	public ResourceLocation getResourceLocation() {
		return new ResourceLocation(this.group.filename, this.getName());
	}

	/**
	 * Returns the language key for this projectile. Ex: elementalmobs.chaosorb
	 * @return Creature language key.
	 */
	public String getLocalisationKey() {
		return this.group.filename + "." + this.getName();
	}

	/**
	 * Returns a translated title for this projectile. Ex: Chaos Orb
	 * @return The display name of this projectile.
	 */
	public String getTitle() {
		return I18n.translateToLocal("entity." + this.getLocalisationKey() + ".name");
	}
}
