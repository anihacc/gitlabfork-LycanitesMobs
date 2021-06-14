package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.core.entity.PortalEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ItemStaffBlood extends ItemStaffSummoning {
	public PortalEntity portalEntity;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemStaffBlood(String itemName, String textureName) {
        super(itemName, textureName);
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Durability ==========
    @Override
    public int getDurability() {
    	return 250;
    }
    
    // ========== Rapid Time ==========
    @Override
    public int getRapidTime(ItemStack itemStack) {
        return 20;
    }
    
    // ========== Summon Cost ==========
    @Override
    public int getSummonCostBoost() {
    	return 0;
    }
    
    @Override
    public float getSummonCostMod() {
    	return 0.5F;
    }
    
    // ========== Summon Duration ==========
    @Override
    public int getSummonDuration() {
    	return 60 * 20;
    }
    
    // ========== Additional Costs ==========
    @Override
    public boolean getAdditionalCosts(EntityPlayer player) {
    	if(player.getHealth() <= 7)
    		return false;
    	player.attackEntityFrom(DamageSource.MAGIC, 6);
    	return true;
    }
    
	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == Items.GOLD_INGOT) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
