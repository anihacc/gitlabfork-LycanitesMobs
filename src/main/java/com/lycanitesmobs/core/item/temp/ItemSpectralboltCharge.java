package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;

import com.lycanitesmobs.core.entity.projectile.EntitySpectralbolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSpectralboltCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSpectralboltCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "spectralboltcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public BaseProjectileEntity createProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntitySpectralbolt(world, entityPlayer);
    }
}
