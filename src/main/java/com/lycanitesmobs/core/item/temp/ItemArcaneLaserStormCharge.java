package com.lycanitesmobs.core.item.temp;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.projectile.EntityArcaneLaserStorm;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemArcaneLaserStormCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemArcaneLaserStormCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "arcanelaserstormcharge";
        this.setup();
    }
    
    
    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase createProjectile(ItemStack itemStack, World world, PlayerEntity entityPlayer) {
        return new EntityArcaneLaserStorm(world, entityPlayer);
    }
}
