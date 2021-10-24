package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.damagesources.ElementDamageSource;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.info.ElementManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;

public class Abaia extends TameableCreatureEntity implements Enemy {

    protected short aoeAttackTick = 0;

    public Abaia(EntityType<? extends Abaia> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0.05D;
        this.canGrow = true;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(!this.getCommandSenderWorld().isClientSide && this.hasAttackTarget() && ++this.aoeAttackTick == (this.isPetType("familiar") ? 100 : 40)) {
            this.aoeAttackTick = 0;
            List aoeTargets = this.getNearbyEntities(LivingEntity.class, null, 4);
            for(Object entityObj : aoeTargets) {
                LivingEntity target = (LivingEntity)entityObj;
                if(target != this && this.canAttackType(target.getType()) && this.canAttack(target) && this.getSensing().hasLineOfSight(target) && (target.isInWater() || this.isRareVariant())) {
                    target.hurt(ElementDamageSource.causeElementDamage(this, ElementManager.getInstance().getElement("lightning")), this.getAttackDamage(1));
                }
            }
        }

        if(this.getCommandSenderWorld().isClientSide && this.hasAttackTarget()) {
            this.getCommandSenderWorld().addParticle(ParticleTypes.CRIT, this.position().x() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double) this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);

            List aoeTargets = this.getNearbyEntities(LivingEntity.class, null, 4);
            for(Object entityObj : aoeTargets) {
                LivingEntity target = (LivingEntity)entityObj;
                if(this.canAttackType(target.getType()) && this.canAttack(target) && this.getSensing().hasLineOfSight(target)) {
                    this.getCommandSenderWorld().addParticle(ParticleTypes.CRIT, target.position().x() + (this.random.nextDouble() - 0.5D) * (double) target.getDimensions(Pose.STANDING).width, target.position().y() + this.random.nextDouble() * (double) target.getDimensions(Pose.STANDING).height, target.position().z() + (this.random.nextDouble() - 0.5D) * (double) target.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;

        Block block = this.getCommandSenderWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
        if(block == Blocks.WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        if(this.getCommandSenderWorld().isRaining() && this.getCommandSenderWorld().canSeeSkyFromBelowWater(new BlockPos(x, y, z)))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }

	@Override
	public boolean isStrongSwimmer() {
		return true;
	}

	@Override
	public boolean canWalk() {
		return false;
	}

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
        if("lightning".equalsIgnoreCase(type))
            return false;
        return super.isVulnerableTo(type, source, damage);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAir() {
        return false;
    }
}
