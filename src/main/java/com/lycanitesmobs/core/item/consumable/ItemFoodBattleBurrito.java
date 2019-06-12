package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFoodBattleBurrito extends ItemCustomFood {
	
    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemFoodBattleBurrito(String setItemName, ModInfo group, String setTexturePath, int feed, float saturation) {
		super(setItemName, group, setTexturePath, feed, saturation, FOOD_CLASS.FEAST);
	}
	public ItemFoodBattleBurrito(String setItemName, ModInfo group, int feed, float saturation) {
		super(setItemName, group, feed, saturation, FOOD_CLASS.FEAST);
	}
	
	
    // ==================================================
  	//                     Effects
  	// ==================================================
    @Override
    protected void onFoodEaten(ItemStack itemStack, World world, PlayerEntity player) {
        super.onFoodEaten(itemStack, world, player);
        player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, this.getEffectDuration(), 3));
		player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, this.getEffectDuration(), 3));
		if(ObjectManager.getEffect("rejuvenation") != null)
			player.addPotionEffect(new PotionEffect(ObjectManager.getEffect("rejuvenation"), this.getEffectDuration(), 1));
        player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, this.getEffectDuration(), 3));
        player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, this.getEffectDuration(), 1));
    }
}
