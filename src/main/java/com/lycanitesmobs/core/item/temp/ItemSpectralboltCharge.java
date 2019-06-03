package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.shadowmobs.ShadowMobs;
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
        this.group = ShadowMobs.instance.group;
        this.itemName = "spectralboltcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntitySpectralbolt(world, entityPlayer);
    }
}
