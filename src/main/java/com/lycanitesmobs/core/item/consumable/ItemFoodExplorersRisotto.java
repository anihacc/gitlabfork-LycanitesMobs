package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.item.Item;

public class ItemFoodExplorersRisotto extends ItemCustomFood {
	
    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemFoodExplorersRisotto(Item.Properties properties, String setItemName, ModInfo group, String setTexturePath, int feed, float saturation, FOOD_CLASS foodClass) {
		super(properties, setItemName, group, feed, saturation, foodClass);
	}
	
	
    // ==================================================
  	//                     Effects
  	// ==================================================
    /*@Override
    protected void onFoodEaten(ItemStack itemStack, World world, PlayerEntity player) {
        super.onFoodEaten(itemStack, world, player);
        player.addPotionEffect(new PotionEffect(MobEffects.SPEED, this.getEffectDuration(), 3));
        player.addPotionEffect(new PotionEffect(MobEffects.HASTE, this.getEffectDuration(), 3));
        player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, this.getEffectDuration(), 1));
        player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, this.getEffectDuration(), 1));
    }*/
}
