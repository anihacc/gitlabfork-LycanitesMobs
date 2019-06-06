package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.projectile.EntityDemonicBlast;
import com.lycanitesmobs.core.entity.EntityProjectileBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDemonicLightningCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDemonicLightningCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "demoniclightningcharge";
        this.setup();
    }


    // ==================================================
    //                    Item Use
    // ==================================================
    @Override
    public EntityProjectileBase createProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityDemonicBlast(world, entityPlayer);
    }
}
