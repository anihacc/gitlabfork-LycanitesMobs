package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import java.util.*;

public class ItemDrop {
	public static List<ItemDrop> allDrops = new ArrayList<>();

	// ========== Item ==========
	public String itemId;
	protected int metadata;

	protected String burningItemId;
	protected int burningMetadata;

	protected Map<Integer, String> effectItemIds = new HashMap<>();
	protected Map<Integer, Integer> effectItemMetadata = new HashMap<>();
	
	public int minAmount = 1;
	public int maxAmount = 1;
	public boolean bonusAmount = true;
	public boolean amountMultiplier = true;

	public float chance = 0;

    /** The ID of the subspecies that this drop is restricted to. An ID below 0 will have this drop ignore the subspecies. **/
    public int subspeciesIndex = -1;

	/** The ID of the variant that this drop is restricted to. An ID below 0 will have this drop ignore the variant. **/
	public int variantIndex = -1;

	/** If true, items will only drop for adults and not babies. True by default. **/
	public boolean adultOnly = true;


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
			LycanitesMobs.logWarning("", "[JSON] Unable to load item drop from json as it has no item id!");
		}
		allDrops.add(itemDrop);

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
		if(json.has("bonusAmount"))
			this.bonusAmount = json.get("bonusAmount").getAsBoolean();
		if(json.has("amountMultiplier"))
			this.amountMultiplier = json.get("amountMultiplier").getAsBoolean();
		if(json.has("chance"))
			this.chance = json.get("chance").getAsFloat();
		if(json.has("subspecies"))
			this.subspeciesIndex = json.get("subspecies").getAsInt();
		if(json.has("variant"))
			this.variantIndex = json.get("variant").getAsInt();
		if(json.has("adultOnly"))
			this.adultOnly = json.get("adultOnly").getAsBoolean();

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

    public ItemDrop setSubspecies(int subspeciesIndex) {
        this.subspeciesIndex = subspeciesIndex;
        return this;
    }

	public ItemDrop setVariant(int variantIndex) {
		this.variantIndex = variantIndex;
		return this;
	}


	/**
	 * Returns a quantity to drop.
	 * @param random The instance of random to use.
	 * @param bonus A bonus multiplier.
	 * @param multiplier The value to multiply the quantity by.
	 * @return The randomised amount to drop.
	 */
	public int getQuantity(Random random, int bonus, int multiplier) {
		// Will It Drop?
		float roll = random.nextFloat();
		roll = Math.max(roll, 0);
		if(roll > this.chance)
			return 0;
		
		// How Many?
		if(!this.amountMultiplier) {
			multiplier = 1;
		}
		int min = this.minAmount;
		int max = this.maxAmount + (this.bonusAmount ? bonus : 0);
		if(max <= min) {
			return min * multiplier;
		}
		roll = roll / this.chance;
		float dropRange = (max - min) * roll;
		int dropAmount = min + Math.round(dropRange);
		return Math.min(dropAmount * multiplier, this.getItemStack().getMaxStackSize());
	}

	/**
	 * Gets the base itemstack for this item drop.
	 * @return The base itemstack to drop.
	 */
	@Nonnull
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
	@Nonnull
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
	@Nonnull
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
		if(nbtTagCompound.hasKey("BonusAmount"))
			this.bonusAmount = nbtTagCompound.getBoolean("BonusAmount");
		this.chance = nbtTagCompound.getFloat("Chance");
		if(nbtTagCompound.hasKey("AmountMultiplier"))
			this.amountMultiplier = nbtTagCompound.getBoolean("AmountMultiplier");
		if(nbtTagCompound.hasKey("Subspecies"))
			this.subspeciesIndex = nbtTagCompound.getInteger("Subspecies");
		if(nbtTagCompound.hasKey("Variant"))
			this.variantIndex = nbtTagCompound.getInteger("Variant");
		if(nbtTagCompound.hasKey("AdultOnly"))
			this.adultOnly = nbtTagCompound.getBoolean("AdultOnly");
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
		nbtTagCompound.setBoolean("BonusAmount", this.bonusAmount);
		nbtTagCompound.setFloat("Chance", this.chance);
		nbtTagCompound.setBoolean("AmountMultiplier", this.amountMultiplier);
		nbtTagCompound.setInteger("Subspecies", this.subspeciesIndex);
		nbtTagCompound.setInteger("Variant", this.variantIndex);
		nbtTagCompound.setBoolean("AdultOnly", this.adultOnly);

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
