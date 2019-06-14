package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.item.Item;

public class ItemFoodBattleBurrito extends ItemCustomFood {
	
    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemFoodBattleBurrito(Item.Properties properties, String setItemName, ModInfo group, String setTexturePath, int feed, float saturation, FOOD_CLASS foodClass) {
		super(properties, setItemName, group, feed, saturation, foodClass);
	}
	
	
    // ==================================================
  	//                     Effects
  	// ==================================================
    /*@Override
    protected void onFoodEaten(ItemStack itemStack, World world, PlayerEntity player) {
        super.onFoodEaten(itemStack, world, player);
        player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, this.getEffectDuration(), 3));
		player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, this.getEffectDuration(), 3));
		if(ObjectManager.getEffect("rejuvenation") != null)
			player.addPotionEffect(new PotionEffect(ObjectManager.getEffect("rejuvenation"), this.getEffectDuration(), 1));
        player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, this.getEffectDuration(), 3));
        player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, this.getEffectDuration(), 1));
    }*/
}
