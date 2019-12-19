package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.item.ItemCharge;

import com.lycanitesmobs.core.entity.projectile.EntityQuill;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemQuill extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemQuill() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "quill";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public BaseProjectileEntity createProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityQuill(world, entityPlayer);
    }
}
