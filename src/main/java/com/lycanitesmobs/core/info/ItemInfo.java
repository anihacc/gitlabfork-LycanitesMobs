package com.lycanitesmobs.core.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.GenericFoodItem;
import com.lycanitesmobs.core.item.GenericItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Iterator;

public class ItemInfo {
	/** The Item based on this Item Info. **/
	public Item item;

	// Core Info:
	/** The name of this item. Lowercase, no space, used for language entries and for generating the projectile id, etc. Required. **/
	protected String name;

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
		LycanitesMobs.logDebug("", "Loading item " + this.name + " from JSON...");

		// Model (Optional):
		String modelName = null;
		if(json.has("model")) {
			modelName = json.get("model").getAsString();
		}

		// Group:
		CreativeTabs group = LycanitesMobs.itemsTab;
		if(json.has("group")) {
			String groupName = json.get("group").getAsString();
			if("blocks".equalsIgnoreCase(groupName))
				group = LycanitesMobs.blocksTab;
			if("creatures".equalsIgnoreCase(groupName))
				group = LycanitesMobs.creaturesTab;
		}

		// Stack Size:
		int maxStackSize = 64;
		if(json.has("maxStackSize"))
			maxStackSize = json.get("maxStackSize").getAsInt();

		// Food:
		FoodInfo food = null;
		if(json.has("food")) {
			JsonObject foodJson = json.get("food").getAsJsonObject();
			food = new FoodInfo();
			food.hunger(foodJson.get("hunger").getAsInt());
			food.saturation(foodJson.get("saturation").getAsFloat());

			if(!foodJson.has("alwaysEdible") || foodJson.get("alwaysEdible").getAsBoolean())
				food.setAlwaysEdible();

			if(foodJson.has("fast") && foodJson.get("fast").getAsBoolean())
				food.fastToEat();

			if(foodJson.has("meat") && foodJson.get("meat").getAsBoolean())
				food.meat();

			if(foodJson.has("effects")) {
				JsonArray effectsJson = foodJson.getAsJsonArray("effects");
				Iterator<JsonElement> jsonIterator = effectsJson.iterator();
				while (jsonIterator.hasNext()) {
					JsonObject foodEffectJson = jsonIterator.next().getAsJsonObject();
					String effectId = foodEffectJson.get("effectId").getAsString();
					String[] effectIds = effectId.split(":");
					Potion effect;
					if("minecraft".equals(effectIds[0]))
						effect = ObjectLists.allEffects.get(effectIds[1]);
					else
						effect = ObjectManager.getEffect(effectIds[1]);
					if(effect == null) {
						LycanitesMobs.logWarning("", "[Items] Unable to add food effect: " + effectId + " to food item: " + this.name);
						continue;
					}
					PotionEffect effectInstance = new PotionEffect(effect, foodEffectJson.get("duration").getAsInt() * 20, foodEffectJson.get("amplifier").getAsInt());

					float chance = 1F;
					if(foodEffectJson.has("chance"))
						chance = foodEffectJson.get("chance").getAsFloat();

					food.effect(effectInstance, chance);
				}
			}
		}

		// Create Item Properties:
		ItemProperties properties = new ItemProperties();
		properties.group(group);
		properties.maxStackSize(maxStackSize);

		// Create Item:
		if(food != null) {
			properties.food(food);
			this.item = new GenericFoodItem(properties, this.name);
		}
		else {
			this.item = new GenericItem(properties, this.name);
			((GenericItem)this.item).modelName = modelName;
		}
	}

	/**
	 * Gets the Item based on this ItemInfo/
	 * @return The item.
	 */
	public Item getItem() {
		return this.item;
	}

	/**
	 * Returns the name of this item info, this is the unformatted lowercase name. Ex: cleansingcrystal
	 * @return Item name.
	 */
	public String getName() {
		return this.name;
	}
}
