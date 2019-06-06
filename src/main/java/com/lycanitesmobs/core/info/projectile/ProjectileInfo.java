package com.lycanitesmobs.core.info.projectile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.EntityProjectileCustom;
import com.lycanitesmobs.core.helpers.JSONHelper;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.ItemCharge;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProjectileInfo {

	// Core Info:
	/** The name of this projectile. Lowercase, no space, used for language entries and for generating the projectile id, etc. Required. **/
	protected String name;

	/** The entity class used by this projectile. Defaults to EntityProjectileCustom but can be changed to special classes for unique behaviour, etc. **/
	public Class<? extends Entity> entityClass;
	/** The model class used by this projectile, if empty, the Charge Item is used instead. **/
	public Class<? extends ModelBase> modelClass;

	/** The group that this projectile belongs to. **/
	public ModInfo modInfo;

	// Item:
	/** The item used to fire this projectile from a dispenser and on use. **/
	public Item chargeItem;
	/** The name of the charge item for this projectile. Can be automatically generated using the name of this projectile or overridden. **/
	public String chargeItemName;
	/** The dispenser behaviour used by this projectile. If null, this item cannot be fired from a dispenser. **/
	public DispenserBehaviorBase dispenserBehaviour;

	// Stats:
	/** The width of the projectile. **/
	public double width = 0.8D;
	/** The height of the projectile. **/
	public double height = 0.75D;
	/** The scale of the projectile. **/
	public float scale = 0.5F;
	/** The base amount of damage that this projectile deals. **/
	public double damage = 0.75D;
	/** The base amount of damage caused by this projectile that can ignore armor and similar defenses. **/
	public double pierce = 1.0D;
	/** The base amount of speed that this projectile travels at. **/
	public double speed = 32.0D;
	/** How much gravity affects this projectile. **/
	public double weight = 1.0D;

	// Elements:
	/** The names of the elements of this projectile, affects buffs and debuffs amongst other things. **/
	protected List<String> elementNames = new ArrayList<>();
	/** The Elements of this projectile, affects buffs and debuffs amongst other things. **/
	public List<ElementInfo> elements = new ArrayList<>();

	// Effects:
	/** A list of effects that this projectile has. **/
	public List<ProjectileEffect> effects = new ArrayList<>();

	// Flags:
	/** If true, this projectile wont be destroyed when hitting the water or underwater. **/
	public boolean waterproof = false;
	/** If true, this projectile wont be destroyed when hitting the lava or submerged in lava. **/
	public boolean lavaproof = false;
	/** If true, this projectile will destroy long grass and similar blocks. **/
	public boolean cutGrass = false;


	/**
	 * Constructor
	 * @param modInfo The group that this projectile definition will belong to.
	 */
	public ProjectileInfo(ModInfo modInfo) {
		this.modInfo = modInfo;
	}


	/** Loads this projectile from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		this.name = json.get("name").getAsString();

		if(json.has("chargeItemName"))
			this.chargeItemName = json.get("chargeItemName").getAsString();
		else
			this.chargeItemName = this.name + "charge";

		if(json.has("entityClass")) {
			try {
				this.entityClass = (Class<? extends Entity>) Class.forName(json.get("entityClass").getAsString());
			} catch (Exception e) {
				LycanitesMobs.printWarning("", "[Projectile] Unable to find the Java Entity Class: " + json.get("entityClass").getAsString() + " for " + this.getName());
			}
		}

		if(json.has("modelClass")) {
			try {
				this.modelClass = (Class<? extends ModelBase>) Class.forName(json.get("modelClass").getAsString());
			} catch (Exception e) {
				LycanitesMobs.printWarning("", "[Projectile] Unable to find the Java Model Class: " + json.get("modelClass").getAsString() + " for " + this.getName());
			}
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

		if(json.has("elements")) {
			this.elementNames = JSONHelper.getJsonStrings(json.get("elements").getAsJsonArray());
		}

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
		return this.modInfo.filename + ":" + this.getName();
	}

	/**
	 * Returns the resource location for this projectile.
	 * @return Projectile resource location.
	 */
	public ResourceLocation getResourceLocation() {
		return new ResourceLocation(this.modInfo.filename, this.getName());
	}

	/**
	 * Returns the language key for this projectile. Ex: elementalmobs.chaosorb
	 * @return Creature language key.
	 */
	public String getLocalisationKey() {
		return this.modInfo.filename + "." + this.getName();
	}

	/**
	 * Returns a translated title for this projectile. Ex: Chaos Orb
	 * @return The display name of this projectile.
	 */
	public String getTitle() {
		return LanguageManager.translate("entity." + this.getLocalisationKey() + ".name");
	}

	/**
	 * Creates the projectile charge item.
	 */
	public void createChargeItem() {
		this.chargeItem = ObjectManager.getItem(this.chargeItemName);
		if(this.chargeItem != null) {
			return;
		}
		this.chargeItem = new ItemCharge(this);
		ObjectManager.addItem(this.chargeItemName, this.chargeItem);
		ObjectManager.addProjectile(this.name, this.entityClass, this.chargeItem, new DispenserBehaviorBase(), true);
	}

	/**
	 * Creates a projectile instance using this info.
	 * @param world The world to create the projectile in.
	 * @param entityLivingBase The entity that created the projectile.
	 */
	public EntityProjectileBase createProjectile(World world, EntityLivingBase entityLivingBase) {
		return new EntityProjectileCustom(world, entityLivingBase, this);
	}

	/**
	 * Creates a projectile instance using this info.
	 * @param world The world to create the projectile in.
	 * @param x The x position of the projectile.
	 * @param y The y position of the projectile.
	 * @param z The z position of the projectile.
	 */
	public EntityProjectileBase createProjectile(World world, double x, double y, double z) {
		return new EntityProjectileCustom(world, x, y, z, this);
	}

	public SoundEvent getLaunchSound() {
		return AssetManager.getSound(this.name + "_launch");
	}

	public SoundEvent getImpactSound() {
		return AssetManager.getSound(this.name + "_impact");
	}
}
