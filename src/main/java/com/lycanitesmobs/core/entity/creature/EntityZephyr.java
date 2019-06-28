package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupElectric;
import com.lycanitesmobs.api.IGroupWater;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

public class EntityZephyr extends TameableCreatureEntity implements IMob, IGroupWater, IGroupElectric {

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
        this.goalSelector.addGoal(0, new PaddleGoal(this));
        this.goalSelector.addGoal(2, new AttackMeleeGoal(this));
        this.goalSelector.addGoal(3, this.stayGoal);
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(8, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RevengeOwnerGoal(this));
        this.targetSelector.addGoal(1, new CopyOwnerAttackTargetGoal(this));
        this.targetSelector.addGoal(2, new RevengeGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).addTargets(EntityType.PLAYER));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).addTargets(EntityType.VILLAGER));
        this.targetSelector.addGoal(6, new DefendOwnerGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

        // Static Aura Attack:
        if(!this.getEntityWorld().isRemote && this.hasAttackTarget() && ++this.aoeAttackTick == (this.isPetType("familiar") ? 100 : 40)) {
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
        if(this.getEntityWorld().isRemote && this.hasAttackTarget()) {
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
