package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import com.lycanitesmobs.core.entity.projectile.EntityChaosOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemChaosOrbCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemChaosOrbCharge() {
        super();
        this.group = ElementalMobs.instance.group;
        this.itemName = "chaosorbcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityChaosOrb(world, entityPlayer);
    }
}
