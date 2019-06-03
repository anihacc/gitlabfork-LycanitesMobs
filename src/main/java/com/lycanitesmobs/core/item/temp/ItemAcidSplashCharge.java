package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
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
        this.group = ElementalMobs.instance.group;
        this.itemName = "acidsplashcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityAcidSplash(world, entityPlayer);
    }
}
