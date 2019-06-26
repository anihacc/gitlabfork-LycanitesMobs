package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.item.BaseItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ItemSoulgazer extends BaseItem {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulgazer(Item.Properties properties) {
        super(properties);
		this.itemName = "soulgazer";
		this.setup();
    }
    
    
	// ==================================================
	//                       Use
	// ==================================================
	// ========== Entity Interaction ==========
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
    	ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
    	if(playerExt == null)
    		return false;

    	return playerExt.beastiary.discoverCreature(entity, 2, true);
    }

    @Override
	public ItemStack getContainerItem(ItemStack itemStack) {
    	return new ItemStack(this, 1);
	}
}
