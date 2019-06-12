package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;

import com.lycanitesmobs.core.entity.projectile.EntityAquaPulse;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterAquaPulse extends ItemScepter {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterAquaPulse() {
        super();
    	this.modInfo = LycanitesMobs.modInfo;
    	this.itemName = "aquapulsescepter";
        this.setup();
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public int getDurability() {
    	return 80;
    }
    
    // ========== Charge Time ==========
    public int getChargeTime(ItemStack itemStack) {
        return 15;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean chargedAttack(ItemStack itemStack, World world, LivingEntity entity, float power) {
    	if(!world.isRemote) {
    		EntityProjectileBase projectile = new EntityAquaPulse(world, entity);
    		projectile.setDamage((int)(projectile.getDamage(null) * power * 2));
        	world.spawnEntity(projectile);
            this.playSound(itemStack, world, entity, power, projectile);
        }
    	return true;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == ObjectManager.getItem("AquaPulseCharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
