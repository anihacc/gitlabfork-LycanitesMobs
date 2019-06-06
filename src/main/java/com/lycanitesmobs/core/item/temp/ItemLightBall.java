package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;

import com.lycanitesmobs.core.entity.projectile.EntityLightBall;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLightBall extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemLightBall() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "lightball";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase createProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityLightBall(world, entityPlayer);
    }
}
