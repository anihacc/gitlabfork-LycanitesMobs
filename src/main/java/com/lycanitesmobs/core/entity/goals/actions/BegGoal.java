package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class BegGoal extends EntityAIBase {
	// Targets:
    private TameableCreatureEntity host;
    private EntityPlayer player;
    
    // Properties:
    private float range = 8.0F * 8.0F;
    private int begTime;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public BegGoal(TameableCreatureEntity setHost) {
        this.host = setHost;
		this.setMutexBits(3);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public BegGoal setRange(float setRange) {
    	this.range = setRange * setRange;
    	return this;
    }
	
    
	// ==================================================
 	//                   Should Execute
 	// ==================================================
	@Override
    public boolean shouldExecute() {
		this.player = this.host.getEntityWorld().getClosestPlayerToEntity(this.host, (double)this.range);
        return this.player != null && this.gotBegItem(this.player);
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
	@Override
    public boolean shouldContinueExecuting() {
        return this.player.isEntityAlive() && (!(this.host.getDistance(this.player) > (double) (this.range * this.range)) && (this.begTime > 0 && this.gotBegItem(this.player)));
    }
	
    
	// ==================================================
 	//                      Start
 	// ==================================================
	@Override
    public void startExecuting() {
        this.host.setSitting(true);
        this.begTime = 40 + this.host.getRNG().nextInt(40);
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
	@Override
    public void resetTask() {
        this.host.setSitting(false);
        this.player = null;
    }
	
    
	// ==================================================
 	//                      Update
 	// ==================================================
	@Override
    public void updateTask() {
        this.host.getLookHelper().setLookPosition(this.player.posX, this.player.posY + (double)this.player.getEyeHeight(), this.player.posZ, 10.0F, (float)this.host.getVerticalFaceSpeed());
        --this.begTime;
    }
	
    
	// ==================================================
 	//                    Got Beg Item
 	// ==================================================
    private boolean gotBegItem(EntityPlayer player) {
        ItemStack itemstack = player.inventory.getCurrentItem();
        if(itemstack.isEmpty())
        	return false;
        
        if(!this.host.isTamed())
        	return this.host.isTamingItem(itemstack);
        
        return this.host.isBreedingItem(itemstack);
    }
}
