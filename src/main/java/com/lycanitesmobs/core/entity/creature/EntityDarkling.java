package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.api.IGroupShadow;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.AttackTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.OwnerAttackTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.OwnerRevengeTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeTargetingGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityDarkling extends EntityCreatureTameable implements IMob, IGroupShadow {

    // Data Manager:
    protected static final DataParameter<Integer> LATCH_TARGET = EntityDataManager.<Integer>createKey(EntityCreatureBase.class, DataSerializers.field_187192_b);
    protected static final DataParameter<Float> LATCH_HEIGHT = EntityDataManager.<Float>createKey(EntityCreatureBase.class, DataSerializers.field_187193_c);
    protected static final DataParameter<Float> LATCH_ANGLE = EntityDataManager.<Float>createKey(EntityCreatureBase.class, DataSerializers.field_187193_c);

    // Latching
    LivingEntity latchEntity = null;
    int latchEntityID = 0;
    double latchHeight = 0.5D;
    double latchAngle = 90D;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityDarkling(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.hasAttackSound = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new SwimmingGoal(this));
        this.field_70714_bg.addTask(1, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.field_70714_bg.addTask(2, new AttackMeleeGoal(this));
        this.field_70714_bg.addTask(3, this.aiSit);
        this.field_70714_bg.addTask(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(6, new WanderGoal(this).setSpeed(1.0D).setPauseRate(30));
        this.field_70714_bg.addTask(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new LookIdleGoal(this));

        this.field_70715_bh.addTask(0, new OwnerRevengeTargetingGoal(this));
        this.field_70715_bh.addTask(1, new OwnerAttackTargetingGoal(this));
        this.field_70715_bh.addTask(2, new RevengeTargetingGoal(this));
        this.field_70715_bh.addTask(3, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(3, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(ChickenEntity.class));
            this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(RabbitEntity.class));
            this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(ParrotEntity.class));
            this.field_70715_bh.addTask(5, new AttackTargetingGoal(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(3, 1));
            this.field_70715_bh.addTask(5, new AttackTargetingGoal(this).setTargetClass(AnimalEntity.class).setPackHuntingScale(3, 1));
        }
    }

    // ========== Init ==========
    /** Initiates the entity setting all the values to be watched by the datawatcher. **/
    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(LATCH_TARGET, 0);
        this.dataManager.register(LATCH_HEIGHT, (float)this.latchHeight);
        this.dataManager.register(LATCH_ANGLE, (float)this.latchAngle);
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Leap:
        if(!this.getEntityWorld().isRemote && this.hasAttackTarget() && !this.hasLatchTarget() && this.onGround && !this.getEntityWorld().isRemote && this.rand.nextInt(10) == 0)
        	this.leap(6.0F, 0.6D, this.getAttackTarget());

        // Latch:
        if(this.hasLatchTarget()) {
            this.noClip = true;

            // Movement:
            Vec3d latchPos = this.getFacingPositionDouble(this.getLatchTarget().posX, this.getLatchTarget().posY + (this.getLatchTarget().getSize(Pose.STANDING).height * this.latchHeight), this.getLatchTarget().posZ, this.getLatchTarget().getSize(Pose.STANDING).width * 0.5D, this.latchAngle);
            this.setPosition(latchPos.x, latchPos.y, latchPos.z);
            double distanceX = this.getLatchTarget().posX - this.posX;
            double distanceZ = this.getLatchTarget().posZ - this.posZ;
            this.renderYawOffset = this.rotationYaw = -((float) MathHelper.atan2(distanceX, distanceZ)) * (180F / (float)Math.PI);

            // Server:
            if(!this.getEntityWorld().isRemote) {
                if(this.getLatchTarget().isAlive() && !this.isInWater()) {
                    if (this.updateTick % 40 == 0) {
                        float damage = this.getAttackDamage(1);
                        if (this.attackMelee(this.getLatchTarget(), damage))
                            this.heal(damage * 2);
                    }
                }
                else {
                    this.setPosition(this.getLatchTarget().posX, this.getLatchTarget().posY, this.getLatchTarget().posZ);
                    this.setLatchTarget(null);
                    this.noClip = false;
                }
            }

            // Client:
            else {
                for(int i = 0; i < 2; ++i) {
                    this.getEntityWorld().addParticle(RedstoneParticleData.REDSTONE_DUST, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.posY + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        else
            this.noClip = false;
    }


    // ==================================================
    //                     Latching
    // ==================================================
    public LivingEntity getLatchTarget() {
        try {
            if (this.getEntityWorld().isRemote) {
                this.latchHeight = this.dataManager.get(LATCH_HEIGHT);
                this.latchAngle = this.dataManager.get(LATCH_ANGLE);
                int latchEntityID = this.getDataManager().get(LATCH_TARGET);
                if (latchEntityID != this.latchEntityID) {
                    this.latchEntity = null;
                    this.latchEntityID = latchEntityID;
                    if (latchEntityID != 0) {
                        Entity possilbeLatchEntity = this.getEntityWorld().getEntityByID(latchEntityID);
                        if (possilbeLatchEntity != null && possilbeLatchEntity instanceof LivingEntity)
                            this.latchEntity = (LivingEntity) possilbeLatchEntity;
                    }
                }
            }
        }
        catch (Exception e) {}
        return this.latchEntity;
    }

    public void setLatchTarget(LivingEntity entity) {
        this.latchEntity = entity;
        if(this.getEntityWorld().isRemote)
            return;
        if(entity == null) {
            this.getDataManager().set(LATCH_TARGET, 0);
            return;
        }
        this.getDataManager().set(LATCH_TARGET, entity.getEntityId());
        this.latchHeight = 0.25D + (0.75D * this.getRNG().nextDouble());
        this.latchAngle = 360 * this.getRNG().nextDouble();
        this.dataManager.set(LATCH_HEIGHT, (float) this.latchHeight);
        this.dataManager.set(LATCH_ANGLE, (float) this.latchAngle);
    }

    public boolean hasLatchTarget() {
        return this.getLatchTarget() != null;
    }
    
    
    // ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Can Attack Class ==========
    @Override
    public boolean canAttack(EntityType targetType) {
        if(this.hasLatchTarget())
            return false;
        return super.canAttack(targetType);
    }

    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
        // Disable Knockback:
        double targetKnockbackResistance = 0;
        if(target instanceof LivingEntity) {
            targetKnockbackResistance = ((LivingEntity)target).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getValue();
            ((LivingEntity)target).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        }

        // Melee Attack:
    	if(!super.attackMelee(target, damageScale))
    		return false;

        // Restore Knockback:
        if(target instanceof LivingEntity)
            ((LivingEntity)target).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
    	
    	// Latch:
        if(!this.hasLatchTarget() && target instanceof LivingEntity && !this.isInWater()) {
        	this.setLatchTarget((LivingEntity) target);
        }
        
        return true;
    }


    // ==================================================
    //                     Abilities
    // ==================================================
    // ========== Movement ==========
    @Override
    public boolean canClimb() { return true; }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                     Stealth
    // ==================================================
    @Override
    public boolean canStealth() {
        if(this.getEntityWorld().isRemote) return false;
        if(this.isMoving()) return false;
        return this.testLightLevel() <= 0;
    }

    @Override
    public void startStealth() {
        if(this.getEntityWorld().isRemote) {
            IParticleData particle = ParticleTypes.WITCH;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            for(int i = 0; i < 100; i++)
                this.getEntityWorld().addParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).height), this.posZ + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, d0, d1, d2);
        }
        super.startStealth();
    }
    
    
    // ==================================================
  	//                     Immunities
  	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("inWall"))
            return false;
        return super.isInvulnerableTo(type, source, damage);
    }

    @Override
    public float getFallResistance() {
        return 10;
    }
}
