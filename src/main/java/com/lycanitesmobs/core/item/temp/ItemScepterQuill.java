package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;

import com.lycanitesmobs.core.entity.projectile.EntityQuill;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterQuill extends ItemScepter {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterQuill() {
        super();
    	this.modInfo = LycanitesMobs.modInfo;
    	this.itemName = "quillscepter";
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
    public boolean rapidAttack(ItemStack itemStack, World world, LivingEntity entity) {
        if(!world.isRemote) {
            EntityQuill projectile = new EntityQuill(world, entity);
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
        if(repairStack.getItem() == ObjectManager.getItem("quill")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
