package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.entity.projectile.EntityBoulderBlast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityTroll extends EntityCreatureTameable implements IMob {
	
	public boolean trollGreifing = true;
	
	// ========== Unique Entity Variables ==========
	public boolean stoneForm = false;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTroll(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;

        //this.canGrow = false;
        //this.babySpawnChance = 0.01D;
        
        this.trollGreifing = ConfigBase.getConfig(this.creatureInfo.modInfo, "general").getBool("Features", "Troll Griefing", this.trollGreifing, "Set to false to disable Troll block destruction.");
        this.solidCollision = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        if(this.getNavigator() instanceof PathNavigateGround) {
            PathNavigateGround pathNavigateGround = (PathNavigateGround)this.getNavigator();
            pathNavigateGround.setBreakDoors(true);
        }
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIBreakDoor(this));
        this.tasks.addTask(5, new EntityAIAttackRanged(this).setSpeed(0.5D).setRange(14.0F).setMinChaseDistance(5.0F));
        this.tasks.addTask(6, this.aiSit);
        this.tasks.addTask(7, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
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
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
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
 	        if(this.getAttackTarget() != null && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.trollGreifing) {
 		    	float distance = this.getAttackTarget().getDistance(this);
 		    		if(distance <= this.width + 4.0F)
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
        if("Jarno".equals(this.getCustomNameTag()))
            return 0;

    	if(this.stoneForm) {
    		if(damageSrc.getTrueSource() != null) {
    			Item heldItem = null;
        		if(damageSrc.getTrueSource() instanceof LivingEntity) {
                    LivingEntity entityLiving = (LivingEntity)damageSrc.getTrueSource();
    	    		if(entityLiving.getHeldItem(EnumHand.MAIN_HAND) != null) {
    	    			heldItem = entityLiving.getHeldItem(EnumHand.MAIN_HAND).getItem();
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
