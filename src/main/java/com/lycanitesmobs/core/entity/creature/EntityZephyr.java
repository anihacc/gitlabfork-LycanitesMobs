package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupElectric;
import com.lycanitesmobs.api.IGroupWater;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

public class EntityZephyr extends EntityCreatureTameable implements IMob, IGroupWater, IGroupElectric {

    protected short aoeAttackTick = 0;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityZephyr(EntityType<? extends EntityZephyr> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(2, new AttackMeleeGoal(this));
        this.goalSelector.addGoal(3, this.aiSit);
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(8, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new OwnerAttackTargetingGoal(this));
        this.targetSelector.addGoal(2, new RevengeTargetingGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(6, new OwnerDefenseTargetingGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

        // Static Aura Attack:
        if(!this.getEntityWorld().isRemote && ++this.aoeAttackTick == (this.isPetType("familiar") ? 100 : 40)) {
            this.aoeAttackTick = 0;
            List aoeTargets = this.getNearbyEntities(LivingEntity.class, null, 4);
            for(Object entityObj : aoeTargets) {
                LivingEntity target = (LivingEntity)entityObj;
                if(target != this && !(target instanceof IGroupElectric) && this.canAttack(target.getType()) && this.canAttack(target) && this.getEntitySenses().canSee(target)) {
                    target.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackDamage(1));
                }
            }
        }
        
        // Particles:
        if(this.getEntityWorld().isRemote) {
            //this.getEntityWorld().addParticle(ParticleTypes.CLOUD, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width, this.posY + this.rand.nextDouble() * (double) this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
            
            List aoeTargets = this.getNearbyEntities(LivingEntity.class, null, 4);
            for(Object entityObj : aoeTargets) {
                LivingEntity target = (LivingEntity)entityObj;
                if(this.canAttack(target.getType()) && this.canAttack(target) && this.getEntitySenses().canSee(target)) {
                    this.getEntityWorld().addParticle(ParticleTypes.CRIT, target.posX + (this.rand.nextDouble() - 0.5D) * (double) target.getSize(Pose.STANDING).width, target.posY + this.rand.nextDouble() * (double) target.getSize(Pose.STANDING).height, target.posZ + (this.rand.nextDouble() - 0.5D) * (double) target.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }


    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                     Immunities
    // ==================================================
    /** Returns whether or not the given damage type is applicable, if not no damage will be taken. **/
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if("lightningBolt".equalsIgnoreCase(type))
    		return false;
    	return super.isInvulnerableTo(type, source, damage);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
}
