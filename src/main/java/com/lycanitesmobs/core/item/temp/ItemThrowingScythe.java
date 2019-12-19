package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCharge;

import com.lycanitesmobs.core.entity.projectile.EntityThrowingScythe;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemThrowingScythe extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemThrowingScythe() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "throwingscythe";
        this.setup();
    }
    
    

    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public BaseProjectileEntity createProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityThrowingScythe(world, entityPlayer);
    }
}
