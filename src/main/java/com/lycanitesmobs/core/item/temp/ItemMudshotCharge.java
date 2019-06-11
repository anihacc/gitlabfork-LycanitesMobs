package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCharge;

import com.lycanitesmobs.core.entity.projectile.EntityMudshot;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMudshotCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemMudshotCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "mudshotcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase createProjectile(ItemStack itemStack, World world, PlayerEntity entityPlayer) {
        return new EntityMudshot(world, entityPlayer);
    }
}
