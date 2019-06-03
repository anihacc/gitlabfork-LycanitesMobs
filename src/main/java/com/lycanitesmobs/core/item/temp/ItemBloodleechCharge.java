package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.shadowmobs.ShadowMobs;
import com.lycanitesmobs.core.entity.projectile.EntityBloodleech;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBloodleechCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBloodleechCharge() {
        super();
        this.group = ShadowMobs.instance.group;
        this.itemName = "bloodleechcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityBloodleech(world, entityPlayer);
    }
}
