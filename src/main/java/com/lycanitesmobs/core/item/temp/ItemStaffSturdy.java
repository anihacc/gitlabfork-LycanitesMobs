package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.core.entity.PortalEntity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ItemStaffSturdy extends ItemStaffSummoning {
	public PortalEntity portalEntity;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemStaffSturdy(String itemName, String textureName) {
        super(itemName, textureName);
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Durability ==========
    @Override
    public int getDurability() {
    	return 1000;
    }
    
    // ========== Rapid Time ==========
    @Override
    public int getRapidTime(ItemStack itemStack) {
        return 20;
    }
    
    // ========== Summon Cost ==========
    public int getSummonCostBoost() {
    	return 2;
    }
    public float getSummonCostMod() {
    	return 1.0F;
    }
    
    // ========== Summon Duration ==========
    public int getSummonDuration() {
    	return 120 * 20;
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
