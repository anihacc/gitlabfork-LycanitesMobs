package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.damagesources.ElementDamageSource;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.info.ElementManager;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

public class EntityZephyr extends TameableCreatureEntity implements IMob {

    protected short aoeAttackTick = 0;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityZephyr(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Static Aura Attack:
        if(!this.getEntityWorld().isRemote && this.hasAttackTarget() && ++this.aoeAttackTick == (this.isPetType("familiar") ? 100 : 40)) {
            this.aoeAttackTick = 0;
            List aoeTargets = this.getNearbyEntities(EntityLivingBase.class, null, 4);
            for(Object entityObj : aoeTargets) {
                EntityLivingBase target = (EntityLivingBase)entityObj;
                if(target != this && this.canAttackClass(target.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target)) {
                    target.attackEntityFrom(ElementDamageSource.causeElementDamage(this, ElementManager.getInstance().getElement("lightning")), this.getAttackDamage(1));
                }
            }
        }
        
        // Particles:
        if(this.getEntityWorld().isRemote && this.hasAttackTarget()) {
            //this.getEntityWorld().spawnParticle(EnumParticleTypes.CLOUD, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);
            
            List aoeTargets = this.getNearbyEntities(EntityLivingBase.class, null, 4);
            for(Object entityObj : aoeTargets) {
                EntityLivingBase target = (EntityLivingBase)entityObj;
                if(this.canAttackClass(target.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target)) {
                    this.getEntityWorld().spawnParticle(EnumParticleTypes.CRIT, target.posX + (this.rand.nextDouble() - 0.5D) * (double) target.width, target.posY + this.rand.nextDouble() * (double) target.height, target.posZ + (this.rand.nextDouble() - 0.5D) * (double) target.width, 0.0D, 0.0D, 0.0D);
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
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
    	if("lightningBolt".equalsIgnoreCase(type))
    		return false;
    	return super.isDamageTypeApplicable(type, source, damage);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
}
