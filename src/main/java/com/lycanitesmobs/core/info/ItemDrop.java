package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.sun.istack.internal.NotNull;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ItemDrop {
	// ========== Item ==========
	protected String itemId;
	protected int metadata;

	protected String burningItemId;
	protected int burningMetadata;

	protected Map<Integer, String> effectItemIds = new HashMap<>();
	protected Map<Integer, Integer> effectItemMetadata = new HashMap<>();
	
	public int minAmount = 1;
	public int maxAmount = 1;
	
	public float chance = 0;

    /** The ID of the subspecies that this drop is restricted to. An ID below 0 will have this drop ignore the subspecies. **/
    public int subspeciesID = -1;


	// ==================================================
	//                       JSON
	// ==================================================
	/** Creates a MobDrop from the provided JSON data. **/
	public static ItemDrop createFromJSON(JsonObject json) {
		ItemDrop itemDrop = null;
		int itemMetadata = 0;
		if(json.has("item")) {
			if(json.has("metadata")) {
				itemMetadata = json.get("metadata").getAsInt();
			}
			String itemId = json.get("item").getAsString();
			itemDrop = new ItemDrop(itemId, itemMetadata, 1);
			itemDrop.loadFromJSON(json);
		}
		else {
			LycanitesMobs.printWarning("", "[JSON] Unable to load item drop from json as it has no item id!");
		}

		return itemDrop;
	}


	// ==================================================
	//                      Config
	// ==================================================
	/** Creates a MobDrop from the provided Config String. **/
	public static ItemDrop createFromConfigString(String itemDropString) {
		if(itemDropString != null && itemDropString.length() > 0) {
			String[] customDropValues = itemDropString.split(",");
			String itemId = customDropValues[0];
			int itemMetadata = 0;
			if (customDropValues.length > 1) {
				itemMetadata = Integer.parseInt(customDropValues[1]);
			}
			int amountMin = 1;
			if (customDropValues.length > 2) {
				amountMin = Integer.parseInt(customDropValues[2]);
			}
			int amountMax = 1;
			if (customDropValues.length > 3) {
				amountMax = Integer.parseInt(customDropValues[3]);
			}
			float chance = 1;
			if (customDropValues.length > 4) {
				chance = Float.parseFloat(customDropValues[4]);
			}

			ItemDrop itemDrop = new ItemDrop(itemId, itemMetadata, chance);
			itemDrop.setMinAmount(amountMin);
			itemDrop.setMaxAmount(amountMax);

			return itemDrop;
		}
		return null;
	}

	
    // ==================================================
   	//                     Constructor
   	// ==================================================
	public ItemDrop(String itemId, int metadata, float chance) {
		this.itemId = itemId;
		this.metadata = metadata;
		this.chance = chance;
	}

	public ItemDrop(NBTTagCompound nbtTagCompound) {
		this.readFromNBT(nbtTagCompound);
	}

	public void loadFromJSON(JsonObject json) {
		if(json.has("minAmount"))
			this.minAmount = json.get("minAmount").getAsInt();
		if(json.has("maxAmount"))
			this.maxAmount = json.get("maxAmount").getAsInt();
		if(json.has("chance"))
			this.chance = json.get("chance").getAsFloat();
		if(json.has("subspecies"))
			this.subspeciesID = json.get("subspecies").getAsInt();

		if(json.has("burningItem")) {
			this.burningItemId = json.get("burningItem").getAsString();
			if(json.has("burningMetadata")) {
				this.burningMetadata = json.get("burningMetadata").getAsInt();
			}
		}
	}


    // ==================================================
   	//                     Properties
   	// ==================================================
	public ItemDrop setDrop(ItemStack itemStack) {
		this.itemId = itemStack.getItem().getRegistryName().toString();
		this.metadata = itemStack.getMetadata();
		return this;
	}

	public ItemDrop setBurningDrop(ItemStack itemStack) {
		this.burningItemId = itemStack.getItem().getRegistryName().toString();
		this.burningMetadata = itemStack.getMetadata();
		return this;
	}

	public ItemDrop setEffectDrop(int effectID, ItemStack itemStack) {
		this.effectItemIds.put(effectID, itemStack.getItem().getRegistryName().toString());
		this.effectItemMetadata.put(effectID, itemStack.getMetadata());
		return this;
	}

	public ItemDrop setMinAmount(int amount) {
		this.minAmount = amount;
		return this;
	}

	public ItemDrop setMaxAmount(int amount) {
		this.maxAmount = amount;
		return this;
	}

	public ItemDrop setChance(float chance) {
		this.chance = chance;
		return this;
	}

    public ItemDrop setSubspecies(int subspeciesID) {
        this.subspeciesID = subspeciesID;
        return this;
    }


	/**
	 * Returns a quantity to drop.
	 * @param random The instance of random to use.
	 * @param bonus A bonus multiplier.
	 * @return The randomised amount to drop.
	 */
	public int getQuantity(Random random, int bonus) {
		// Will It Drop?
		float roll = random.nextFloat();
		roll = Math.max(roll, 0);
		if(roll > this.chance)
			return 0;
		
		// How Many?
		int min = this.minAmount;
		int max = this.maxAmount + bonus;
		if(max <= min)
			return min;
		roll = roll / this.chance;
		float dropRange = (max - min) * roll;
		int dropAmount = min + Math.round(dropRange);
		return dropAmount;
	}

	/**
	 * Gets the base itemstack for this item drop.
	 * @return The base itemstack to drop.
	 */
	@NotNull
	public ItemStack getItemStack() {
		if(this.itemId == null) {
			return ItemStack.EMPTY;
		}

		Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(this.itemId));
		if(item != null) {
			return new ItemStack(item, 1, this.metadata);
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Gets the itemstack that burning entities should drop.
	 * @return The burning itemstack or the base itemstack if not set.
	 */
	@NotNull
	public ItemStack getBurningItemStack() {
		if(this.burningItemId == null) {
			return this.getItemStack();
		}

		Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(this.burningItemId));
		if(item != null) {
			return new ItemStack(item, 1, this.burningMetadata);
		}

		return this.getItemStack();
	}

	/**
	 * Gets the itemstack that entities with the provided effect should drop.
	 * @return The effect itemstack or the base itemstack if not set.
	 */
	@NotNull
	public ItemStack getEffectItemStack(int effectId) {
		if(!this.effectItemIds.containsKey(effectId) || !this.effectItemMetadata.containsKey(effectId)) {
			return ItemStack.EMPTY;
		}
		Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(this.effectItemIds.get(effectId)));
		if(item != null) {
			return new ItemStack(item, 1, this.effectItemMetadata.get(effectId));
		}

		return ItemStack.EMPTY;
	}
	
	public ItemStack getEntityDropItemStack(EntityLivingBase entity, int quantity) {
		ItemStack itemStack = this.getItemStack();

		if(entity != null) {
			if(entity.isBurning()) {
				itemStack = this.getBurningItemStack();
			}

			for(Object potionEffect : entity.getActivePotionEffects()) {
				if(potionEffect instanceof PotionEffect) {
					int effectId = Potion.getIdFromPotion(((PotionEffect) potionEffect).getPotion());
					ItemStack effectStack = this.getEffectItemStack(effectId);
					if(!effectStack.isEmpty())
						itemStack = effectStack;
				}
			}
		}
		
		if(itemStack != null) {
			itemStack.setCount(quantity);
		}

		return itemStack;
	}


	/**
	 * Reads this Item Drop from NBT.
	 * @param nbtTagCompound The NBT to load values from.
	 */
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.hasKey("ItemId"))
			this.itemId = nbtTagCompound.getString("ItemId");
		if(nbtTagCompound.hasKey("Metadata"))
			this.metadata = nbtTagCompound.getInteger("Metadata");
		this.minAmount = nbtTagCompound.getInteger("MinAmount");
		this.maxAmount = nbtTagCompound.getInteger("MaxAmount");
		this.chance = nbtTagCompound.getFloat("Chance");
	}


	/**
	 * Writes this Item Drop to NBT.
	 * @param nbtTagCompound The NBT to write to.
	 * @return True on success or false on fail (this happens if this drop is missing an item id, etc).
	 */
	public boolean writeToNBT(NBTTagCompound nbtTagCompound) {
		if(this.itemId == null) {
			return false;
		}

		nbtTagCompound.setString("ItemId", this.itemId);
		nbtTagCompound.setInteger("Metadata", this.metadata);
		nbtTagCompound.setInteger("MinAmount", this.minAmount);
		nbtTagCompound.setInteger("MaxAmount", this.maxAmount);
		nbtTagCompound.setFloat("Chance", this.chance);

		return true;
	}


	/**
	 * Returns this Item Drop as a string value for using in configs.
	 * @return The Item Drop config string.
	 */
	public String toConfigString() {
		return this.itemId + "," + this.metadata + "," + this.minAmount + "," + this.maxAmount + "," + this.chance;
	}
}
