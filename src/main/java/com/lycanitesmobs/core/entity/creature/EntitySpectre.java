package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.api.IGroupShadow;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntityMP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntitySpectre extends EntityCreatureTameable implements IMob, IGroupShadow, IGroupHeavy {

	protected int pullRange = 6;
	protected int pullEnergy = 0;
	protected int pullEnergyMax = 2 * 20;
	protected int pullEnergyRecharge = 0;
	protected int pullEnergyRechargeMax = 4 * 20;
	protected boolean pullRecharging = true;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySpectre(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spawnsInWater = true;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIStealth(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setLongMemory(true));
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }

    // ========== Set Size ==========
    @Override
    public void setSize(float width, float height) {
        if(this.getSubspeciesIndex() == 3) {
            super.setSize(width * 2, height * 2);
            return;
        }
        super.setSize(width, height);
    }

    @Override
    public double getRenderScale() {
        if(this.getSubspeciesIndex() == 3) {
            return this.sizeScale * 2;
        }
        return this.sizeScale;
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

		// Pull:
		if(!this.getEntityWorld().isRemote) {
			if(this.pullRecharging) {
				if(++this.pullEnergyRecharge >= this.pullEnergyRechargeMax) {
					this.pullRecharging = false;
					this.pullEnergy = this.pullEnergyMax;
					this.pullEnergyRecharge = 0;
				}
			}
			this.pullEnergy = Math.min(this.pullEnergy, this.pullEnergyMax);
			if(this.canPull()) {
				for (EntityLivingBase entity : this.getNearbyEntities(EntityLivingBase.class, null, this.pullRange)) {
					if (entity == this || entity == this.getControllingPassenger() || entity instanceof IGroupBoss || entity instanceof IGroupHeavy || entity.isPotionActive(ObjectManager.getPotionEffect("weight")) || !this.canAttackEntity(entity))
						continue;
					PlayerEntityMP player = null;
					if (entity instanceof PlayerEntityMP) {
						player = (PlayerEntityMP) entity;
						if (player.capabilities.isCreativeMode)
							continue;
					}
					double xDist = this.posX - entity.posX;
					double zDist = this.posZ - entity.posZ;
					double xzDist = MathHelper.sqrt(xDist * xDist + zDist * zDist);
					double factor = 0.1D;
					double motionCap = 10;
					if(entity.motionX < motionCap && entity.motionX > -motionCap && entity.motionZ < motionCap && entity.motionZ > -motionCap) {
						entity.addVelocity(
								xDist / xzDist * factor + entity.motionX * factor,
								0,
								zDist / xzDist * factor + entity.motionZ * factor
						);
					}
					if (player != null)
						player.connection.sendPacket(new SPacketEntityVelocity(entity));
				}
				if(--this.pullEnergy <= 0) {
					this.pullRecharging = true;
					this.pullEnergyRecharge = 0;
				}
			}
		}
        
        // Particles:
        if(this.getEntityWorld().isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.getEntityWorld().spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	        }
    }

	// ========== Extra Animations ==========
	/** An additional animation boolean that is passed to all clients through the animation mask. **/
	public boolean extraAnimation01() {
		if(this.getEntityWorld().isRemote) {
			return super.extraAnimation01();
		}
		return this.canPull();
	}

	// ========== Pull ==========
	public boolean canPull() {
		if(this.getEntityWorld().isRemote) {
			return this.extraAnimation01();
		}

		// Attack Target:
		return !this.pullRecharging && this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) <= (this.pullRange * 3);
	}
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean isStrongSwimmer() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
        if(type.equals("inWall")) return false;
        return super.isDamageTypeApplicable(type, source, damage);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
}
