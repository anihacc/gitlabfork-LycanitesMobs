package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.arcticmobs.ArcticMobs;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.projectile.EntityIcefireball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemIcefireCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemIcefireCharge() {
        super();
        this.group = ArcticMobs.instance.group;
        this.itemName = "icefirecharge";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityIcefireball(world, entityPlayer);
    }
}
