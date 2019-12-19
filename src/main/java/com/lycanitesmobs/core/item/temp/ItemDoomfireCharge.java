package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.projectile.EntityDoomfireball;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDoomfireCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDoomfireCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "doomfirecharge";
        this.setup();
    }


    // ==================================================
    //                    Item Use
    // ==================================================
    @Override
    public BaseProjectileEntity createProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityDoomfireball(world, entityPlayer);
    }
}
