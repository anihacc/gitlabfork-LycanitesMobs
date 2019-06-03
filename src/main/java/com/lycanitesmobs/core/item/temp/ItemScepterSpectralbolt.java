package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.temp.ItemScepter;
import com.lycanitesmobs.shadowmobs.ShadowMobs;
import com.lycanitesmobs.core.entity.projectile.EntitySpectralbolt;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterSpectralbolt extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterSpectralbolt() {
        super();
    	this.group = ShadowMobs.instance.group;
    	this.itemName = "spectralboltscepter";
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
    		EntitySpectralbolt projectile = new EntitySpectralbolt(world, entity);
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
        if(repairStack.getItem() == ObjectManager.getItem("spectralboltcharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
