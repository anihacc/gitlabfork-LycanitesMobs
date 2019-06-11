package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;

import com.lycanitesmobs.core.entity.projectile.EntityFaeBolt;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFaeboltCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemFaeboltCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "faeboltcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase createProjectile(ItemStack itemStack, World world, PlayerEntity entityPlayer) {
        return new EntityFaeBolt(world, entityPlayer);
    }
}
