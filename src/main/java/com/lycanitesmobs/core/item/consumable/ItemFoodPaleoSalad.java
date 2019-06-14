package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.item.Item;

public class ItemFoodPaleoSalad extends ItemCustomFood {
	
    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemFoodPaleoSalad(Item.Properties properties, String setItemName, ModInfo group, String setTexturePath, int feed, float saturation, FOOD_CLASS foodClass) {
		super(properties, setItemName, group, feed, saturation, foodClass);
	}
	
	
    // ==================================================
  	//                     Effects
  	// ==================================================
    /*protected void onFoodEaten(ItemStack itemStack, World world, PlayerEntity player) {
        player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, this.getEffectDuration(), 2));
        player.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, 1, 3));
    }*/
}
