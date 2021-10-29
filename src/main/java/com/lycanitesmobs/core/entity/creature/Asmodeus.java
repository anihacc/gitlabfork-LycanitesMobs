package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.goals.GoalConditions;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.FindNearbyPlayersGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FaceTargetGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.HealWhenNoPlayersGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.SummonMinionsGoal;
import com.lycanitesmobs.core.entity.navigate.ArenaNode;
import com.lycanitesmobs.core.entity.navigate.ArenaNodeNetwork;
import com.lycanitesmobs.core.entity.navigate.ArenaNodeNetworkGrid;
import com.lycanitesmobs.core.entity.projectile.EntityDevilGatling;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
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

public class Asmodeus extends BaseCreatureEntity implements Enemy, IGroupHeavy, IGroupBoss {

    protected static final EntityDataAccessor<Byte> ANIMATION_STATES = SynchedEntityData.defineId(Asmodeus.class, EntityDataSerializers.BYTE);
    public enum ANIMATION_STATES_ID {
        SNAP_TO_ARENA((byte)1), COOLDOWN((byte)2);
        public final byte id;
        ANIMATION_STATES_ID(byte value) { this.id = value; }
        public byte getValue() { return id; }
    }

    public AttackRangedGoal aiRangedAttack;

    public boolean firstPlayerTargetCheck = false;
    public List<Astaroth> astarothMinions = new ArrayList<>();
    public List<Grell> grellMinions = new ArrayList<>();

    public int devilstarStreamTime = 0;
    public int devilstarStreamTimeMax = 5 * 20;
    public int devilstarStreamCharge = 20 * 20;
    public int devilstarStreamChargeMax = 20 * 20;

    public int hellshieldAstarothRespawnTime = 0;
    public int hellshieldAstarothRespawnTimeMax = 30;

    public int rebuildAstarothRespawnTime = 0;
    public int rebuildAstarothRespawnTimeMax = 40;

    public ArenaNodeNetwork arenaNodeNetwork;
    public ArenaNode currentArenaNode;
    public int arenaNodeChangeCooldown = 0;
    public int arenaNodeChangeCooldownMax = 200;
    public int arenaJumpingTime = 0;
    public int arenaJumpingTimeMax = 60;
    protected double jumpHeight = 6D;

    public Asmodeus(EntityType<? extends Asmodeus> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.setAttackCooldownMax(30);
        this.hasJumpSound = true;
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
        this.goalSelector.addGoal(this.nextIdleGoalIndex, new SummonMinionsGoal(this).setMinionInfo("grigori").setAntiFlight(true));

        this.aiRangedAttack = new AttackRangedGoal(this).setSpeed(1.0D).setStaminaTime(200).setStaminaDrainRate(3).setRange(90.0F).setChaseTime(0).setCheckSight(false);
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, this.aiRangedAttack);

        this.goalSelector.addGoal(this.nextIdleGoalIndex, new SummonMinionsGoal(this).setMinionInfo("trite").setSummonRate(20 * 3).setSummonCap(3).setPerPlayer(true)
                .setConditions(new GoalConditions().setBattlePhase(0)));

        super.registerGoals();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION_STATES, (byte) 0);
    }

    @OnlyIn(Dist.CLIENT)
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(200, 50, 200).move(0, -25, 0);
    }

    @Override
    public boolean isPersistant() {
        if(this.getMasterTarget() != null && this.getMasterTarget() instanceof Asmodeus)
            return true;
        return super.isPersistant();
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
        this.arenaNodeNetwork = new ArenaNodeNetworkGrid(this.getCommandSenderWorld(), pos, 3, 1, 3, 60);
        this.currentArenaNode = this.arenaNodeNetwork.getClosestNode(this.blockPosition());
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if(!this.getCommandSenderWorld().isClientSide) {
            this.updatePhases();
            this.updateCurrentArenaNode();
            this.updateArenaMovement();
        }

        if(this.getCommandSenderWorld().isClientSide && this.updateTick % 200 == 0) {
            if(!this.astarothMinions.isEmpty()) {
                for (Astaroth minion : this.astarothMinions.toArray(new Astaroth[this.astarothMinions.size()])) {
                    if(minion == null || !minion.isAlive() || minion.getMasterTarget() != this)
                        this.astarothMinions.remove(minion);
                }
            }
            if(!this.grellMinions.isEmpty()) {
                for (Grell minion : this.grellMinions.toArray(new Grell[this.grellMinions.size()])) {
                    if(minion == null || !minion.isAlive() || minion.getMasterTarget() != this)
                        this.grellMinions.remove(minion);
                }
            }
        }

        if(!this.getCommandSenderWorld().isClientSide) {
            byte animationState = 0;
            if(this.aiRangedAttack != null && this.aiRangedAttack.attackOnCooldown)
                animationState += ANIMATION_STATES_ID.COOLDOWN.id;
            this.entityData.set(ANIMATION_STATES, animationState);
        }

        if(this.getCommandSenderWorld().isClientSide) {
            if ((this.entityData.get(ANIMATION_STATES) & ANIMATION_STATES_ID.COOLDOWN.id) > 0) {
                BlockPos particlePos = this.getFacingPosition(this, 13, this.getYHeadRot() - this.getYRot());
                for (int i = 0; i < 4; ++i) {
                    this.getCommandSenderWorld().addParticle(ParticleTypes.LARGE_SMOKE,
                            particlePos.getX() + (this.random.nextDouble() - 0.5D) * 2,
                            particlePos.getY() + (this.getDimensions(Pose.STANDING).height * 0.2D) + this.random.nextDouble() * 2,
                            particlePos.getZ() + (this.random.nextDouble() - 0.5D) * 2,
                            0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public boolean rollWanderChance() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public void updateCurrentArenaNode() {
        if(!this.hasArenaCenter())
            return;
        if(this.arenaNodeChangeCooldown > 0) {
            this.arenaNodeChangeCooldown--;
            return;
        }

        if(this.getTarget() == null || !this.getTarget().isAlive()) {
            this.setCurrentArenaNode(this.arenaNodeNetwork.centralNode);
            return;
        }

        if(this.currentArenaNode != null)
            this.setCurrentArenaNode(this.currentArenaNode.getClosestAdjacentNode(this.getTarget().blockPosition()));
        else
            this.setCurrentArenaNode(this.arenaNodeNetwork.getClosestNode(this.getTarget().blockPosition()));
    }

    public void setCurrentArenaNode(ArenaNode arenaNode) {
        this.arenaNodeChangeCooldown = this.arenaNodeChangeCooldownMax;
        if(this.currentArenaNode == arenaNode)
            return;
        this.currentArenaNode = arenaNode;

        if(this.currentArenaNode != null && this.currentArenaNode.pos != null) {
            this.arenaJumpingTime = this.arenaJumpingTimeMax;
            this.leap(200, this.jumpHeight, this.currentArenaNode.pos);
            if(this.hasJumpSound)
                this.playJumpSound();
        }
    }

    public void updateArenaMovement() {
        this.noPhysics = false;
        if(this.currentArenaNode == null || this.currentArenaNode.pos == null) {
            return;
        }

        if(this.arenaJumpingTime > 0) {
            this.arenaJumpingTime--;
            if(this.updateTick % 4 == 0) {
                double dropForce = -0.5D;
                this.noPhysics = this.position().y() > this.currentArenaNode.pos.getY() + 8;
                if(this.position().y() < this.currentArenaNode.pos.getY()) {
                    this.setPos(this.position().x(), this.currentArenaNode.pos.getY(), this.position().z());
                    dropForce = 0;
                }
                this.leap(200, dropForce, this.currentArenaNode.pos);
            }
            if(this.arenaJumpingTime == 0)
                this.playStepSound(this.currentArenaNode.pos.below(), this.getCommandSenderWorld().getBlockState(this.currentArenaNode.pos.below()));
            return;
        }

        BlockPos arenaPos = this.currentArenaNode.pos;
        double arenaY = this.position().y();
        if (this.getCommandSenderWorld().isEmptyBlock(arenaPos))
            arenaY = arenaPos.getY();
        else if (this.getCommandSenderWorld().isEmptyBlock(arenaPos.offset(0, 1, 0)))
            arenaY = arenaPos.offset(0, 1, 0).getY();

        if(this.position().x() != arenaPos.getX() || this.position().y() != arenaY || this.position().z() != arenaPos.getZ())
            this.setPos(arenaPos.getX(), arenaY, arenaPos.getZ());
    }


    public void updatePhases() {
        int playerCount = Math.max(this.playerTargets.size(), 1);

        if(this.getBattlePhase() == 0) {
            if(this.devilstarStreamTime > 0) {
                this.devilstarStreamTime--;
                if(this.updateTick % 10 == 0) {
                    for (float angle = 0; angle < 360F; angle += 10F) {
                        this.attackDevilstar(angle);
                    }
                }
            }
            else if(this.devilstarStreamCharge > 0) {
                this.devilstarStreamCharge--;
            }
            else {
                this.devilstarStreamCharge = this.devilstarStreamChargeMax;
                this.devilstarStreamTime = this.devilstarStreamTimeMax;
            }
        }

        else if(this.getBattlePhase() == 1 && this.updateTick % 20 == 0) {
            if(this.astarothMinions.isEmpty() && this.hellshieldAstarothRespawnTime-- <= 0) {
                for (int i = 0; i < 2 * playerCount; i++) {
                    Astaroth minion = (Astaroth)CreatureManager.getInstance().getCreature("astaroth").createEntity(this.getCommandSenderWorld());
                    this.summonMinion(minion, this.getRandom().nextDouble() * 360, 0);
                    minion.setSizeScale(2.5D);
                    this.astarothMinions.add(minion);
                }
                this.hellshieldAstarothRespawnTime = this.hellshieldAstarothRespawnTimeMax;
            }
        }

        else if(this.updateTick % 20 == 0) {
            if(this.astarothMinions.size() < playerCount * 4) {
                if (this.rebuildAstarothRespawnTime-- <= 0) {
                    for (int i = 0; i < playerCount; i++) {
                        Astaroth minion = (Astaroth)CreatureManager.getInstance().getCreature("astaroth").createEntity(this.getCommandSenderWorld());
                        this.summonMinion(minion, this.getRandom().nextDouble() * 360, 0);
                        minion.setSizeScale(2.5D);
                        this.astarothMinions.add(minion);
                    }
                    this.rebuildAstarothRespawnTime = this.rebuildAstarothRespawnTimeMax;
                }
            }
            if(this.grellMinions.size() < playerCount * 6 && this.updateTick % 10 * 20 == 0) {
                for (int i = 0; i < 5 * playerCount; i++) {
                    Grell minion = (Grell)CreatureManager.getInstance().getCreature("grell").createEntity(this.getCommandSenderWorld());
                    this.summonMinion(minion, this.getRandom().nextDouble() * 360, 10);
                    minion.setPos(
                            minion.position().x(),
                            minion.position().y() + 10 + this.getRandom().nextInt(20),
                            minion.position().z()
                    );
                    this.grellMinions.add(minion);
                }
            }

            if(!this.astarothMinions.isEmpty()) {
                float healAmount = this.astarothMinions.size();
                if (((this.getHealth() + healAmount) / this.getMaxHealth()) <= 0.2D)
                    this.heal(healAmount * 2);
            }
        }
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

    @Override
    public boolean canAttack(LivingEntity target) {
    	if(target instanceof Trite || target instanceof Grell ||  target instanceof Astaroth)
    		return false;
        return super.canAttack(target);
    }

    @Override
    public void attackRanged(Entity target, float range) {
        for (int i = 0; i < 5; i++) {
            this.fireProjectile(EntityDevilGatling.class, target, range, 0, new Vec3(0, -24, 0), 4f, 2f, 8F);
        }
        this.attackHitscan(target, target instanceof Player ? 1 : 10);
    }

    public void attackDevilstar(float angle) {
        ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("devilstar");
        if(projectileInfo == null) {
            return;
        }
        BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getCommandSenderWorld(), this);

        BlockPos offset = this.getFacingPosition(this, 8, angle);
        projectile.setPos(
                offset.getX(),
                offset.getY() + (this.getDimensions(Pose.STANDING).height * 0.5D),
                offset.getZ()
        );

        float range = 20 + (20 * this.getRandom().nextFloat());
        BlockPos target = this.getFacingPosition(this, range, angle);
        double d0 = target.getX() - projectile.position().x();
        double d1 = target.getY() - projectile.position().y();
        double d2 = target.getZ() - projectile.position().z();
        float f1 = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.1F;
        float velocity = 1.2F;
        projectile.shoot(d0, d1 + (double) f1, d2, velocity, 0.0F);
        projectile.setProjectileScale(3f);

        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.getCommandSenderWorld().addFreshEntity(projectile);
    }

	@Override
	public void die(DamageSource damageSource) {
        if(!this.getCommandSenderWorld().isClientSide && CreatureManager.getInstance().getCreature("trite").enabled) {
            int j = 6 + this.random.nextInt(20) + (getCommandSenderWorld().getDifficulty().getId() * 4);
            for(int k = 0; k < j; ++k) {
                float f = ((float)(k % 2) - 0.5F) * this.getDimensions(Pose.STANDING).width / 4.0F;
                float f1 = ((float)(k / 2) - 0.5F) * this.getDimensions(Pose.STANDING).width / 4.0F;
                Trite trite = (Trite)CreatureManager.getInstance().getCreature("trite").createEntity(this.getCommandSenderWorld());
                trite.moveTo(this.position().x() + (double)f, this.position().y() + 0.5D, this.position().z() + (double)f1, this.random.nextFloat() * 360.0F, 0.0F);
                trite.setMinion(true);
                trite.applyVariant(this.getVariantIndex());
                this.getCommandSenderWorld().addFreshEntity(trite);
                if(this.getTarget() != null)
                	trite.setLastHurtByMob(this.getTarget());
            }
        }
        super.die(damageSource);
    }

    @Override
    public void onMinionDeath(LivingEntity minion, DamageSource damageSource) {
        if(minion instanceof Astaroth && this.astarothMinions.contains(minion)) {
            this.astarothMinions.remove(minion);
        }
        if(minion instanceof Grell && this.grellMinions.contains(minion)) {
            this.grellMinions.remove(minion);
        }
        super.onMinionDeath(minion, damageSource);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if(this.isBlocking())
            return true;
        return super.isInvulnerableTo(source);
    }
    
    @Override
    public boolean canBurn() { return false; }

    @Override
    public boolean isBlocking() {
        if(this.getCommandSenderWorld().isClientSide)
            return super.isBlocking();
        return this.getBattlePhase() == 1 && !this.astarothMinions.isEmpty();
    }

    public boolean canAttackWhileBlocking() {
        return true;
    }

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
        if(nbt.contains("DevilstarStreamCharge")) {
            this.devilstarStreamCharge = nbt.getInt("DevilstarStreamCharge");
        }
        if(nbt.contains("DevilstarStreamTime")) {
            this.devilstarStreamTime = nbt.getInt("DevilstarStreamTime");
        }
        if(nbt.contains("AstarothIDs")) {
            ListTag astarothIDs = nbt.getList("AstarothIDs", 10);
            for(int i = 0; i < astarothIDs.size(); i++) {
                CompoundTag astarothID = astarothIDs.getCompound(i);
                if(astarothID.contains("ID")) {
                    Entity entity = this.getCommandSenderWorld().getEntity(astarothID.getInt("ID"));
                    if(entity != null && entity instanceof Astaroth)
                        this.astarothMinions.add((Astaroth)entity);
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("DevilstarStreamCharge", this.devilstarStreamCharge);
        nbt.putInt("DevilstarStreamTime", this.devilstarStreamTime);
        if(this.getBattlePhase() > 0) {
            ListTag astarothIDs = new ListTag();
            for(Astaroth entityAstaroth : this.astarothMinions) {
                CompoundTag astarothID = new CompoundTag();
                astarothID.putInt("ID", entityAstaroth.getId());
                astarothIDs.add(astarothID);
            }
            nbt.put("AstarothIDs", astarothIDs);
        }
    }

    public float getBrightness() {
        return 1.0F;
    }

    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
}
