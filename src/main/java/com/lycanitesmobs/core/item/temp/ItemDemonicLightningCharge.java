package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.projectile.EntityDemonicBlast;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDemonicLightningCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDemonicLightningCharge() {
        super();
        this.group = DemonMobs.instance.group;
        this.itemName = "demoniclightningcharge";
        this.setup();
    }


    // ==================================================
    //                    Item Use
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityDemonicBlast(world, entityPlayer);
    }
}
