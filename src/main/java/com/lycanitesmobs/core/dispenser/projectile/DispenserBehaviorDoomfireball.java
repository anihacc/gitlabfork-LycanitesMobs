package com.lycanitesmobs.core.dispenser.projectile;

import com.lycanitesmobs.core.entity.projectile.EntityDoomfireball;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class DispenserBehaviorDoomfireball extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack itemStack) {
        return new EntityDoomfireball(world, position.getX(), position.getY(), position.getZ());
    }


    // ==================================================
    //                        Sound
    // ==================================================
    @Override
    protected SoundEvent getDispenseSound() {
        return AssetManager.getSound("doomfireball");
    }
}