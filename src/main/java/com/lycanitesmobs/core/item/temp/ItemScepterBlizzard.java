package com.lycanitesmobs.core.item.temp;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.temp.ItemScepter;
import com.lycanitesmobs.core.entity.projectile.EntityBlizzard;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterBlizzard extends ItemScepter {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterBlizzard() {
        super();
    	this.group = LycanitesMobs.modInfo;
    	this.itemName = "blizzardscepter";
        this.setup();
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public int getDurability() {
    	return 250;
    }

    @Override
    public int getRapidTime(ItemStack itemStack) {
        return 3;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
    	if(!world.isRemote) {
        	EntityBlizzard projectile = new EntityBlizzard(world, entity);
        	world.spawnEntity(projectile);
        	
        	projectile = new EntityBlizzard(world, entity);
        	projectile.setPosition(projectile.posX + 1.0D, projectile.posY, projectile.posZ);
        	world.spawnEntity(projectile);
        	
        	projectile = new EntityBlizzard(world, entity);
        	projectile.setPosition(projectile.posX - 1.0D, projectile.posY, projectile.posZ);
        	world.spawnEntity(projectile);
        	
        	projectile = new EntityBlizzard(world, entity);
        	projectile.setPosition(projectile.posX, projectile.posY, projectile.posZ + 1.0D);
        	world.spawnEntity(projectile);
        	
        	projectile = new EntityBlizzard(world, entity);
        	projectile.setPosition(projectile.posX, projectile.posY, projectile.posZ - 1.0D);
        	world.spawnEntity(projectile);
        	
        	projectile = new EntityBlizzard(world, entity);
        	projectile.setPosition(projectile.posX, projectile.posY + 1.0D, projectile.posZ);
        	world.spawnEntity(projectile);
        	
        	projectile = new EntityBlizzard(world, entity);
        	projectile.setPosition(projectile.posX, projectile.posY - 1.0D, projectile.posZ);
        	world.spawnEntity(projectile);

            this.playSound(itemStack, world, entity, 1, projectile);
        }
    	return true;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == ObjectManager.getItem("BlizzardCharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
