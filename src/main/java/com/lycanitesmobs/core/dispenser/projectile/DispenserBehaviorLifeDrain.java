package com.lycanitesmobs.core.dispenser.projectile;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import com.lycanitesmobs.core.entity.projectile.EntityLifeDrain;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class DispenserBehaviorLifeDrain extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack) {
        World world = blockSource.getWorld();
        IPosition iposition = DispenserBlock.getDispensePosition(blockSource);
		Direction facing = blockSource.getBlockState().get(DispenserBlock.FACING);
        
        double targetX = iposition.getX();
		double targetY = iposition.getY();
		double targetZ = iposition.getZ();
		
		/*if(facing.equals(Direction.UP))
			targetY += 1;
		if(facing.equals(Direction.DOWN))
			targetY -= 1;
		if(facing.equals(Direction.NORTH))
			targetZ += 1;
		if(facing.equals(Direction.SOUTH))
			targetZ -= 1;
		if(facing.equals(Direction.EAST))
			targetX += 1;
		if(facing.equals(Direction.WEST))
			targetX -= 1;*/
		
		IProjectile projectile = new EntityLifeDrain(world, targetX, targetY, targetZ, 5 * 20, 10);
		EntityProjectileLaser laser = (EntityProjectileLaser)projectile;
		
		if(facing.equals(Direction.DOWN))
			targetY -= laser.laserRange;
		if(facing.equals(Direction.UP))
			targetY += laser.laserRange;
		if(facing.equals(Direction.NORTH))
			targetZ -= laser.laserRange;
		if(facing.equals(Direction.SOUTH))
			targetZ += laser.laserRange;
		if(facing.equals(Direction.EAST))
			targetX -= laser.laserRange;
		if(facing.equals(Direction.WEST))
			targetX += laser.laserRange;
		
		laser.setTarget(targetX, targetY, targetZ);

		world.func_217376_c(laser);
		itemStack.split(1);
        return itemStack;
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
	@Override
    protected SoundEvent getDispenseSound() {
        return AssetManager.getSound("lifedrain");
    }
}