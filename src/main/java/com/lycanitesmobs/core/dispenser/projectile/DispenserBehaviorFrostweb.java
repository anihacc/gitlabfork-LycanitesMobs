package com.lycanitesmobs.core.dispenser.projectile;

import com.lycanitesmobs.core.entity.projectile.EntityFrostweb;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class DispenserBehaviorFrostweb extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack itemStack) {
        return new EntityFrostweb(world, position.getX(), position.getY(), position.getZ());
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
	@Override
    protected SoundEvent getDispenseSound() {
        return AssetManager.getSound("frostweb");
    }
}