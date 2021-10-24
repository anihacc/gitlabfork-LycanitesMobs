package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class Belphegor extends TameableCreatureEntity implements Enemy {

    protected static final EntityDataAccessor<Integer> HELLFIRE_ENERGY = SynchedEntityData.defineId(Belphegor.class, EntityDataSerializers.INT);

    public int hellfireEnergy = 0;
    public List<EntityHellfireOrb> hellfireOrbs = new ArrayList<>();
    
    public Belphegor(EntityType<? extends Belphegor> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEAD;
        this.hasAttackSound = false;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        //this.goalSelector.addGoal(this.nextTravelGoalIndex++, new MoveRestrictionGoal(this));

        super.registerGoals();

        this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(8.0F).setChaseTime(-1));

        if(this.getNavigation() instanceof GroundPathNavigation) {
            GroundPathNavigation pathNavigateGround = (GroundPathNavigation)this.getNavigation();
            pathNavigateGround.setCanOpenDoors(true);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HELLFIRE_ENERGY, this.hellfireEnergy);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.getCommandSenderWorld().isClientSide) {
            this.entityData.set(HELLFIRE_ENERGY, this.hellfireEnergy);
        }
        else {
            try {
                this.hellfireEnergy = this.entityData.get(HELLFIRE_ENERGY);
            }
            catch(Exception e) {}
        }

        if(this.getCommandSenderWorld().isClientSide && this.hellfireEnergy > 0)
            Rahovart.updateHellfireOrbs(this, this.updateTick, 3, this.hellfireEnergy, 0.5F, this.hellfireOrbs);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
    	if(this.isTamed())
    		return super.canAttack(target);
    	if(target instanceof Behemophet)
    		return false;
        return super.canAttack(target);
    }

	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile("doomfireball", target, range, 0, new Vec3(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}

    public boolean petControlsEnabled() { return true; }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean canBurn() { return false; }
}
