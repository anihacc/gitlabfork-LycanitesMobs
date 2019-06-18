package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.navigate.CreaturePathNavigator;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.consumable.ItemTreat;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class TemptGoal extends Goal {
    // Targets:
    private EntityCreatureBase host;
    private PlayerEntity player;
    
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
    public TemptGoal(EntityCreatureBase setHost) {
        this.host = setHost;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public TemptGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public TemptGoal setItemStack(ItemStack item) {
    	this.temptItemStack = item;
    	return this;
    }
    public TemptGoal setIgnoreMeta(boolean ignore) {
    	this.ignoreTemptMeta = ignore;
    	return this;
    }
    public TemptGoal setItemList(String list) {
    	this.temptList = list;
    	return this;
    }
    public TemptGoal setRetemptTime(int time) {
    	this.retemptTimeMax = time;
    	return this;
    }
    public TemptGoal setTemptDistanceMin(double dist) {
    	this.temptDistanceMin = dist;
    	return this;
    }
    public TemptGoal setTemptDistanceMax(double dist) {
    	this.temptDistanceMax = dist;
    	return this;
    }
    public TemptGoal setScaredByMovement(boolean scared) {
    	this.scaredByPlayerMovement = scared;
    	return this;
    }
    public TemptGoal setStopAttack(boolean setStopAttack) {
    	this.stopAttack = setStopAttack;
    	return this;
    }
    public TemptGoal setIncludeTreats(boolean includeTreats) {
        this.includeTreats = includeTreats;
        return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
	@Override
    public boolean shouldExecute() {
        if(this.retemptTime > 0) {
            --this.retemptTime;
            return false;
        }
        
        if(!this.host.canBeTempted())
        	return false;
        
        if(this.host instanceof EntityCreatureTameable && this.host.isTamed())
        	return false;
		
        this.player = this.host.getEntityWorld().getClosestPlayer(this.host.getPositionVec().getX(), this.host.getPositionVec().getY(), this.host.getPositionVec().getZ(), this.temptDistanceMax, entity -> true);
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
            if(!ObjectLists.inItemList(this.temptList, itemStack))
                return false;
        }

        // Single Tempt Item:
        else if(this.temptItemStack != null) {
            if(itemStack.getItem() != this.temptItemStack.getItem())
                return false;
            if(!this.ignoreTemptMeta && itemStack.getItem() == this.temptItemStack.getItem())
                return false;
            return true;
        }

        return false;
    }
    
    
    // ==================================================
  	//                 Continue Executing
  	// ==================================================
	@Override
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
	@Override
    public void startExecuting() {
        this.targetX = this.player.posX;
        this.targetY = this.player.posY;
        this.targetZ = this.player.posZ;
        this.isRunning = true;
        if (this.host.getNavigator() instanceof GroundPathNavigator || this.host.getNavigator() instanceof CreaturePathNavigator) {
            PathNavigator navigateGround = this.host.getNavigator();
            this.canSwim = !navigateGround.getCanSwim();
            navigateGround.setCanSwim(true);
        }
        if(this.stopAttack)
        	this.host.setAttackTarget(null);
    }
    
    
    // ==================================================
  	//                      Reset
  	// ==================================================
	@Override
    public void resetTask() {
        this.player = null;
        this.host.getNavigator().clearPath();
        this.retemptTime = this.retemptTimeMax;
        if(this.host instanceof EntityCreatureAgeable) {
            EntityCreatureAgeable ageable = (EntityCreatureAgeable)this.host;
            if(!ageable.isChild() && !ageable.canBreed())
                this.retemptTime *= 10;
        }
        this.isRunning = false;
        if (this.host.getNavigator() instanceof GroundPathNavigator || this.host.getNavigator() instanceof CreaturePathNavigator) {
            PathNavigator navigateGround = this.host.getNavigator();
            navigateGround.setCanSwim(this.canSwim);
        }
    }
    
    
    // ==================================================
  	//                      Update
  	// ==================================================
	@Override
    public void tick() {
        if(this.stopAttack)
        	this.host.setAttackTarget(null);
        this.host.getLookController().setLookPositionWithEntity(this.player, 30.0F, (float)this.host.getVerticalFaceSpeed());
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
