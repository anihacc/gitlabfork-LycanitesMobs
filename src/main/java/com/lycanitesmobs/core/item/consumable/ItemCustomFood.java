package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.BaseItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class ItemCustomFood extends BaseItem {

    /** The various classes of foods, used mainly for generic configurable effect durations. **/
    public static enum FOOD_CLASS {
        NONE(0), RAW(1), COOKED(2), MEAL(3), FEAST(4);
        public final int id;
        private FOOD_CLASS(int value) { this.id = value; }
        public int getValue() { return id; }
    }
	
	public String itemName = "customfood";
	public ModInfo group = LycanitesMobs.modInfo;
	public String texturePath = "customfood";
    public FOOD_CLASS foodClass = FOOD_CLASS.NONE;

    /** The ID of the potion effect that will occur upon eating this food. Set using setPotionEffect(). */
    protected EffectInstance effect;
    /** The ID of the chance effect that will occur upon eating this food. Set using setPotionEffect(). */
    protected float effectChance;


    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemCustomFood(Item.Properties properties, String setItemName, ModInfo group, String setTexturePath, int feed, float saturation, FOOD_CLASS foodClass) {
		super(properties);
		this.itemName = setItemName;
		this.group = group;
		this.texturePath = setTexturePath;
        this.foodClass = foodClass;
		this.setRegistryName(this.group.modid, this.itemName);

		// Food: TODO Better Food Management in ItemManager
		Food.Builder foodBuilder = new Food.Builder();
		foodBuilder.hunger(feed);
		foodBuilder.saturation(saturation);
		foodBuilder.setAlwaysEdible();
		foodBuilder.effect(new EffectInstance(Effects.INSTANT_HEALTH, 1 * 20, 0, false, false), 1.0F); // Additive Effects, float is chance.
		properties.food(foodBuilder.build());
	}

	public ItemCustomFood(Item.Properties properties, String setItemName, ModInfo group, int feed, float saturation, FOOD_CLASS foodClass) {
		this(properties, setItemName, group, setItemName, feed, saturation, foodClass);
	}


    // ==================================================
    //                     Effects
    // ==================================================
    public int getEffectDuration() {
        if(this.foodClass == FOOD_CLASS.RAW)
            return 10 * 20;
        else if(this.foodClass == FOOD_CLASS.COOKED)
            return 10 * 20;
        else if(this.foodClass == FOOD_CLASS.MEAL)
            return 60 * 20;
        else if(this.foodClass == FOOD_CLASS.FEAST)
            return 5 * 60 * 20;
        return 1;
    }

	public int getInstantHealing() {
		if(this.foodClass == FOOD_CLASS.COOKED)
			return 8;
		else if(this.foodClass == FOOD_CLASS.MEAL)
			return 12;
		else if(this.foodClass == FOOD_CLASS.FEAST)
			return 20;
		return 0;
	}
}
