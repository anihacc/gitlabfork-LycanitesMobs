package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.infernomobs.InfernoMobs;
import com.lycanitesmobs.core.entity.projectile.EntityMagma;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMagmaCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemMagmaCharge() {
        super();
        this.group = InfernoMobs.instance.group;
        this.itemName = "magmacharge";
        this.setup();
    }
    

    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityMagma(world, entityPlayer);
    }
}
