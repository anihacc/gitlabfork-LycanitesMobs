package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;

import com.lycanitesmobs.core.entity.projectile.EntityChaosOrb;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemChaosOrbCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemChaosOrbCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "chaosorbcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase createProjectile(ItemStack itemStack, World world, PlayerEntity entityPlayer) {
        return new EntityChaosOrb(world, entityPlayer);
    }
}
