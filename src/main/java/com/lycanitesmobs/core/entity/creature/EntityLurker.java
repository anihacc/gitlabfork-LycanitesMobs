package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.EffectBase;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityLurker extends TameableCreatureEntity implements IGroupHunter {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityLurker(EntityType<? extends EntityLurker> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(1, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.goalSelector.addGoal(2, new AvoidGoal(this).setNearSpeed(2.0D).setFarSpeed(1.5D).setNearDistance(5.0D).setFarDistance(10.0D));
        this.goalSelector.addGoal(3, new AttackMeleeGoal(this).setLongMemory(false));
        this.goalSelector.addGoal(4, this.aiSit);
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(6, new TemptGoal(this).setTemptDistanceMin(2.0D));
        this.goalSelector.addGoal(7, new MateGoal(this));
        this.goalSelector.addGoal(8, new FollowParentGoal(this));
        this.goalSelector.addGoal(9, new WanderGoal(this));
        this.goalSelector.addGoal(10, new BegGoal(this));
        this.goalSelector.addGoal(11, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(12, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RevengeOwnerGoal(this));
        this.targetSelector.addGoal(1, new CopyOwnerAttackTargetGoal(this));
        this.targetSelector.addGoal(2, new RevengeGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(3, new FindAttackTargetGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(3, new FindAttackTargetGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(3, new FindAttackTargetGoal(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetSelector.addGoal(3, new FindAttackTargetGoal(this).setTargetClass(ChickenEntity.class));
            this.targetSelector.addGoal(3, new FindAttackTargetGoal(this).setTargetClass(IGroupPrey.class));
        }
        this.targetSelector.addGoal(0, new FindParentGoal(this).setSightCheck(false).setDistance(32.0D));
        this.targetSelector.addGoal(6, new DefendOwnerGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Lurker Blind Stalking:
        if(this.getAttackTarget() != null) {
        	EffectBase stalkingEffect = ObjectManager.getEffect("plague");
        	if(stalkingEffect != null && this.getAttackTarget().isPotionActive(stalkingEffect))
        		this.setAvoidTarget(this.getAttackTarget());
        	else
        		this.setAvoidTarget(null);
        }
        else
        	this.setAvoidTarget(null);
        
        // Leap:
        if(this.onGround && !this.getEntityWorld().isRemote && this.rand.nextInt(10) == 0) {
        	if(this.hasAttackTarget())
        		this.leap(6.0F, 0.4D, this.getAttackTarget());
        	else if(this.hasAvoidTarget())
        		this.leap(4.0F, 0.4D);
        }
    }
    
    
    // ==================================================
   	//                     Stealth
   	// ==================================================
    @Override
    public boolean canStealth() {
    	if(this.getEntityWorld().isRemote) return false;
    	else {
	    	if(this.hasAttackTarget()) {
	    		if(this.getAttackTarget() instanceof PlayerEntity) {
	    			PlayerEntity playerTarget = (PlayerEntity)this.getAttackTarget();
	    			ItemStack itemstack = playerTarget.inventory.getCurrentItem();
	    			if(this.isTamingItem(itemstack))
	    				return false;
	    		}
				EffectBase stalkingEffect = ObjectManager.getEffect("plague");
	    		if(stalkingEffect != null) {
					if(!this.getAttackTarget().isPotionActive(stalkingEffect))
						return false;
				}
	    		if(this.getDistance(this.getAttackTarget()) < (5.0D * 5.0D))
	    			return false;
	    	}
	    	else {
	    		if(this.isMoving())
	    			return false;
	    	}
	        return true;
        }
    }
    
    @Override
    public void startStealth() {
    	if(this.getEntityWorld().isRemote) {
            IParticleData particle = ParticleTypes.SMOKE;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            for(int i = 0; i < 100; i++)
            	this.getEntityWorld().addParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).height), this.posZ + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, d0, d1, d2);
        }
    	super.startStealth();
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canClimb() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("CookedMeat", testStack);
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isInvisibleToPlayer(PlayerEntity player) {
    	if(this.isTamed() && this.getOwner() == player)
    		return false;
        return this.isInvisible();
    }
}
