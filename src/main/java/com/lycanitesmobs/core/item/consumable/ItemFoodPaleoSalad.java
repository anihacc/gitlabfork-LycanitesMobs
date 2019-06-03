package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFoodPaleoSalad extends ItemCustomFood {
	
    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemFoodPaleoSalad(String setItemName, ModInfo group, String setTexturePath, int feed, float saturation, FOOD_CLASS foodClass) {
		super(setItemName, group, setTexturePath, feed, saturation, foodClass);
	}
	public ItemFoodPaleoSalad(String setItemName, ModInfo group, int feed, float saturation, FOOD_CLASS foodClass) {
		super(setItemName, group, feed, saturation, foodClass);
	}
	
	
    // ==================================================
  	//                     Effects
  	// ==================================================
    protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, this.getEffectDuration(), 2));
        player.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, 1, 3));
    }
}
