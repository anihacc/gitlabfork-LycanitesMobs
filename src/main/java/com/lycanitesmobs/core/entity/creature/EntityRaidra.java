package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.damagesources.ElementDamageSource;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.info.ElementManager;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

public class EntityRaidra extends TameableCreatureEntity implements IMob {

    protected short aoeAttackTick = 0;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityRaidra(EntityType<? extends EntityRaidra> entityType, World world) {
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
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
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
                if(target != this && this.canAttack(target.getType()) && this.canAttack(target) && this.getEntitySenses().canSee(target)) {
                    target.attackEntityFrom(ElementDamageSource.causeElementDamage(this, ElementManager.getInstance().getElement("lightning")), this.getAttackDamage(1));
                }
            }
        }
        
        // Particles:
        if(this.getEntityWorld().isRemote && this.hasAttackTarget()) {
            //this.getEntityWorld().addParticle(ParticleTypes.CLOUD, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double) this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
            
            List aoeTargets = this.getNearbyEntities(LivingEntity.class, null, 4);
            for(Object entityObj : aoeTargets) {
                LivingEntity target = (LivingEntity)entityObj;
                if(this.canAttack(target.getType()) && this.canAttack(target) && this.getEntitySenses().canSee(target)) {
                    this.getEntityWorld().addParticle(ParticleTypes.CRIT, target.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double) target.getSize(Pose.STANDING).width, target.getPositionVec().getY() + this.rand.nextDouble() * (double) target.getSize(Pose.STANDING).height, target.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double) target.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
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
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.BagSize; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                     Immunities
    // ==================================================
    /** Returns whether or not the given damage type is applicable, if not no damage will be taken. **/
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if("lightningBolt".equalsIgnoreCase(type))
    		return false;
    	return super.isVulnerableTo(type, source, damage);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
}
