package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.mountainmobs.MountainMobs;
import com.lycanitesmobs.core.entity.projectile.EntityBoulderBlast;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBoulderBlastCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBoulderBlastCharge() {
        super();
        this.group = MountainMobs.instance.group;
        this.itemName = "boulderblastcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityBoulderBlast(world, entityPlayer);
    }
}
