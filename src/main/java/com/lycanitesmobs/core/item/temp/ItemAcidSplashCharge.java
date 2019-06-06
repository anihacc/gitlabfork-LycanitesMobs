package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;

import com.lycanitesmobs.core.entity.projectile.EntityAcidSplash;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAcidSplashCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemAcidSplashCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "acidsplashcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase createProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityAcidSplash(world, entityPlayer);
    }
}
