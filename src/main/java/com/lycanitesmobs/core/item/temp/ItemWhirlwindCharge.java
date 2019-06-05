package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;

import com.lycanitesmobs.core.entity.projectile.EntityWhirlwind;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemWhirlwindCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemWhirlwindCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "whirlwindcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityWhirlwind(world, entityPlayer);
    }
}
