package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemScepterWaterJet extends ItemScepter {
	private BaseProjectileEntity projectileTarget;

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterWaterJet() {
        super();
    	this.modInfo = LycanitesMobs.modInfo;
    	this.itemName = "waterjetscepter";
        this.setup();
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Start ==========
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        this.projectileTarget = null;
        return super.onItemRightClick(world, player, hand);
    }
    
    @Override
    public int getDurability() {
    	return 250;
    }

    @Override
    public int getRapidTime(ItemStack par1ItemStack) {
        return 10;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
    	if(!world.isRemote) {
    		if(this.projectileTarget != null && this.projectileTarget.isEntityAlive()) {
				projectileTarget.projectileLife = 20;
    		}
    		else {
				ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("waterjet");
				if(projectileInfo == null) {
					return true;
				}
				this.projectileTarget = projectileInfo.createProjectile(world, entity);
    			world.spawnEntity(this.projectileTarget);
                this.playSound(itemStack, world, entity, 1, this.projectileTarget);
    		}
    	}
    	return true;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == ObjectManager.getItem("waterjetcharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
