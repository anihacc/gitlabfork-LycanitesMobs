package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.EnumSet;

public class BegGoal extends Goal {
	// Targets:
    private TameableCreatureEntity host;
    private PlayerEntity player;
    
    // Properties:
    private float range = 8.0F * 8.0F;
    private int begTime;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public BegGoal(TameableCreatureEntity setHost) {
        this.host = setHost;
		this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
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
        this.player = this.host.getEntityWorld().getClosestPlayer(this.host.getPositionVec().getX(), this.host.getPositionVec().getY(), this.host.getPositionVec().getZ(), (double) this.range, entity -> true);
        return this.player != null && this.gotBegItem(this.player);
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
	@Override
    public boolean shouldContinueExecuting() {
        return this.player.isAlive() && (!(this.host.getDistance(this.player) > (double) (this.range * this.range)) && (this.begTime > 0 && this.gotBegItem(this.player)));
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
    public void tick() {
        this.host.getLookController().setLookPosition(this.player.getPositionVec().getX(), this.player.getPositionVec().getY() + (double)this.player.getEyeHeight(), this.player.getPositionVec().getZ(), 10.0F, (float)this.host.getVerticalFaceSpeed());
        --this.begTime;
    }
	
    
	// ==================================================
 	//                    Got Beg Item
 	// ==================================================
    private boolean gotBegItem(PlayerEntity player) {
        ItemStack itemstack = player.inventory.getCurrentItem();
        if(itemstack.isEmpty())
        	return false;
        
        if(!this.host.isTamed())
        	return this.host.isTamingItem(itemstack);
        
        return this.host.isBreedingItem(itemstack);
    }
}
