package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.SummonMinionsGoal;
import com.lycanitesmobs.core.entity.navigate.ArenaNode;
import com.lycanitesmobs.core.entity.navigate.ArenaNodeNetwork;
import com.lycanitesmobs.core.entity.navigate.ArenaNodeNetworkGrid;
import com.lycanitesmobs.core.entity.projectile.EntityDevilGatling;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class EntityAsmodeus extends BaseCreatureEntity implements IMob, IGroupHeavy, IGroupBoss {

    // Data Manager:
    protected static final DataParameter<Byte> ANIMATION_STATES = EntityDataManager.createKey(EntityAsmodeus.class, DataSerializers.BYTE);
    public enum ANIMATION_STATES_ID {
        SNAP_TO_ARENA((byte)1), COOLDOWN((byte)2);
        public final byte id;
        ANIMATION_STATES_ID(byte value) { this.id = value; }
        public byte getValue() { return id; }
    }

    // AI:
    public AttackRangedGoal aiRangedAttack;

    public List<PlayerEntity> playerTargets = new ArrayList<>();
    public boolean firstPlayerTargetCheck = false;
    public List<EntityAstaroth> astarothMinions = new ArrayList<>();
    public List<EntityCacodemon> cacodemonMinions = new ArrayList<>();

    // First Phase:
    public int devilstarStreamTime = 0;
    public int devilstarStreamTimeMax = 5 * 20;
    public int devilstarStreamCharge = 20 * 20;
    public int devilstarStreamChargeMax = 20 * 20;

    // Second Phase:
    public int hellshieldAstarothRespawnTime = 0;
    public int hellshieldAstarothRespawnTimeMax = 30;

    // Third Phase:
    public int rebuildAstarothRespawnTime = 0;
    public int rebuildAstarothRespawnTimeMax = 40;

    // Arena Movement:
    public ArenaNodeNetwork arenaNodeNetwork;
    public ArenaNode currentArenaNode;
    public int arenaNodeChangeCooldown = 0;
    public int arenaNodeChangeCooldownMax = 200;
    public int arenaJumpingTime = 0;
    public int arenaJumpingTimeMax = 60;
    protected double jumpHeight = 6D;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAsmodeus(EntityType<? extends EntityAsmodeus> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;
        this.setAttackCooldownMax(30);
        this.hasJumpSound = true;
        this.solidCollision = true;
        this.entityCollisionReduction = 1.0F;
        this.setupMob();
        this.hitAreaWidthScale = 2F;

        // Boss:
        this.damageMax = BaseCreatureEntity.BOSS_DAMAGE_LIMIT;
        this.damageLimit = BaseCreatureEntity.BOSS_DAMAGE_LIMIT;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.aiRangedAttack = new AttackRangedGoal(this).setSpeed(1.0D).setStaminaTime(200).setStaminaDrainRate(3).setRange(90.0F).setChaseTime(0).setCheckSight(false);
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, this.aiRangedAttack);

        // Phase 1:
        this.goalSelector.addGoal(this.nextIdleGoalIndex, new SummonMinionsGoal(this).setMinionInfo("trite").setSummonRate(20 * 3).setSummonCap(3).setPerPlayer(true).setPhase(0));
    }

    // ========== Init ==========
    /** Initiates the entity setting all the values to be watched by the data manager. **/
    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(ANIMATION_STATES, (byte) 0);
    }

    // ========== Rendering Distance ==========
    /** Returns a larger bounding box for rendering this large entity. **/
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getBoundingBox().grow(200, 50, 200).offset(0, -25, 0);
    }

    // ========== Persistence ==========
    @Override
    public boolean isPersistant() {
        if(this.getMasterTarget() != null && this.getMasterTarget() instanceof EntityAsmodeus)
            return true;
        return super.isPersistant();
    }

    // ========== First Spawn ==========
    @Override
    public void onFirstSpawn() {
        super.onFirstSpawn();
        if(this.getArenaCenter() == null) {
            this.setArenaCenter(this.getPosition());
        }
    }


    // ==================================================
    //                      Positions
    // ==================================================
    // ========== Arena Center ==========
    /** Sets the central arena point for this mob to use. **/
    public void setArenaCenter(BlockPos pos) {
        super.setArenaCenter(pos);
        this.arenaNodeNetwork = new ArenaNodeNetworkGrid(this.getEntityWorld(), pos, 3, 1, 3, 60);
        this.currentArenaNode = this.arenaNodeNetwork.getClosestNode(this.getPosition());
    }


    // ==================================================
    //                      Updates
    // ==================================================
    // ========== Living Update ==========
    @Override
    public void livingTick() {
        super.livingTick();

        // Player Targets and No Player Healing:
        if(!this.getEntityWorld().isRemote) {
            if (this.updateTick % 200 == 0 || !this.firstPlayerTargetCheck) {
                this.firstPlayerTargetCheck = true;
                this.playerTargets = this.getNearbyEntities(PlayerEntity.class, null, 64);
            }
            if (this.updateTick % 20 == 0) {
                if (this.playerTargets.isEmpty()) {
                    this.heal(50);
                }
            }
        }

        // Update Phases:
        if(!this.getEntityWorld().isRemote) {
            this.updatePhases();
            this.updateCurrentArenaNode();
            this.updateArenaMovement();
        }

        // Passive Attacks:
        if(!this.getEntityWorld().isRemote && this.updateTick % 20 == 0) {
            // Flying Player Wraith Attack:
            for(PlayerEntity target : this.playerTargets) {
                if(target.abilities.disableDamage || target.isSpectator())
                    continue;
                if(CreatureManager.getInstance().config.bossAntiFlight > 0 && target.getPositionVec().getY() > this.getPositionVec().getY() + CreatureManager.getInstance().config.bossAntiFlight + 1) {
                    for(int i = 0; i < 3; i++) {
                        EntityWraith minion = (EntityWraith)CreatureManager.getInstance().getCreature("wraith").createEntity(this.getEntityWorld());
                        this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                        minion.setAttackTarget(target);
                    }
                }
            }
        }

        // Clean Minion Lists:
        if(this.getEntityWorld().isRemote && this.updateTick % 200 == 0) {
            if(!this.astarothMinions.isEmpty()) {
                for (EntityAstaroth minion : this.astarothMinions.toArray(new EntityAstaroth[this.astarothMinions.size()])) {
                    if(minion == null || !minion.isAlive() || minion.getMasterTarget() != this)
                        this.astarothMinions.remove(minion);
                }
            }
            if(!this.cacodemonMinions.isEmpty()) {
                for (EntityCacodemon minion : this.cacodemonMinions.toArray(new EntityCacodemon[this.cacodemonMinions.size()])) {
                    if(minion == null || !minion.isAlive() || minion.getMasterTarget() != this)
                        this.cacodemonMinions.remove(minion);
                }
            }
        }

        // Update Animation States:
        if(!this.getEntityWorld().isRemote) {
            byte animationState = 0;
            if(this.aiRangedAttack != null && this.aiRangedAttack.attackOnCooldown)
                animationState += ANIMATION_STATES_ID.COOLDOWN.id;
            this.dataManager.set(ANIMATION_STATES, animationState);
        }

        // Client Attack Cooldown Particles:
        if(this.getEntityWorld().isRemote && (this.dataManager.get(ANIMATION_STATES) & ANIMATION_STATES_ID.COOLDOWN.id) > 0) {
            BlockPos particlePos = this.getFacingPosition(this, 13, this.getRotationYawHead() - this.rotationYaw);
            for(int i = 0; i < 4; ++i) {
                this.getEntityWorld().addParticle(ParticleTypes.LARGE_SMOKE, particlePos.getX() + (this.rand.nextDouble() - 0.5D) * 2, particlePos.getY() + (this.getSize(Pose.STANDING).height * 0.2D) + this.rand.nextDouble() * 2, particlePos.getZ() + (this.rand.nextDouble() - 0.5D) * 2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public boolean rollWanderChance() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    // ========== Current Arena Node Update ==========
    public void updateCurrentArenaNode() {
        if(!this.hasArenaCenter())
            return;
        if(this.arenaNodeChangeCooldown > 0) {
            this.arenaNodeChangeCooldown--;
            return;
        }

        // Return to center with no target.
        if(this.getAttackTarget() == null || !this.getAttackTarget().isAlive()) {
            this.setCurrentArenaNode(this.arenaNodeNetwork.centralNode);
            return;
        }

        if(this.currentArenaNode != null)
            this.setCurrentArenaNode(this.currentArenaNode.getClosestAdjacentNode(this.getAttackTarget().getPosition()));
        else
            this.setCurrentArenaNode(this.arenaNodeNetwork.getClosestNode(this.getAttackTarget().getPosition()));
    }

    // ========== Set Current Arena Node ==========
    public void setCurrentArenaNode(ArenaNode arenaNode) {
        this.arenaNodeChangeCooldown = this.arenaNodeChangeCooldownMax;
        if(this.currentArenaNode == arenaNode)
            return;
        this.currentArenaNode = arenaNode;

        // Update home position jumping time on node change to new node.
        if(this.currentArenaNode != null && this.currentArenaNode.pos != null) {
            this.arenaJumpingTime = this.arenaJumpingTimeMax;
            this.leap(200, this.jumpHeight, this.currentArenaNode.pos); // First leap for jump height.
            if(this.hasJumpSound)
                this.playJumpSound();
        }
    }

    // ========== Arena Movement Update ==========
    public void updateArenaMovement() {
        this.noClip = false;
        if(this.currentArenaNode == null || this.currentArenaNode.pos == null) {
            return;
        }

        // Jumping:
        if(this.arenaJumpingTime > 0) {
            this.arenaJumpingTime--;
            if(this.updateTick % 4 == 0) {
                double dropForce = -0.5D;
                this.noClip = this.getPositionVec().getY() > this.currentArenaNode.pos.getY() + 8;
                if(this.getPositionVec().getY() < this.currentArenaNode.pos.getY()) {
                    this.setPosition(this.getPositionVec().getX(), this.currentArenaNode.pos.getY(), this.getPositionVec().getZ());
                    dropForce = 0;
                }
                this.leap(200, dropForce, this.currentArenaNode.pos); // Leap for XZ movement and negative height for increased weight on update.
            }
            if(this.arenaJumpingTime == 0)
                this.playStepSound(this.currentArenaNode.pos.down(), this.getEntityWorld().getBlockState(this.currentArenaNode.pos.down()));
            return;
        }

        // Snap To Node:
        BlockPos arenaPos = this.currentArenaNode.pos;
        double arenaY = this.getPositionVec().getY();
        if (this.getEntityWorld().isAirBlock(arenaPos))
            arenaY = arenaPos.getY();
        else if (this.getEntityWorld().isAirBlock(arenaPos.add(0, 1, 0)))
            arenaY = arenaPos.add(0, 1, 0).getY();

        if(this.getPositionVec().getX() != arenaPos.getX() || this.getPositionVec().getY() != arenaY || this.getPositionVec().getZ() != arenaPos.getZ())
            this.setPosition(arenaPos.getX(), arenaY, arenaPos.getZ());
    }


    // ========== Phases Update ==========
    public void updatePhases() {
        int playerCount = Math.max(this.playerTargets.size(), 1);

        // ===== First Phase - Devilstar Stream =====
        if(this.getBattlePhase() == 0) {
            // Devilstar Stream - Fire:
            if(this.devilstarStreamTime > 0) {
                this.devilstarStreamTime--;
                if(this.updateTick % 10 == 0) {
                    for (float angle = 0; angle < 360F; angle += 10F) {
                        this.attackDevilstar(angle);
                    }
                }
            }
            // Devilstar Stream - Recharge:
            else if(this.devilstarStreamCharge > 0) {
                this.devilstarStreamCharge--;
            }
            // Devilstar Stream - Charged
            else {
                this.devilstarStreamCharge = this.devilstarStreamChargeMax;
                this.devilstarStreamTime = this.devilstarStreamTimeMax;
            }
        }


        // ===== Second Phase - Hellshield =====
        else if(this.getBattlePhase() == 1 && this.updateTick % 20 == 0) {
            // Summon Astaroth:
            if(this.astarothMinions.isEmpty() && this.hellshieldAstarothRespawnTime-- <= 0) {
                for (int i = 0; i < 2 * playerCount; i++) {
                    EntityAstaroth minion = (EntityAstaroth)CreatureManager.getInstance().getCreature("astaroth").createEntity(this.getEntityWorld());
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 0);
                    minion.setSizeScale(2.5D);
                    this.astarothMinions.add(minion);
                }
                this.hellshieldAstarothRespawnTime = this.hellshieldAstarothRespawnTimeMax;
            }
        }


        // ===== Third Phase - Rebuild =====
        else if(this.updateTick % 20 == 0) {
            if(this.astarothMinions.size() < playerCount * 4) {
                // Summon Astaroth:
                if (this.rebuildAstarothRespawnTime-- <= 0) {
                    for (int i = 0; i < playerCount; i++) {
                        EntityAstaroth minion = (EntityAstaroth)CreatureManager.getInstance().getCreature("astaroth").createEntity(this.getEntityWorld());
                        this.summonMinion(minion, this.getRNG().nextDouble() * 360, 0);
                        minion.setSizeScale(2.5D);
                        this.astarothMinions.add(minion);
                    }
                    this.rebuildAstarothRespawnTime = this.rebuildAstarothRespawnTimeMax;
                }
            }

            // Summon Cacodemon:
            if(this.cacodemonMinions.size() < playerCount * 6 && this.updateTick % 10 * 20 == 0) {
                for (int i = 0; i < 5 * playerCount; i++) {
                    EntityCacodemon minion = (EntityCacodemon)CreatureManager.getInstance().getCreature("cacodemon").createEntity(this.getEntityWorld());
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 10);
                    minion.setPosition(
                            minion.getPositionVec().getX(),
                            minion.getPositionVec().getY() + 10 + this.getRNG().nextInt(20),
                            minion.getPositionVec().getZ()
                    );
                    this.cacodemonMinions.add(minion);
                }
            }

            // Heal:
            if(!this.astarothMinions.isEmpty()) {
                float healAmount = this.astarothMinions.size();
                if (((this.getHealth() + healAmount) / this.getMaxHealth()) <= 0.2D)
                    this.heal(healAmount * 2);
            }
        }
    }


    // ==================================================
    //                  Battle Phases
    // ==================================================
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
    
    
	// ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttack(LivingEntity target) {
    	if(target instanceof EntityTrite || target instanceof EntityCacodemon ||  target instanceof EntityAstaroth)
    		return false;
        return super.canAttack(target);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile(EntityDevilGatling.class, target, range, 0, new Vec3d(0, 8, 0), 1.2f, 6f, 0F);
        super.attackRanged(target, range);
    }

    // ========== Devilstars ==========
    public void attackDevilstar(float angle) {
        // Type:
        ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("devilstar");
        if(projectileInfo == null) {
            return;
        }
        BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getEntityWorld(), this);
        projectile.setProjectileScale(4f);

        // Y Offset:
        BlockPos offset = this.getFacingPosition(this, 8, angle);
        projectile.setPosition(
                offset.getX(),
                offset.getY() + (this.getSize(Pose.STANDING).height * 0.5D),
                offset.getZ()
        );

        // Set Velocities:
        float range = 20 + (20 * this.getRNG().nextFloat());
        BlockPos target = this.getFacingPosition(this, range, angle);
        double d0 = target.getX() - projectile.getPositionVec().getX();
        double d1 = target.getY() - projectile.getPositionVec().getY();
        double d2 = target.getZ() - projectile.getPositionVec().getZ();
        float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.1F;
        float velocity = 1.2F;
        projectile.shoot(d0, d1 + (double) f1, d2, velocity, 0.0F);

        // Launch:
        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.getEntityWorld().addEntity(projectile);
    }
	
	
	// ==================================================
   	//                      Death
   	// ==================================================
	@Override
	public void onDeath(DamageSource damageSource) {
        if(!this.getEntityWorld().isRemote && CreatureManager.getInstance().getCreature("trite").enabled) {
            int j = 6 + this.rand.nextInt(20) + (getEntityWorld().getDifficulty().getId() * 4);
            for(int k = 0; k < j; ++k) {
                float f = ((float)(k % 2) - 0.5F) * this.getSize(Pose.STANDING).width / 4.0F;
                float f1 = ((float)(k / 2) - 0.5F) * this.getSize(Pose.STANDING).width / 4.0F;
                EntityTrite trite = (EntityTrite)CreatureManager.getInstance().getCreature("trite").createEntity(this.getEntityWorld());
                trite.setLocationAndAngles(this.getPositionVec().getX() + (double)f, this.getPositionVec().getY() + 0.5D, this.getPositionVec().getZ() + (double)f1, this.rand.nextFloat() * 360.0F, 0.0F);
                trite.setMinion(true);
                trite.applySubspecies(this.getSubspeciesIndex());
                this.getEntityWorld().addEntity(trite);
                if(this.getAttackTarget() != null)
                	trite.setRevengeTarget(this.getAttackTarget());
            }
        }
        super.onDeath(damageSource);
    }

    // ========== Minion Death ==========
    @Override
    public void onMinionDeath(LivingEntity minion, DamageSource damageSource) {
        if(minion instanceof EntityAstaroth && this.astarothMinions.contains(minion)) {
            this.astarothMinions.remove(minion);
        }
        if(minion instanceof EntityCacodemon && this.cacodemonMinions.contains(minion)) {
            this.cacodemonMinions.remove(minion);
        }
        super.onMinionDeath(minion, damageSource);
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    // ========== Damage ==========
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if(this.isBlocking())
            return true;
        return super.isInvulnerableTo(source);
    }
    
    @Override
    public boolean canBurn() { return false; }

    // ========== Blocking ==========
    @Override
    public boolean isBlocking() {
        if(this.getEntityWorld().isRemote)
            return super.isBlocking();
        return this.getBattlePhase() == 1 && !this.astarothMinions.isEmpty();
    }

    public boolean canAttackWhileBlocking() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(Entity entity) {
        if(entity instanceof ZombiePigmanEntity) {
            entity.remove();
            return false;
        }
        if(entity instanceof IronGolemEntity) {
            entity.remove();
            return false;
        }
        if(entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            if (!player.abilities.disableDamage && player.getPositionVec().getY() > this.getPositionVec().getY() + CreatureManager.getInstance().config.bossAntiFlight) {
                return false;
            }
        }
        return super.isInvulnerableTo(entity);
    }


    // ==================================================
    //                    Taking Damage
    // ==================================================
    // ========== Attacked From ==========
    /** Called when this entity has been attacked, uses a DamageSource and damage value. **/
    @Override
    public boolean attackEntityFrom(DamageSource damageSrc, float damageAmount) {
        if(this.playerTargets != null && damageSrc.getTrueSource() != null && damageSrc.getTrueSource() instanceof PlayerEntity) {
            if (!this.playerTargets.contains(damageSrc.getTrueSource()))
                this.playerTargets.add((PlayerEntity)damageSrc.getTrueSource());
        }
        return super.attackEntityFrom(damageSrc, damageAmount);
    }


    // ==================================================
    //                       NBT
    // ==================================================
    // ========== Read ===========
    @Override
    public void readAdditional(CompoundNBT nbtTagCompound) {
        super.readAdditional(nbtTagCompound);
        if(nbtTagCompound.contains("DevilstarStreamCharge")) {
            this.devilstarStreamCharge = nbtTagCompound.getInt("DevilstarStreamCharge");
        }
        if(nbtTagCompound.contains("DevilstarStreamTime")) {
            this.devilstarStreamTime = nbtTagCompound.getInt("DevilstarStreamTime");
        }
        if(nbtTagCompound.contains("AstarothIDs")) {
            ListNBT astarothIDs = nbtTagCompound.getList("AstarothIDs", 10);
            for(int i = 0; i < astarothIDs.size(); i++) {
                CompoundNBT astarothID = astarothIDs.getCompound(i);
                if(astarothID.contains("ID")) {
                    Entity entity = this.getEntityWorld().getEntityByID(astarothID.getInt("ID"));
                    if(entity != null && entity instanceof EntityAstaroth)
                        this.astarothMinions.add((EntityAstaroth)entity);
                }
            }
        }
    }

    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeAdditional(CompoundNBT nbtTagCompound) {
        super.writeAdditional(nbtTagCompound);
        nbtTagCompound.putInt("DevilstarStreamCharge", this.devilstarStreamCharge);
        nbtTagCompound.putInt("DevilstarStreamTime", this.devilstarStreamTime);
        if(this.getBattlePhase() > 0) {
            ListNBT astarothIDs = new ListNBT();
            for(EntityAstaroth entityAstaroth : this.astarothMinions) {
                CompoundNBT astarothID = new CompoundNBT();
                astarothID.putInt("ID", entityAstaroth.getEntityId());
                astarothIDs.add(astarothID);
            }
            nbtTagCompound.put("AstarothIDs", astarothIDs);
        }
    }


    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness() {
        return 1.0F;
    }

    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
}
