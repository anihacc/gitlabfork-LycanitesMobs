package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.item.ItemCharge;

import com.lycanitesmobs.core.entity.projectile.EntityLifeDrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLifeDrainCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemLifeDrainCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "lifedraincharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public BaseProjectileEntity createProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityLifeDrain(world, entityPlayer, 20, 10);
    }
}
