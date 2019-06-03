package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;

import com.lycanitesmobs.core.entity.projectile.EntityFrostweb;
import com.lycanitesmobs.core.item.temp.ItemScepter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterFrostweb extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterFrostweb() {
        super();
    	this.group = LycanitesMobs.modInfo;
    	this.itemName = "frostwebscepter";
        this.setup();
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public int getDurability() {
    	return 250;
    }

    @Override
    public int getRapidTime(ItemStack itemStack) {
        return 5;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
    	if(!world.isRemote) {
        	EntityFrostweb projectile = new EntityFrostweb(world, entity);
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
        if(repairStack.getItem() == ObjectManager.getItem("FrostwebCharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
