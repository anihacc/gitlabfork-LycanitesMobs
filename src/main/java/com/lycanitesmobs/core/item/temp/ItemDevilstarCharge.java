package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;

import com.lycanitesmobs.core.entity.projectile.EntityDevilstar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDevilstarCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDevilstarCharge() {
        super();
        this.group = LycanitesMobs.modInfo;
        this.itemName = "devilstarcharge";
        this.setup();
    }


    // ==================================================
    //                    Item Use
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityDevilstar(world, entityPlayer);
    }
}
