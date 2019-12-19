package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;

import com.lycanitesmobs.core.entity.projectile.EntityBoulderBlast;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterBoulderBlast extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterBoulderBlast() {
        super();
    	this.modInfo = LycanitesMobs.modInfo;
    	this.itemName = "boulderblastscepter";
        this.setup();
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public int getDurability() {
    	return 250;
    }

    // ========== Charge Time ==========
    public int getChargeTime(ItemStack itemStack) {
        return 15;
    }


	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean chargedAttack(ItemStack itemStack, World world, EntityLivingBase entity, float power) {
    	if(!world.isRemote) {
    		BaseProjectileEntity projectile = new EntityBoulderBlast(world, entity);
    		projectile.setDamage((int)(projectile.getDamage(null) * power * 2));
        	world.spawnEntity(projectile);
            this.playSound(itemStack, world, entity, 1, projectile);
        }
    	return true;
    }


	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == ObjectManager.getItem("boulderblastcharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
