package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.PotionBase;
import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityLurker extends EntityCreatureTameable implements IGroupHunter {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityLurker(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIStealth(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.tasks.addTask(2, new EntityAIAvoid(this).setNearSpeed(2.0D).setFarSpeed(1.5D).setNearDistance(5.0D).setFarDistance(10.0D));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setLongMemory(false));
        this.tasks.addTask(4, this.aiSit);
        this.tasks.addTask(5, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.tasks.addTask(6, new EntityAITempt(this).setTemptDistanceMin(2.0D));
        this.tasks.addTask(7, new EntityAIMate(this));
        this.tasks.addTask(8, new EntityAIFollowParent(this));
        this.tasks.addTask(9, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIBeg(this));
        this.tasks.addTask(11, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(12, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityChicken.class));
            this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        }
        this.targetTasks.addTask(0, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Lurker Blind Stalking:
        if(this.getAttackTarget() != null) {
        	PotionBase stalkingEffect = ObjectManager.getPotionEffect("plague");
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
	    		if(this.getAttackTarget() instanceof EntityPlayer) {
	    			EntityPlayer playerTarget = (EntityPlayer)this.getAttackTarget();
	    			ItemStack itemstack = playerTarget.inventory.getCurrentItem();
	    			if(this.isTamingItem(itemstack))
	    				return false;
	    		}
				PotionBase stalkingEffect = ObjectManager.getPotionEffect("plague");
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
            EnumParticleTypes particle = EnumParticleTypes.SMOKE_NORMAL;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            for(int i = 0; i < 100; i++)
            	this.getEntityWorld().spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
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
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player) {
    	if(this.isTamed() && this.getOwner() == player)
    		return false;
        return this.isInvisible();
    }
}
