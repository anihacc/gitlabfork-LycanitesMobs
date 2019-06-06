package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;

import com.lycanitesmobs.core.entity.projectile.EntityAquaPulse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAquaPulseCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemAquaPulseCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "aquapulsecharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase createProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityAquaPulse(world, entityPlayer);
    }
}
