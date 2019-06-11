package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;

import com.lycanitesmobs.core.entity.projectile.EntityBlizzard;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlizzardCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBlizzardCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "blizzardcharge";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
    @Override
    public EntityProjectileBase createProjectile(ItemStack itemStack, World world, PlayerEntity entityPlayer) {
        return new EntityBlizzard(world, entityPlayer);
    }
}
