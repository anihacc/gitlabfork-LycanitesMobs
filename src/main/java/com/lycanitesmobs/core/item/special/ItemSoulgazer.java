package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.item.ItemBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ItemSoulgazer extends ItemBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulgazer(Item.Properties properties) {
        super(properties);
		properties.maxStackSize(1);
		properties.containerItem(this); // Infinite use in the crafting grid.
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
}
