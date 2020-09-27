package com.lycanitesmobs.core.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class CustomItemEntity extends ItemEntity {
	protected boolean canBurn = true;

	// ==================================================
   	//                     Constructor
   	// ==================================================
	public CustomItemEntity(World world) {
		super(EntityType.ITEM, world);
	}
	
	public CustomItemEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public CustomItemEntity(World world, double x, double y, double z, ItemStack itemStack) {
        super(world, x, y, z, itemStack);
    }
    

	// ==================================================
   	//                   Taking Damage
   	// ==================================================
    public boolean attackEntityFrom(DamageSource damageSource, float damageAmount) {
    	if(this.isInvulnerableTo(DamageSource.ON_FIRE) || !this.canBurn) {
    		if(damageSource.isFireDamage() || "inFire".equalsIgnoreCase(damageSource.damageType)) {
    			return false;
    		}
    	}
        return super.attackEntityFrom(damageSource, damageAmount);
    }

    public void setCanBurn(boolean canBurn) {
		this.canBurn = canBurn;
	}
    

	// ==================================================
   	//                    Immunities
   	// ==================================================
	// TODO Fire Immunity handled by EntityType
    
    
    // ==================================================
   	//                  Network Flags
   	// ==================================================
    protected void setFlag(int flagID, boolean value) {
    	if(flagID == 0 && this.isInvulnerableTo(DamageSource.ON_FIRE))
    		value = false;
        super.setFlag(flagID, value);
    }
}
