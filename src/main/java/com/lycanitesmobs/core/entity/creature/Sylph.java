package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class Sylph extends TameableCreatureEntity implements Enemy {

	public float fireDamageAbsorbed = 0;

    public Sylph(EntityType<? extends Sylph> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = false;
        this.setupMob();

        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(16.0F).setMinChaseDistance(8.0F));

        this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(EntityType.PLAYER));
    }

	@Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    public void attackRanged(Entity target, float range) {
    	int projectileCount = 10;
    	for(int i = 0; i < projectileCount; i++) {
    		this.fireProjectile("aetherwave", target, range, (360 / projectileCount) * i, new Vec3(0, 0, 0), 0.6f, 2f, 1F);
		}
        super.attackRanged(target, range);
    }

    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean isStrongSwimmer() { return false; }



    

    public boolean petControlsEnabled() { return true; }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("cactus") || type.equals("inWall"))
            return false;
        if(source.isFire()) {
            this.fireDamageAbsorbed += damage;
            return false;
        }
        return super.isVulnerableTo(type, source, damage);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }
}