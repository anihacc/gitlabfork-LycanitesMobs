package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.StealthGoal;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntitySpectre extends TameableCreatureEntity implements IMob, IGroupHeavy {

	protected int pullRange = 6;
	protected int pullEnergy = 0;
	protected int pullEnergyMax = 2 * 20;
	protected int pullEnergyRecharge = 0;
	protected int pullEnergyRechargeMax = 4 * 20;
	protected boolean pullRecharging = true;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySpectre(EntityType<? extends EntitySpectre> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spawnsInWater = true;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextPriorityGoalIndex++, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

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
				for (LivingEntity entity : this.getNearbyEntities(LivingEntity.class, null, this.pullRange)) {
					if (entity == this || entity == this.getControllingPassenger() || entity instanceof IGroupBoss || entity instanceof IGroupHeavy || entity.isPotionActive(ObjectManager.getEffect("weight")) || !this.canAttack(entity))
						continue;
					ServerPlayerEntity player = null;
					if (entity instanceof ServerPlayerEntity) {
						player = (ServerPlayerEntity) entity;
						if (player.abilities.isCreativeMode)
							continue;
					}
					double xDist = this.posX - entity.posX;
					double zDist = this.posZ - entity.posZ;
					double xzDist = MathHelper.sqrt(xDist * xDist + zDist * zDist);
					double factor = 0.1D;
					double motionCap = 10;
					if(entity.getMotion().getX() < motionCap && entity.getMotion().getX() > -motionCap && entity.getMotion().getZ() < motionCap && entity.getMotion().getZ() > -motionCap) {
						entity.addVelocity(
								xDist / xzDist * factor + entity.getMotion().getX() * factor,
								0,
								zDist / xzDist * factor + entity.getMotion().getZ() * factor
						);
					}
					if (player != null)
						player.connection.sendPacket(new SEntityVelocityPacket(entity));
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
	            this.getEntityWorld().addParticle(ParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.posY + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
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
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("inWall")) return false;
        return super.isInvulnerableTo(type, source, damage);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
}
