package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.ItemConfig;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.ItemBase;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class ItemCustomFood extends ItemBase {

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
		this.setRegistryName(this.group.filename, this.itemName);
		properties.maxStackSize(64);
		properties.group(LycanitesMobs.itemsTab);

		// Food: TODO Better Food Management in ItemManager
		Food.Builder foodBuilder = new Food.Builder();
		foodBuilder.func_221456_a(feed);
		foodBuilder.func_221454_a(saturation);
		foodBuilder.func_221451_a(); // Set Always Edible
		foodBuilder.func_221452_a(new EffectInstance(Effects.field_76432_h, 1 * 20, 0, false, false), 1.0F); // Additive Effects, float is chance.
		properties.func_221540_a(foodBuilder.func_221453_d()); // properties.food(foodBuilder.createFood());
	}

	public ItemCustomFood(Item.Properties properties, String setItemName, ModInfo group, int feed, float saturation, FOOD_CLASS foodClass) {
		this(properties, setItemName, group, setItemName, feed, saturation, foodClass);
	}


    // ==================================================
    //                     Effects
    // ==================================================
    public int getEffectDuration() {
        if(this.foodClass == FOOD_CLASS.RAW)
            return ItemConfig.durationRaw * 20;
        else if(this.foodClass == FOOD_CLASS.COOKED)
            return ItemConfig.durationCooked * 20;
        else if(this.foodClass == FOOD_CLASS.MEAL)
            return ItemConfig.durationMeal * 20;
        else if(this.foodClass == FOOD_CLASS.FEAST)
            return ItemConfig.durationFeast * 20;
        return 1;
    }

	public int getInstantHealing() {
		if(this.foodClass == FOOD_CLASS.COOKED)
			return ItemConfig.healingCooked;
		else if(this.foodClass == FOOD_CLASS.MEAL)
			return ItemConfig.healingMeal;
		else if(this.foodClass == FOOD_CLASS.FEAST)
			return ItemConfig.healingFeast;
		return 0;
	}
}
