package com.lycanitesmobs.core.item.temp;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.projectile.EntityIcefireball;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemIcefireCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemIcefireCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "icefirecharge";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
    @Override
    public EntityProjectileBase createProjectile(ItemStack itemStack, World world, PlayerEntity entityPlayer) {
        return new EntityIcefireball(world, entityPlayer);
    }
}
