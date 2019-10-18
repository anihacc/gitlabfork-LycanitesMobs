package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.item.consumable.ItemTreat;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;

public class EntityAITempt extends EntityAIBase {
    // Targets:
    private BaseCreatureEntity host;
    private EntityPlayer player;
    
    // Properties:
    private double speed = 1.0D;
    private ItemStack temptItemStack = null;
    private boolean ignoreTemptMeta = false;
    private String temptList = null;
    private int retemptTime;
    private int retemptTimeMax = 10; // Lowered from 100 because it's just annoying!
    private double temptDistanceMin = 1.0D;
    private double temptDistanceMax = 10.0D;
    private boolean scaredByPlayerMovement = false;
    private boolean stopAttack = false;
    private boolean includeTreats = true;
    
    private double targetX;
    private double targetY;
    private double targetZ;
    private double targetPitch;
    private double targetYaw;
    private boolean canSwim;
    private boolean isRunning;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAITempt(BaseCreatureEntity setHost) {
        this.host = setHost;
        this.setMutexBits(3);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITempt setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAITempt setItemStack(ItemStack item) {
    	this.temptItemStack = item;
    	return this;
    }
    public EntityAITempt setIgnoreMeta(boolean ignore) {
    	this.ignoreTemptMeta = ignore;
    	return this;
    }
    public EntityAITempt setItemList(String list) {
    	this.temptList = list;
    	return this;
    }
    public EntityAITempt setRetemptTime(int time) {
    	this.retemptTimeMax = time;
    	return this;
    }
    public EntityAITempt setTemptDistanceMin(double dist) {
    	this.temptDistanceMin = dist;
    	return this;
    }
    public EntityAITempt setTemptDistanceMax(double dist) {
    	this.temptDistanceMax = dist;
    	return this;
    }
    public EntityAITempt setScaredByMovement(boolean scared) {
    	this.scaredByPlayerMovement = scared;
    	return this;
    }
    public EntityAITempt setStopAttack(boolean setStopAttack) {
    	this.stopAttack = setStopAttack;
    	return this;
    }
    public EntityAITempt setIncludeTreats(boolean includeTreats) {
        this.includeTreats = includeTreats;
        return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
    public boolean shouldExecute() {
        if(this.retemptTime > 0) {
            --this.retemptTime;
            return false;
        }
        
        if(!this.host.canBeTempted())
        	return false;
        
        if(this.host instanceof TameableCreatureEntity && this.host.isTamed())
        	return false;
		
        this.player = this.host.getEntityWorld().getClosestPlayerToEntity(this.host, this.temptDistanceMax);
        if(this.player == null)
            return false;

        if(!this.isTemptStack(this.player.getHeldItemMainhand()) && !this.isTemptStack(this.player.getHeldItemOffhand()))
            return false;
        
        this.host.setStealth(0.0F);
        return true;
    }

    public boolean isTemptStack(ItemStack itemStack) {
        if(itemStack.isEmpty()) {
            return false;
        }

        // Creature Type Treats:
        if(this.includeTreats && this.host.creatureInfo.creatureType != null && itemStack.getItem() instanceof ItemTreat) {
            ItemTreat itemTreat = (ItemTreat)itemStack.getItem();
            if(this.host.creatureInfo.creatureType == itemTreat.getCreatureType()) {
                return true;
            }
        }

        // Tempt List:
        if(this.temptList != null) {
            return ObjectLists.inItemList(this.temptList, itemStack);
        }

        // Single Tempt Item:
        else if(this.temptItemStack != null) {
            if(itemStack.getItem() != this.temptItemStack.getItem())
                return false;
            if(!this.ignoreTemptMeta && itemStack.getItem() == this.temptItemStack.getItem() && itemStack.getItemDamage() != this.temptItemStack.getItemDamage())
                return false;
            return true;
        }

        return false;
    }
    
    
    // ==================================================
  	//                 Continue Executing
  	// ==================================================
    public boolean shouldContinueExecuting() {
        if(this.scaredByPlayerMovement) {
            if(this.host.getDistance(this.player) < 36.0D) {
                if(this.player.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002D)
                    return false;
                if(Math.abs((double)this.player.rotationPitch - this.targetPitch) > 5.0D || Math.abs((double)this.player.rotationYaw - this.targetYaw) > 5.0D)
                    return false;
            }
            else {
                this.targetX = this.player.posX;
                this.targetY = this.player.posY;
                this.targetZ = this.player.posZ;
            }

            this.targetPitch = (double)this.player.rotationPitch;
            this.targetYaw = (double)this.player.rotationYaw;
        }

        return this.shouldExecute();
    }
    
    
    // ==================================================
  	//                      Start
  	// ==================================================
    public void startExecuting() {
        this.targetX = this.player.posX;
        this.targetY = this.player.posY;
        this.targetZ = this.player.posZ;
        this.isRunning = true;
        if (this.host.getNavigator() instanceof PathNavigateGround) {
            PathNavigateGround navigateGround = (PathNavigateGround) this.host.getNavigator();
            this.canSwim = !navigateGround.getCanSwim();
            navigateGround.setCanSwim(true);
        }
        if(this.stopAttack)
        	this.host.setAttackTarget(null);
    }
    
    
    // ==================================================
  	//                      Reset
  	// ==================================================
    public void resetTask() {
        this.player = null;
        this.host.getNavigator().clearPath();
        this.retemptTime = this.retemptTimeMax;
        if(this.host instanceof AgeableCreatureEntity) {
            AgeableCreatureEntity ageable = (AgeableCreatureEntity)this.host;
            if(!ageable.isChild() && !ageable.canBreed())
                this.retemptTime *= 10;
        }
        this.isRunning = false;
        if (this.host.getNavigator() instanceof PathNavigateGround) {
            PathNavigateGround navigateGround = (PathNavigateGround) this.host.getNavigator();
            navigateGround.setCanSwim(this.canSwim);
        }
    }
    
    
    // ==================================================
  	//                      Update
  	// ==================================================
    public void updateTask() {
        if(this.stopAttack)
        	this.host.setAttackTarget(null);
        this.host.getLookHelper().setLookPositionWithEntity(this.player, 30.0F, (float)this.host.getVerticalFaceSpeed());
        if(this.host.getDistance(this.player) < this.temptDistanceMin)
            this.host.clearMovement();
        else {
        	if(!this.host.useDirectNavigator())
        		this.host.getNavigator().tryMoveToEntityLiving(this.player, this.speed);
        	else
        		this.host.directNavigator.setTargetPosition(new BlockPos((int)this.player.posX, (int)this.player.posY, (int)this.player.posZ), speed);
        }
    }
    
    /**
     * @see #isRunning ???
     */
    public boolean isRunning() {
        return this.isRunning;
    }
}
