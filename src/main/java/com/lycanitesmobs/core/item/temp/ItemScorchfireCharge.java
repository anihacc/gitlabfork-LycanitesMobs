package com.lycanitesmobs.core.item.temp;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.projectile.EntityScorchfireball;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScorchfireCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScorchfireCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "scorchfirecharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase createProjectile(ItemStack itemStack, World world, PlayerEntity entityPlayer) {
        return new EntityScorchfireball(world, entityPlayer);
    }
}
