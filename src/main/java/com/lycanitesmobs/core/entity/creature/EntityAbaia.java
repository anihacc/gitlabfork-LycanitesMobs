package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupElectric;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EntityAbaia extends EntityCreatureTameable implements IMob, IGroupPredator, IGroupElectric {

	WanderGoal wanderAI;
    protected short aoeAttackTick = 0;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAbaia(EntityType<? extends EntityAbaia> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0.05D;
        this.canGrow = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new StayByWaterGoal(this));
        this.goalSelector.addGoal(2, this.aiSit);
        this.goalSelector.addGoal(3, new AttackMeleeGoal(this).setLongMemory(false));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.wanderAI = new WanderGoal(this);
        this.goalSelector.addGoal(7, wanderAI.setPauseRate(0));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new OwnerAttackTargetingGoal(this));
        this.targetSelector.addGoal(2, new RevengeTargetingGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(IGroupAnimal.class));
            this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(AnimalEntity.class));
        }
        this.targetSelector.addGoal(6, new OwnerDefenseTargetingGoal(this));
    }

	// ========== Set Size ==========
	@Override
	public void setSizeScale(double scale) {
		if(this.isRareSubspecies()) {
			super.setSizeScale(scale * 3);
			return;
		}
		super.setSizeScale(scale);
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
            this.getEntityWorld().addParticle(ParticleTypes.CRIT, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width, this.posY + this.rand.nextDouble() * (double) this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);

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
    //                      Movement
    // ==================================================
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;

        Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
        if(block == Blocks.WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        if(this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(new BlockPos(x, y, z)))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getAttackTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }
	
	// Swimming:
	@Override
	public boolean isStrongSwimmer() {
		return true;
	}
	
	// Walking:
	@Override
	public boolean canWalk() {
		return false;
	}
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    // ========== Damage ==========
    /** Returns whether or not the given damage type is applicable, if not no damage will be taken. **/
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
        if("lightning".equalsIgnoreCase(type))
            return false;
        return super.isInvulnerableTo(type, source, damage);
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
