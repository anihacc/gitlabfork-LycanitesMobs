package com.lycanitesmobs.core.item.temp;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.projectile.EntityFrostweb;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFrostwebCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemFrostwebCharge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "frostwebcharge";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
    @Override
    public BaseProjectileEntity createProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityFrostweb(world, entityPlayer);
    }
}
