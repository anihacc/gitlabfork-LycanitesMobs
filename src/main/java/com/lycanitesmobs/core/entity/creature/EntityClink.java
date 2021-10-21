package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class EntityClink extends TameableCreatureEntity implements Enemy {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityClink(EntityType<? extends EntityClink> entityType, Level world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = false;
        this.setupMob();
        
        // Stats:
        this.attackPhaseMax = 3;
        this.setAttackCooldownMax(10);
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(4.0F));
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile("throwingscythe", target, range, 0, new Vec3(0, 0, 0), 1.2f, 2f, 1F);
		this.nextAttackPhase();
		super.attackRanged(target, range);
	}

    @Override
	public int getMeleeCooldown() {
    	if(this.getAttackPhase() == 2)
    		return super.getMeleeCooldown();
		return Math.round((float)super.getMeleeCooldown() / 6);
	}

	@Override
	public int getRangedCooldown() {
		if(this.getAttackPhase() == 2)
			return super.getRangedCooldown();
		return Math.round((float)super.getRangedCooldown() / 6);
	}
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	return super.isVulnerableTo(type, source, damage);
    }

    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
