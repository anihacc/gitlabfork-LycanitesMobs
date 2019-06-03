package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import com.lycanitesmobs.core.entity.projectile.EntityCrystalShard;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemCrystalShard extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemCrystalShard() {
        super();
        this.group = ElementalMobs.instance.group;
        this.itemName = "crystalshard";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityCrystalShard(world, entityPlayer);
    }
}
