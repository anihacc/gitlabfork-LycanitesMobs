package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class Zephyr extends TameableCreatureEntity implements Enemy, IFusable {

	public float fireDamageAbsorbed = 0;

    public Zephyr(EntityType<? extends Zephyr> entityType, Level world) {
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
    }

	@Override
    public void aiStep() {
        super.aiStep();

		if(!this.getCommandSenderWorld().isClientSide) {

			if(!this.isTamed()) {
				if (this.fireDamageAbsorbed >= 10) {
					this.transform(CreatureManager.getInstance().getEntityType("zephyr"), null, false);
				}
			}
		}

        // Particles:
        //if(this.getEntityWorld().isRemote)
            //this.getEntityWorld().addParticle(ParticleTypes.SNOWBALL, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void attackRanged(Entity target, float range) {
    	int projectileCount = 10;
    	for(int i = 0; i < projectileCount; i++) {
    		this.fireProjectile("whirlwind", target, range, (360 / projectileCount) * i, new Vec3(0, 0, 0), 0.6f, 2f, 1F);
		}
        super.attackRanged(target, range);
    }

    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean isStrongSwimmer() { return false; }


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    public boolean petControlsEnabled() { return true; }
    
    

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("cactus") || type.equals("inWall"))
            return false;
        if(source.isFire()) {
            this.fireDamageAbsorbed += damage;
            return false;
        }
        if(type.equals("lightningBolt") && !this.isTamed()) {
        	this.transform(CreatureManager.getInstance().getEntityType("zephyr"), null, false);
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
		if(fusable instanceof Cinder) {
			return CreatureManager.getInstance().getEntityType("raidra");
		}
        if(fusable instanceof Jengu) {
			return CreatureManager.getInstance().getEntityType("reiver");
        }
        if(fusable instanceof Geonach) {
			return CreatureManager.getInstance().getEntityType("banshee");
        }
        if(fusable instanceof Aegis) {
			return CreatureManager.getInstance().getEntityType("sylph");
        }
		if(fusable instanceof Argus) {
			return CreatureManager.getInstance().getEntityType("wraith");
		}
        return null;
    }
}
