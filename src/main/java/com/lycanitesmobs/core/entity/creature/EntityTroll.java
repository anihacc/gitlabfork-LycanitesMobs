package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.entity.projectile.EntityBoulderBlast;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EntityTroll extends TameableCreatureEntity implements IMob {
	
	public boolean trollGreifing = true; // TODO Creature flags.
	
	// ========== Unique Entity Variables ==========
	public boolean stoneForm = false;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTroll(EntityType<? extends EntityTroll> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;

        //this.canGrow = false;
        //this.babySpawnChance = 0.01D;
        
        this.solidCollision = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        if(this.getNavigator() instanceof GroundPathNavigator) {
            GroundPathNavigator pathNavigateGround = (GroundPathNavigator)this.getNavigator();
            pathNavigateGround.setBreakDoors(true);
        }
        this.goalSelector.addGoal(0, new PaddleGoal(this));
        this.goalSelector.addGoal(1, new BreakDoorGoal(this));
        this.goalSelector.addGoal(5, new AttackRangedGoal(this).setSpeed(0.5D).setRange(14.0F).setMinChaseDistance(5.0F));
        this.goalSelector.addGoal(6, this.stayGoal);
        this.goalSelector.addGoal(7, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(8, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RevengeOwnerGoal(this));
        this.targetSelector.addGoal(1, new CopyOwnerAttackTargetGoal(this));
        this.targetSelector.addGoal(2, new RevengeGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(3, new FindAttackTargetGoal(this).addTargets(EntityType.PLAYER));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).addTargets(EntityType.VILLAGER));
        this.targetSelector.addGoal(6, new DefendOwnerGoal(this));
    }
    
    
    // ==================================================
    //                       Name
    // ==================================================
    public String getTextureName() {
    	if(this.stoneForm)
    		return super.getTextureName() + "_stone";
    	return super.getTextureName();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Daylight Stone Form:
        if(!this.stoneForm) {
        	if(this.isDaytime() && this.getEntityWorld().canBlockSeeSky(this.getPosition())) {
        		this.stoneForm = true;
        	}
        }
        else {
        	if(!this.isDaytime() || !this.getEntityWorld().canBlockSeeSky(this.getPosition())) {
                this.stoneForm = false;
            }
        }
        
        // Destroy Blocks:
 		if(!this.getEntityWorld().isRemote)
 	        if(this.getAttackTarget() != null && this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING) && this.trollGreifing) {
 		    	float distance = this.getAttackTarget().getDistance(this);
 		    		if(distance <= this.getSize(Pose.STANDING).width + 4.0F)
 		    			this.destroyArea((int)this.posX, (int)this.posY, (int)this.posZ, 10, true);
 	        }
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(this.stoneForm) // Slower in stone form.
    		return 0.125F;
    	return 1.0F;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
	// ========== Ranged Attack ==========
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile(EntityBoulderBlast.class, target, range, 0, new Vec3d(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    /** A multiplier that alters how much damage this mob receives from the given DamageSource, use for resistances and weaknesses. Note: The defense multiplier is handled before this. **/
    public float getDamageModifier(DamageSource damageSrc) {
        if("Jarno".equals(this.getCustomName()))
            return 0;

    	if(this.stoneForm) {
    		if(damageSrc.getTrueSource() != null) {
    			Item heldItem = null;
        		if(damageSrc.getTrueSource() instanceof LivingEntity) {
                    LivingEntity entityLiving = (LivingEntity)damageSrc.getTrueSource();
    	    		if(!entityLiving.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
    	    			heldItem = entityLiving.getHeldItem(Hand.MAIN_HAND).getItem();
    	    		}
        		}
        		
        		if(ObjectLists.isPickaxe(heldItem))
    				return 2.0F;
        	}
    		return 0.25F;
    	}
    	
    	if(damageSrc.isFireDamage())
    		return 2.0F;
    	return 1.0F;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
    	return 50;
    }
    
    @Override
    public boolean canBurn() { return !this.stoneForm; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
