package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.entity.projectile.EntityChaosOrb;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityArgus extends TameableCreatureEntity implements IMob, IFusable {

	private int teleportTime = 60;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityArgus(EntityType<? extends EntityArgus> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spawnsInWater = true;
        this.setupMob();

        this.stepHeight = 1.0F;

        this.setAttackCooldownMax(40);
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
		this.goalSelector.addGoal(1, new FollowFuseGoal(this).setLostDistance(16));
        this.goalSelector.addGoal(2, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.goalSelector.addGoal(3, new AttackMeleeGoal(this).setLongMemory(true).setMaxChaseDistanceSq(5.0F));
		this.goalSelector.addGoal(4, new AttackRangedGoal(this).setSpeed(0.75D).setRange(16.0F).setMinChaseDistance(14.0F));
        this.goalSelector.addGoal(5, this.aiSit);
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(8, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RevengeOwnerGoal(this));
        this.targetSelector.addGoal(1, new CopyOwnerAttackTargetGoal(this));
        this.targetSelector.addGoal(2, new RevengeGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).setTargetClass(EntityAegis.class));
        this.targetSelector.addGoal(6, new DefendOwnerGoal(this));
		this.targetSelector.addGoal(7, new FindFuseTargetGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        /*/ Random Target Teleporting:
		if(!this.getEntityWorld().isRemote && this.hasAttackTarget()) {
			if(this.teleportTime-- <= 0) {
				this.teleportTime = 20 + this.getRNG().nextInt(20);
				BlockPos teleportPosition = this.getFacingPosition(this.getAttackTarget(), -this.getAttackTarget().getSize(Pose.STANDING).width - 1D, 0);
				if(this.canTeleportTo(teleportPosition)) {
					this.playJumpSound();
					this.setPosition(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
				}
			}
		}*/
        
        // Particles:
        if(this.getEntityWorld().isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.getEntityWorld().addParticle(ParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.posY + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	        }
    }


	// ==================================================
	//                      Attacks
	// ==================================================
	// ========== Ranged Attack ==========
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile(EntityChaosOrb.class, target, range, 0, new Vec3d(0, 0, 0), 0.6f, 1f, 1F);
		super.attackRanged(target, range);
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


	// ==================================================
	//                      Fusion
	// ==================================================
	protected IFusable fusionTarget;

	@Override
	public IFusable getFusionTarget() {
		return this.fusionTarget;
	}

	@Override
	public void setFusionTarget(IFusable fusionTarget) {
		this.fusionTarget = fusionTarget;
	}

	@Override
	public EntityType<? extends LivingEntity> getFusionType(IFusable fusable) {
		if(fusable instanceof EntityCinder) {
			return CreatureManager.getInstance().getEntityType("grue");
		}
		if(fusable instanceof EntityJengu) {
			return CreatureManager.getInstance().getEntityType("eechetik");
		}
		if(fusable instanceof EntityGeonach) {
			return CreatureManager.getInstance().getEntityType("tremor");
		}
		if(fusable instanceof EntityDjinn) {
			return CreatureManager.getInstance().getEntityType("wraith");
		}
		if(fusable instanceof EntityAegis) {
			return CreatureManager.getInstance().getEntityType("spectre");
		}
		return null;
	}
}
