package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.plainsmobs.PlainsMobs;
import com.lycanitesmobs.core.entity.projectile.EntityQuill;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemQuill extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemQuill() {
        super();
        this.group = PlainsMobs.instance.group;
        this.itemName = "quill";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityQuill(world, entityPlayer);
    }
}
