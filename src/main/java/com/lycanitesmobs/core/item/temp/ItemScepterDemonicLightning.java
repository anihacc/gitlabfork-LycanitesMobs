package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterDemonicLightning extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterDemonicLightning() {
        super();
    	this.modInfo = LycanitesMobs.modInfo;
    	this.itemName = "demoniclightningscepter";
        this.setup();
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public int getDurability() {
    	return 250;
    }

    // ========== Charge Time ==========
    public int getChargeTime(ItemStack itemStack) {
        return 30;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean chargedAttack(ItemStack itemStack, World world, EntityLivingBase entity, float power) {
    	if(!world.isRemote) {
			ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("demonicblast");
			if(projectileInfo == null) {
				return true;
			}
			BaseProjectileEntity projectile = projectileInfo.createProjectile(world, entity);
    		projectile.setDamage((int)(projectile.getDamage(null) * power * 2));
        	world.spawnEntity(projectile);
            this.playSound(itemStack, world, entity, power, projectile);
        }
    	return true;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == ObjectManager.getItem("demoniclightningcharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}