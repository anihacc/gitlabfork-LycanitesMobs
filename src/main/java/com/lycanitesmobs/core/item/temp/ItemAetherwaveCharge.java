package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;

import com.lycanitesmobs.core.entity.projectile.EntityAetherwave;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAetherwaveCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemAetherwaveCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "aetherwavecharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityAetherwave(world, entityPlayer);
    }
}
