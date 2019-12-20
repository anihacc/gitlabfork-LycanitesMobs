package com.lycanitesmobs.core.item.temp;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.projectile.EntityScorchfireball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScorchfireCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScorchfireCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "scorchfirecharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public BaseProjectileEntity createProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityScorchfireball(world, entityPlayer);
    }
}