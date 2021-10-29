package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.GoalConditions;
import com.lycanitesmobs.core.entity.goals.actions.FindNearbyPlayersGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FaceTargetGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FireProjectilesGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.HealWhenNoPlayersGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.SummonMinionsGoal;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrier;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireWave;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Rahovart extends BaseCreatureEntity implements Enemy, IGroupHeavy, IGroupBoss {

    public int hellfireEnergy = 0;
    public List<EntityHellfireOrb> hellfireOrbs = new ArrayList<>();

    protected static final EntityDataAccessor<Integer> HELLFIRE_ENERGY = SynchedEntityData.defineId(Rahovart.class, EntityDataSerializers.INT);

    public List<Belphegor> hellfireBelphegorMinions = new ArrayList<>();

    public List<Behemophet> hellfireBehemophetMinions = new ArrayList<>();
    public int hellfireWallTime = 0;
    public int hellfireWallTimeMax = 20 * 20;
    public boolean hellfireWallClockwise = false;
    public EntityHellfireBarrier hellfireWallLeft;
    public EntityHellfireBarrier hellfireWallRight;

    public List<EntityHellfireBarrier> hellfireBarriers = new ArrayList<>();
    public int hellfireBarrierHealth = 100;


    public Rahovart(EntityType<? extends Rahovart> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEAD;
        this.hasAttackSound = false;
        this.setAttackCooldownMax(40);
        this.solidCollision = true;
        // this.pushthrough = 1.0F; TODO Find a function for this
        this.setupMob();
        this.hitAreaWidthScale = 2F;

        this.damageMax = BaseCreatureEntity.BOSS_DAMAGE_LIMIT;
        this.damageLimit = BaseCreatureEntity.BOSS_DAMAGE_LIMIT;
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(this.nextFindTargetIndex, new FindNearbyPlayersGoal(this));

        this.goalSelector.addGoal(this.nextIdleGoalIndex, new FaceTargetGoal(this));
        this.goalSelector.addGoal(this.nextIdleGoalIndex, new HealWhenNoPlayersGoal(this));
        this.goalSelector.addGoal(this.nextIdleGoalIndex, new SummonMinionsGoal(this).setMinionInfo("wraith").setAntiFlight(true));
        this.goalSelector.addGoal(this.nextIdleGoalIndex, new FireProjectilesGoal(this).setProjectile("hellfireball").setFireRate(40).setVelocity(1.6F).setScale(8F).setAllPlayers(true));
        this.goalSelector.addGoal(this.nextIdleGoalIndex, new FireProjectilesGoal(this).setProjectile("hellfireball").setFireRate(60).setVelocity(1.6F).setScale(8F));

        this.goalSelector.addGoal(this.nextIdleGoalIndex, new SummonMinionsGoal(this).setMinionInfo("apollyon").setSummonRate(20 * 10).setSummonCap(1).setPerPlayer(true).setSizeScale(2)
                .setConditions(new GoalConditions().setBattlePhase(2)));

        super.registerGoals();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(Rahovart.HELLFIRE_ENERGY, this.hellfireEnergy);
    }

    @OnlyIn(Dist.CLIENT)
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(200, 50, 200).move(0, -25, 0);
    }

    @Override
    public void onFirstSpawn() {
        super.onFirstSpawn();
        if(this.getArenaCenter() == null) {
            this.setArenaCenter(this.blockPosition());
        }
    }

    public void setArenaCenter(BlockPos pos) {
        super.setArenaCenter(pos);
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(this.hasAttackTarget() && !this.getCommandSenderWorld().isClientSide) {
            this.getLookControl().setLookAt(this.getTarget(), 30.0F, 30.0F);
        }

        if(this.hasArenaCenter()) {
            BlockPos arenaPos = this.getArenaCenter();
            double arenaY = this.position().y();
            if (this.getCommandSenderWorld().isEmptyBlock(arenaPos))
                arenaY = arenaPos.getY();
            else if (this.getCommandSenderWorld().isEmptyBlock(arenaPos.offset(0, 1, 0)))
                arenaY = arenaPos.offset(0, 1, 0).getY();

            if (this.position().x() != arenaPos.getX() || this.position().y() != arenaY || this.position().z() != arenaPos.getZ())
                this.setPos(arenaPos.getX(), arenaY, arenaPos.getZ());
        }

        if(!this.getCommandSenderWorld().isClientSide)
            this.entityData.set(HELLFIRE_ENERGY, this.hellfireEnergy);
        else
            this.hellfireEnergy = this.entityData.get(HELLFIRE_ENERGY);

        updateHellfireOrbs(this, this.updateTick, 5, this.hellfireEnergy, 10, this.hellfireOrbs);

        if(!this.getCommandSenderWorld().isClientSide) {
			this.updatePhases();
		}
    }

    @Override
    public boolean rollWanderChance() {
        return false;
    }

    public void updatePhases() {

        if(this.getBattlePhase() == 0) {
            if(!this.hellfireBehemophetMinions.isEmpty()) {
                for (Behemophet minion : this.hellfireBehemophetMinions.toArray(new Behemophet[this.hellfireBehemophetMinions.size()])) {
                    minion.hellfireEnergy = 0;
                }
                this.hellfireBehemophetMinions = new ArrayList<>();
            }
            this.hellfireWallTime = 0;
            this.hellfireBarrierCleanup();

            if(this.updateTick % 20 == 0) {
                for (Belphegor minion : this.hellfireBelphegorMinions.toArray(new Belphegor[this.hellfireBelphegorMinions.size()])) {
                    if (!minion.isAlive()) {
                        this.onMinionDeath(minion, null);
                        continue;
                    }
                    minion.hellfireEnergy += 5;
                    if (minion.hellfireEnergy >= 100) {
                        this.hellfireEnergy += 20;
                        this.onMinionDeath(minion, null);
                        this.getCommandSenderWorld().explode(minion, minion.position().x(), minion.position().y(), minion.position().z(), 1, Explosion.BlockInteraction.NONE);
                        minion.hellfireEnergy = 0;
                        minion.discard();
                        continue;
                    }
                }
            }

            if(this.hellfireEnergy >= 100) {
                this.hellfireEnergy = 0;
                double angle = this.getRandom().nextFloat() * 360;
                if(this.hasAttackTarget()) {
                    double deltaX = this.getTarget().position().x() - this.position().x();
                    double deltaZ = this.getTarget().position().z() - this.position().z();
                    angle = Math.atan2(deltaZ, deltaX) * 180 / Math.PI;
                }
                this.hellfireWaveAttack(angle);
            }

            if(this.updateTick % 100 == 0) {
                int summonAmount = this.getRandom().nextInt(4);
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                    for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                        Belphegor minion = (Belphegor)CreatureManager.getInstance().getCreature("belphegor").createEntity(this.getCommandSenderWorld());
                        this.summonMinion(minion, this.getRandom().nextDouble() * 360, 5);
                        this.hellfireBelphegorMinions.add(minion);
                    }
            }
        }

        if(this.getBattlePhase() == 1) {
            if(!this.hellfireBelphegorMinions.isEmpty()) {
                for (Belphegor minion : this.hellfireBelphegorMinions.toArray(new Belphegor[this.hellfireBelphegorMinions.size()])) {
                    minion.hellfireEnergy = 0;
                }
                this.hellfireBelphegorMinions = new ArrayList<>();
            }
            this.hellfireBarrierCleanup();

            if(this.hellfireWallTime <= 0 && this.updateTick % 20 == 0) {
                for (Behemophet minion : this.hellfireBehemophetMinions.toArray(new Behemophet[this.hellfireBehemophetMinions.size()])) {
                    if (!minion.isAlive()) {
                        this.onMinionDeath(minion, null);
                        continue;
                    }
                    minion.hellfireEnergy += 5;
                    if (minion.hellfireEnergy >= 100) {
                        this.hellfireEnergy += 20;
                        this.onMinionDeath(minion, null);
                        this.getCommandSenderWorld().explode(minion, minion.position().x(), minion.position().y(), minion.position().z(), 1, Explosion.BlockInteraction.NONE);
                        minion.hellfireEnergy = 0;
                        minion.discard();
                        continue;
                    }
                }
            }

            if(this.hellfireEnergy >= 100) {
                this.hellfireEnergy = 0;
                this.hellfireWallAttack(this.getYRot());
            }

            if(this.hellfireWallTime > 0) {
                this.hellfireWallUpdate();
                this.hellfireWallTime--;
            }

            if(this.updateTick % 400 == 0) {
                int summonAmount = 2;
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                    for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                        Behemophet minion = (Behemophet)CreatureManager.getInstance().getCreature("behemophet").createEntity(this.getCommandSenderWorld());
                        this.summonMinion(minion, this.getRandom().nextDouble() * 360, 5);
                        this.hellfireBehemophetMinions.add(minion);
                    }
            }

            if(this.updateTick % 200 == 0) {
                int summonAmount = this.getRandom().nextInt(4) - 1;
                for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                    Belphegor minion = (Belphegor)CreatureManager.getInstance().getCreature("belphegor").createEntity(this.getCommandSenderWorld());
                    this.summonMinion(minion, this.getRandom().nextDouble() * 360, 5);
                }
            }
        }

        if(this.getBattlePhase() >= 2) {
            if(!this.hellfireBelphegorMinions.isEmpty()) {
                for (Belphegor minion : this.hellfireBelphegorMinions.toArray(new Belphegor[this.hellfireBelphegorMinions.size()])) {
                    minion.hellfireEnergy = 0;
                }
                this.hellfireBelphegorMinions = new ArrayList<>();
            }
            if(!this.hellfireBehemophetMinions.isEmpty()) {
                for (Behemophet minion : this.hellfireBehemophetMinions.toArray(new Behemophet[this.hellfireBehemophetMinions.size()])) {
                    minion.hellfireEnergy = 0;
                }
                this.hellfireBehemophetMinions = new ArrayList<>();
            }
            this.hellfireWallTime = 0;

            if(this.updateTick % 20 == 0) {
                if (this.hellfireEnergy < 100)
                    this.hellfireEnergy += 5;
            }

            if(this.hellfireEnergy >= 100 && this.hellfireBarriers.size() < 20) {
                this.hellfireEnergy = 0;
                this.hellfireBarrierAttack(360F * this.getRandom().nextFloat());
            }

            if(this.hellfireBarriers.size() > 0)
                this.hellfireBarrierUpdate();

            if(this.updateTick % 200 == 0) {
                int summonAmount = this.getRandom().nextInt(2);
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                    for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                        Behemophet minion = (Behemophet)CreatureManager.getInstance().getCreature("behemophet").createEntity(this.getCommandSenderWorld());
                        this.summonMinion(minion, this.getRandom().nextDouble() * 360, 5);
                        this.hellfireBehemophetMinions.add(minion);
                    }
            }

            if(this.updateTick % 400 == 0) {
                int summonAmount = this.getRandom().nextInt(4); 
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                    Belphegor minion = (Belphegor)CreatureManager.getInstance().getCreature("belphegor").createEntity(this.getCommandSenderWorld());
                    this.summonMinion(minion, this.getRandom().nextDouble() * 360, 5);
                }
                summonAmount = this.getRandom().nextInt(3);
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                    Wraith minion = (Wraith)CreatureManager.getInstance().getCreature("wraith").createEntity(this.getCommandSenderWorld());
                    this.summonMinion(minion, this.getRandom().nextDouble() * 360, 5);
                }
            }
        }

        if(this.hellfireWallTime <= 0)
            this.hellfireWallCleanup();
    }

    @Override
    public void onMinionDeath(LivingEntity minion, DamageSource damageSource) {
        if(minion instanceof Belphegor && this.hellfireBelphegorMinions.contains(minion)) {
            this.hellfireBelphegorMinions.remove(minion);
            return;
        }
        if(minion instanceof Behemophet && this.hellfireBehemophetMinions.contains(minion)) {
            this.hellfireBehemophetMinions.remove(minion);
            return;
        }
        if(this.hellfireBarriers.size() > 0) {
            if(minion instanceof Behemophet)
                this.hellfireBarrierHealth -= 100;
            else
                this.hellfireBarrierHealth -= 50;
        }
        super.onMinionDeath(minion, damageSource);
    }

    @Override
    public void updateBattlePhase() {
        double healthNormal = this.getHealth() / this.getMaxHealth();
        if(healthNormal <= 0.2D) {
            this.setBattlePhase(2);
            return;
        }
        if(healthNormal <= 0.6D) {
            this.setBattlePhase(1);
            return;
        }
        this.setBattlePhase(0);
    }

    public static void updateHellfireOrbs(LivingEntity entity, long orbTick, int hellfireOrbMax, int hellfireOrbEnergy, float orbSize, List<EntityHellfireOrb> hellfireOrbs) {
        if(!entity.getCommandSenderWorld().isClientSide)
            return;

        int hellfireChargeCount = Math.round((float)Math.min(hellfireOrbEnergy, 100) / (100F / hellfireOrbMax));
        int hellfireOrbRotationTime = 5 * 20;
        double hellfireOrbAngle = 360 * ((float)(orbTick % hellfireOrbRotationTime) / hellfireOrbRotationTime);
        double hellfireOrbAngleOffset = 360.0D / hellfireOrbMax;

        while(hellfireOrbs.size() < hellfireChargeCount) {
            EntityHellfireOrb hellfireOrb = new EntityHellfireOrb(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireOrb.class), entity.getCommandSenderWorld(), entity);
            hellfireOrb.clientOnly = true;
            hellfireOrbs.add(hellfireOrb);
            entity.getCommandSenderWorld().addFreshEntity(hellfireOrb);
            hellfireOrb.setProjectileScale(orbSize);
        }

        while(hellfireOrbs.size() > hellfireChargeCount) {
            hellfireOrbs.get(hellfireOrbs.size() - 1).discard();
            hellfireOrbs.remove(hellfireOrbs.size() - 1);
        }

        for(int i = 0; i < hellfireOrbs.size(); i++) {
            EntityHellfireOrb hellfireOrb = hellfireOrbs.get(i);
            double rotationRadians = Math.toRadians((hellfireOrbAngle + (hellfireOrbAngleOffset * i)) % 360);
            double x = (entity.getDimensions(Pose.STANDING).width * 1.25D) * Math.cos(rotationRadians) + Math.sin(rotationRadians);
            double z = (entity.getDimensions(Pose.STANDING).width * 1.25D) * Math.sin(rotationRadians) - Math.cos(rotationRadians);
            hellfireOrb.setPos(
                    entity.position().x() - x,
                    entity.position().y() + (entity.getDimensions(Pose.STANDING).height * 0.75F),
                    entity.position().z() - z
            );
            hellfireOrb.setPos(entity.position().x() - x, entity.position().y() + (entity.getDimensions(Pose.STANDING).height * 0.75F), entity.position().z() - z);
            hellfireOrb.projectileLife = 5;
        }
    }
	
    public boolean canAttack(LivingEntity targetEntity) {
        if(targetEntity instanceof Belphegor || targetEntity instanceof Behemophet || targetEntity instanceof Apollyon || targetEntity instanceof Wraith) {
            if(targetEntity instanceof TameableCreatureEntity)
                return ((TameableCreatureEntity)targetEntity).getOwner() instanceof Player;
            else
                return false;
        }
        return super.canAttack(targetEntity);
    }

    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("hellfireball", target, range, 0, new Vec3(0, 0, 0), 1.2f, 8f, 1F);
        super.attackRanged(target, range);
    }

    public void hellfireWaveAttack(double angle) {
        this.triggerAttackCooldown();
        this.playAttackSound();
        EntityHellfireWave hellfireWave = new EntityHellfireWave(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireWave.class), this.getCommandSenderWorld(), this);
        hellfireWave.setPos(
                hellfireWave.position().x(),
                this.position().y(),
                hellfireWave.position().z()
        );
        hellfireWave.rotation = angle;
        this.getCommandSenderWorld().addFreshEntity(hellfireWave);
    }

    public void hellfireWallAttack(double angle) {
        this.playAttackSound();
        this.triggerAttackCooldown();

        this.hellfireWallTime = this.hellfireWallTimeMax;
        this.hellfireWallClockwise = this.getRandom().nextBoolean();
    }

    public void hellfireWallUpdate() {
        this.triggerAttackCooldown();

        double hellfireWallNormal = (double)this.hellfireWallTime / this.hellfireWallTimeMax;
        double hellfireWallAngle = 360;
        if(this.hellfireWallClockwise)
            hellfireWallAngle = -360;

        if(this.hellfireWallLeft == null) {
            this.hellfireWallLeft = new EntityHellfireBarrier(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireBarrier.class), this.getCommandSenderWorld(), this);
            this.hellfireWallLeft.wall = true;
            this.getCommandSenderWorld().addFreshEntity(this.hellfireWallLeft);
        }
        this.hellfireWallLeft.time = 0;
        this.hellfireWallLeft.setPos(
                this.position().x(),
                this.position().y(),
                this.position().z()
        );
        this.hellfireWallLeft.rotation = hellfireWallNormal * hellfireWallAngle;

        if(this.hellfireWallRight == null) {
            this.hellfireWallRight = new EntityHellfireBarrier(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireBarrier.class), this.getCommandSenderWorld(), this);
            this.hellfireWallRight.wall = true;
            this.getCommandSenderWorld().addFreshEntity(this.hellfireWallRight);
        }
        this.hellfireWallRight.time = 0;
        this.hellfireWallRight.setPos(
                this.position().x(),
                this.position().y(),
                this.position().z()
        );
        this.hellfireWallRight.rotation = 180 + (hellfireWallNormal * hellfireWallAngle);
    }

    public void hellfireWallCleanup() {
        if(this.hellfireWallLeft != null) {
            this.hellfireWallLeft.discard();
            this.hellfireWallLeft = null;
        }
        if(this.hellfireWallRight != null) {
            this.hellfireWallRight.discard();
            this.hellfireWallRight = null;
        }
    }

    public void hellfireBarrierAttack(double angle) {
    	if(this.hellfireBarriers.size() >= 10) {
    		return;
		}
        this.triggerAttackCooldown();
        this.playAttackSound();

        EntityHellfireBarrier hellfireBarrier = new EntityHellfireBarrier(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireBarrier.class), this.getCommandSenderWorld(), this);
        this.getCommandSenderWorld().addFreshEntity(hellfireBarrier);
        hellfireBarrier.time = 0;
        hellfireBarrier.setPos(
                this.position().x(),
                this.position().y(),
                this.position().z()
        );
        hellfireBarrier.rotation = angle;
        this.hellfireBarriers.add(hellfireBarrier);
    }

    public void hellfireBarrierUpdate() {
        if(this.hellfireBarrierHealth <= 0) {
            this.hellfireBarrierHealth = 100;
            if(this.hellfireBarriers.size() > 0) {
                EntityHellfireBarrier hellfireBarrier = this.hellfireBarriers.get(this.hellfireBarriers.size() - 1);
                hellfireBarrier.discard();
                this.hellfireBarriers.remove(this.hellfireBarriers.size() - 1);
            }
        }
        for(EntityHellfireBarrier hellfireBarrier : this.hellfireBarriers) {
            hellfireBarrier.time = 0;
            hellfireBarrier.setPos(
                    this.position().x(),
                    this.position().y(),
                    this.position().z()
            );
        }
    }

    public void hellfireBarrierCleanup() {
        if(this.getCommandSenderWorld().isClientSide || this.hellfireBarriers.size() < 1)
            return;
        for(EntityHellfireBarrier hellfireBarrier : this.hellfireBarriers) {
            hellfireBarrier.discard();
        }
        this.hellfireBarriers = new ArrayList<>();
        this.hellfireBarrierHealth = 100;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance potionEffect) {
        if(potionEffect.getEffect() == MobEffects.WITHER)
            return false;
        if(ObjectManager.getEffect("decay") != null)
            if(potionEffect.getEffect() == ObjectManager.getEffect("decay")) return false;
        super.canBeAffected(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }

    @Override
    public boolean isVulnerableTo(Entity entity) {
        if(entity instanceof ZombifiedPiglin) {
            entity.discard();
            return false;
        }
        if(entity instanceof IronGolem) {
            entity.discard();
            return false;
        }
        if(entity instanceof Player) {
            Player player = (Player)entity;
            if (!player.getAbilities().invulnerable && player.position().y() > this.position().y() + CreatureManager.getInstance().config.bossAntiFlight) {
                return false;
            }
        }
        return super.isVulnerableTo(entity);
    }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if(nbt.contains("HellfireEnergy")) {
            this.hellfireEnergy = nbt.getInt("HellfireEnergy");
        }
        if(nbt.contains("HellfireWallTime")) {
            this.hellfireWallTime = nbt.getInt("HellfireWallTime");
        }
        if(nbt.contains("BelphegorIDs")) {
            ListTag belphegorIDs = nbt.getList("BelphegorIDs", 10);
            for(int i = 0; i < belphegorIDs.size(); i++) {
                CompoundTag belphegorID = belphegorIDs.getCompound(i);
                if(belphegorID.contains("ID")) {
                    Entity entity = this.getCommandSenderWorld().getEntity(belphegorID.getInt("ID"));
                    if(entity != null && entity instanceof Belphegor)
                        this.hellfireBelphegorMinions.add((Belphegor)entity);
                }
            }
        }
        if(nbt.contains("BehemophetIDs")) {
            ListTag behemophetIDs = nbt.getList("BehemophetIDs", 10);
            for(int i = 0; i < behemophetIDs.size(); i++) {
                CompoundTag behemophetID = behemophetIDs.getCompound(i);
                if(behemophetID.contains("ID")) {
                    Entity entity = this.getCommandSenderWorld().getEntity(behemophetID.getInt("ID"));
                    if(entity != null && entity instanceof Behemophet)
                        this.hellfireBehemophetMinions.add((Behemophet)entity);
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("HellfireEnergy", this.hellfireEnergy);
        nbt.putInt("HellfireWallTime", this.hellfireWallTime);
        if(this.getBattlePhase() == 0) {
            ListTag belphegorIDs = new ListTag();
            for(Belphegor entityBelphegoregor : this.hellfireBelphegorMinions) {
                CompoundTag belphegorID = new CompoundTag();
                belphegorID.putInt("ID", entityBelphegoregor.getId());
                belphegorIDs.add(belphegorID);
            }
            nbt.put("BelphegorIDs", belphegorIDs);
        }
        if(this.getBattlePhase() == 1) {
            ListTag behemophetIDs = new ListTag();
            for(Behemophet entityBehemophet : this.hellfireBehemophetMinions) {
                CompoundTag behemophetID = new CompoundTag();
                behemophetID.putInt("ID", entityBehemophet.getId());
                behemophetIDs.add(behemophetID);
            }
            nbt.put("BehemophetIDs", behemophetIDs);
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        if(this.hasArenaCenter())
            return;
        super.playStepSound(pos, block);
    }

    public float getBrightness() {
        return 1.0F;
    }

    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
}
