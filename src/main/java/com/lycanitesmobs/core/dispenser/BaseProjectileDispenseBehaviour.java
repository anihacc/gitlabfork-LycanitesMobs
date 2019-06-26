package com.lycanitesmobs.core.dispenser;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.Random;

public class BaseProjectileDispenseBehaviour extends ProjectileDispenseBehavior {
	protected ProjectileInfo projectileInfo;

	protected String oldProjectileName;
	protected Class<? extends BaseProjectileEntity> oldProjectileClass;

	// ==================================================
	//                      Constructor
	// ==================================================
	public BaseProjectileDispenseBehaviour(ProjectileInfo projectileInfo) {
		super();
		this.projectileInfo = projectileInfo;
	}

	public BaseProjectileDispenseBehaviour(Class<? extends BaseProjectileEntity> oldProjectileClass, String oldProjectileName) {
		super();
		this.oldProjectileClass = oldProjectileClass;
		this.oldProjectileName = oldProjectileName;
	}
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack) {
        World world = blockSource.getWorld();
        IPosition position = DispenserBlock.getDispensePosition(blockSource);
        Direction facing = blockSource.getBlockState().get(DispenserBlock.FACING);
        
        IProjectile iprojectile = this.getProjectileEntity(world, position, stack);
        if(iprojectile == null)
        	return stack;
        
        iprojectile.shoot((double)facing.getXOffset(), (double)facing.getYOffset(), (double)facing.getZOffset(), this.getProjectileVelocity(), this.getProjectileInaccuracy());
        world.addEntity((Entity)iprojectile);
        stack.split(1);
        
        return stack;
    }
    
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition pos, ItemStack stack) {
		if(this.projectileInfo != null) {
			return this.projectileInfo.createProjectile(world, pos.getX(), pos.getY(), pos.getZ());
		}
		if(this.oldProjectileClass != null) {
			return ProjectileManager.getInstance().createOldProjectile(this.oldProjectileClass, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return null;
	}

	@Override
	protected float getProjectileInaccuracy()
	{
		return 0F;
	}

	@Override
	protected float getProjectileVelocity()
	{
		if(this.projectileInfo != null) {
			return (float)this.projectileInfo.velocity;
		}
		return 1.1F;
	}
    
    
	// ==================================================
	//                        Sound
	// ==================================================
	@Override
    protected void playDispenseSound(IBlockSource blockSource) {
        SoundEvent soundEvent = this.getDispenseSound();
        if(soundEvent == null || blockSource == null)
            return;
        blockSource.getWorld().playSound(null, blockSource.getBlockPos(), soundEvent, SoundCategory.AMBIENT, 1.0F, 1.0F / (new Random().nextFloat() * 0.4F + 0.8F));
    }

    protected SoundEvent getDispenseSound() {
		if(this.projectileInfo != null) {
			return this.projectileInfo.getLaunchSound();
		}
		return ObjectManager.getSound(this.oldProjectileName);
    }
}