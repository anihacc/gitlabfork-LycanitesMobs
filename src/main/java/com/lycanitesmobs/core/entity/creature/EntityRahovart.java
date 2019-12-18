package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrier;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireWave;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireball;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.block.BlockState;
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
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class EntityRahovart extends BaseCreatureEntity implements IMob, IGroupHeavy, IGroupBoss {

    public List<PlayerEntity> playerTargets = new ArrayList<>();
    public int hellfireEnergy = 0;
    public List<EntityHellfireOrb> hellfireOrbs = new ArrayList<>();

    // Data Manager:
    protected static final DataParameter<Integer> HELLFIRE_ENERGY = EntityDataManager.createKey(EntityRahovart.class, DataSerializers.VARINT);

    // First Phase:
    public List<EntityBelph> hellfireBelphMinions = new ArrayList<>();

    // Second Phase:
    public List<EntityBehemoth> hellfireBehemothMinions = new ArrayList<>();
    public int hellfireWallTime = 0;
    public int hellfireWallTimeMax = 20 * 20;
    public boolean hellfireWallClockwise = false;
    public EntityHellfireBarrier hellfireWallLeft;
    public EntityHellfireBarrier hellfireWallRight;

    // Third Phase:
    public List<EntityHellfireBarrier> hellfireBarriers = new ArrayList<>();
    public int hellfireBarrierHealth = 100;


    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityRahovart(EntityType<? extends EntityRahovart> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.hasAttackSound = false;
        this.setAttackCooldownMax(40);
        this.solidCollision = true;
        this.entityCollisionReduction = 1.0F;
        this.setupMob();
        this.hitAreaWidthScale = 2F;

        // Boss:
        this.damageMax = 25;
        this.damageLimit = 40;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    // ========== Init ==========
    /** Initiates the entity setting all the values to be watched by the data manager. **/
    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(EntityRahovart.HELLFIRE_ENERGY, this.hellfireEnergy);
    }

    // ========== Rendering Distance ==========
    /** Returns a larger bounding box for rendering this large entity. **/
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getBoundingBox().grow(200, 50, 200).offset(0, -25, 0);
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
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

        // Look At Target:
        if(this.hasAttackTarget() && !this.getEntityWorld().isRemote) {
            this.getLookController().setLookPositionWithEntity(this.getAttackTarget(), 30.0F, 30.0F);
        }

        // Arena Snapping:
        if(this.hasArenaCenter()) {
            BlockPos arenaPos = this.getArenaCenter();
            double arenaY = this.posY;
            if (this.getEntityWorld().isAirBlock(arenaPos))
                arenaY = arenaPos.getY();
            else if (this.getEntityWorld().isAirBlock(arenaPos.add(0, 1, 0)))
                arenaY = arenaPos.add(0, 1, 0).getY();

            if (this.posX != arenaPos.getX() || this.posY != arenaY || this.posZ != arenaPos.getZ())
                this.setPosition(arenaPos.getX(), arenaY, arenaPos.getZ());
        }

        // Sync Hellfire Energy:
        if(!this.getEntityWorld().isRemote)
            this.dataManager.set(HELLFIRE_ENERGY, this.hellfireEnergy);
        else
            this.hellfireEnergy = this.dataManager.get(HELLFIRE_ENERGY);

        // Hellfire Update:
        updateHellfireOrbs(this, this.updateTick, 5, this.hellfireEnergy, 10, this.hellfireOrbs);

        // Update Phases:
        if(!this.getEntityWorld().isRemote) {
			this.updatePhases();
		}

        // Player Targets and No Player Healing:
        if(!this.getEntityWorld().isRemote && this.updateTick % 200 == 0) {
            this.playerTargets = this.getNearbyEntities(PlayerEntity.class, null, 64);
        }
        if(!this.getEntityWorld().isRemote && this.updateTick % 20 == 0) {
            if (this.playerTargets.size() == 0)
                this.heal(50);
        }

        // Passive Attacks:
        if(!this.getEntityWorld().isRemote && this.updateTick % 20 == 0) {

            // Random Projectiles:
            for(int i = 0; i < 3; i++) {
                BaseProjectileEntity projectile = new EntityHellfireball(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireball.class), this.getEntityWorld(), this);
                projectile.setProjectileScale(8f);
                projectile.shoot((this.getRNG().nextFloat()) - 0.5F, this.getRNG().nextFloat(), (this.getRNG().nextFloat()) - 0.5F, 1.2F, 3.0F);
                this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
                this.getEntityWorld().addEntity(projectile);
            }

            // Flying Player Wraith Attack:
            for(PlayerEntity target : this.playerTargets) {
                if(target.abilities.disableDamage || target.isSpectator())
                    continue;
                this.attackRanged(target, 1F);
                if(CreatureManager.getInstance().config.bossAntiFlight > 0 && target.posY > this.posY + CreatureManager.getInstance().config.bossAntiFlight + 1) {
                    for(int i = 0; i < 3; i++) {
                        EntityWraith minion = (EntityWraith)CreatureManager.getInstance().getCreature("wraith").createEntity(this.getEntityWorld());
                        this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                        minion.setAttackTarget(target);
                        minion.setMasterTarget(null); // Clear master target so that these minions don't break phase 3 barriers.
                    }
                }
            }

            // Primary Target
            if(this.hasAttackTarget()) {
                this.attackRanged(this.getAttackTarget(), 1F);
            }
        }
    }

    @Override
    public boolean rollWanderChance() {
        return false;
    }

    // ========== Phases Update ==========
    public void updatePhases() {

        // ===== First Phase - Hellfire Wave =====
        if(this.getBattlePhase() == 0) {
            // Clean Up:
            if(!this.hellfireBehemothMinions.isEmpty()) {
                for (EntityBehemoth minion : this.hellfireBehemothMinions.toArray(new EntityBehemoth[this.hellfireBehemothMinions.size()])) {
                    minion.hellfireEnergy = 0;
                }
                this.hellfireBehemothMinions = new ArrayList<>();
            }
            this.hellfireWallTime = 0;
            this.hellfireBarrierCleanup();

            // Hellfire Minion Update - Every Second:
            if(this.updateTick % 20 == 0) {
                for (EntityBelph minion : this.hellfireBelphMinions.toArray(new EntityBelph[this.hellfireBelphMinions.size()])) {
                    if (!minion.isAlive()) {
                        this.onMinionDeath(minion, null);
                        continue;
                    }
                    minion.hellfireEnergy += 5; // Charged after 20 secs.
                    if (minion.hellfireEnergy >= 100) {
                        this.hellfireEnergy += 20;
                        this.onMinionDeath(minion, null);
                        this.getEntityWorld().createExplosion(minion, minion.posX, minion.posY, minion.posZ, 1, Explosion.Mode.NONE);
                        minion.hellfireEnergy = 0;
                        minion.remove();
                        continue;
                    }
                }
            }

            // Hellfire Charged:
            if(this.hellfireEnergy >= 100) {
                this.hellfireEnergy = 0;
                double angle = this.getRNG().nextFloat() * 360;
                if(this.hasAttackTarget()) {
                    double deltaX = this.getAttackTarget().posX - this.posX;
                    double deltaZ = this.getAttackTarget().posZ - this.posZ;
                    angle = Math.atan2(deltaZ, deltaX) * 180 / Math.PI;
                }
                this.hellfireWaveAttack(angle);
            }

            // Every 5 Secs:
            if(this.updateTick % 100 == 0) {
                int summonAmount = this.getRNG().nextInt(4); // 0-3 Hellfire Belphs
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                    for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                        EntityBelph minion = (EntityBelph)CreatureManager.getInstance().getCreature("belph").createEntity(this.getEntityWorld());
                        this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                        this.hellfireBelphMinions.add(minion);
                    }
            }
        }

        // ===== Second Phase - Hellfire Wall =====
        if(this.getBattlePhase() == 1) {
            // Clean Up:
            if(!this.hellfireBelphMinions.isEmpty()) {
                for (EntityBelph minion : this.hellfireBelphMinions.toArray(new EntityBelph[this.hellfireBelphMinions.size()])) {
                    minion.hellfireEnergy = 0;
                }
                this.hellfireBelphMinions = new ArrayList<>();
            }
            this.hellfireBarrierCleanup();

            // Hellfire Minion Update - Every Second:
            if(this.hellfireWallTime <= 0 && this.updateTick % 20 == 0) {
                for (EntityBehemoth minion : this.hellfireBehemothMinions.toArray(new EntityBehemoth[this.hellfireBehemothMinions.size()])) {
                    if (!minion.isAlive()) {
                        this.onMinionDeath(minion, null);
                        continue;
                    }
                    minion.hellfireEnergy += 5; // Charged after 20 secs.
                    if (minion.hellfireEnergy >= 100) {
                        this.hellfireEnergy += 20;
                        this.onMinionDeath(minion, null);
                        this.getEntityWorld().createExplosion(minion, minion.posX, minion.posY, minion.posZ, 1, Explosion.Mode.NONE);
                        minion.hellfireEnergy = 0;
                        minion.remove();
                        continue;
                    }
                }
            }

            // Hellfire Charged:
            if(this.hellfireEnergy >= 100) {
                this.hellfireEnergy = 0;
                this.hellfireWallAttack(this.rotationYaw);
            }

            // Hellfire Wall:
            if(this.hellfireWallTime > 0) {
                this.hellfireWallUpdate();
                this.hellfireWallTime--;
            }

            // Every 20 Secs:
            if(this.updateTick % 400 == 0) {
                int summonAmount = 2; // 2 Hellfire Behemoth
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                    for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                        EntityBehemoth minion = (EntityBehemoth)CreatureManager.getInstance().getCreature("behemoth").createEntity(this.getEntityWorld());
                        this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                        this.hellfireBehemothMinions.add(minion);
                    }
            }

            // Every 10 Secs:
            if(this.updateTick % 200 == 0) {
                int summonAmount = this.getRNG().nextInt(4) - 1; // 0-2 Belphs with 50% fail chance.
                for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                    EntityBelph minion = (EntityBelph)CreatureManager.getInstance().getCreature("belph").createEntity(this.getEntityWorld());
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                }
            }
        }

        // ===== Third Phase - Hellfire Barrier =====
        if(this.getBattlePhase() >= 2) {
            // Clean Up:
            if(!this.hellfireBelphMinions.isEmpty()) {
                for (EntityBelph minion : this.hellfireBelphMinions.toArray(new EntityBelph[this.hellfireBelphMinions.size()])) {
                    minion.hellfireEnergy = 0;
                }
                this.hellfireBelphMinions = new ArrayList<>();
            }
            if(!this.hellfireBehemothMinions.isEmpty()) {
                for (EntityBehemoth minion : this.hellfireBehemothMinions.toArray(new EntityBehemoth[this.hellfireBehemothMinions.size()])) {
                    minion.hellfireEnergy = 0;
                }
                this.hellfireBehemothMinions = new ArrayList<>();
            }
            this.hellfireWallTime = 0;

            // Hellfire Energy - Every Second:
            if(this.updateTick % 20 == 0) {
                if (this.hellfireEnergy < 100)
                    this.hellfireEnergy += 5;
            }

            // Hellfire Charged:
            if(this.hellfireEnergy >= 100 && this.hellfireBarriers.size() < 20) {
                this.hellfireEnergy = 0;
                this.hellfireBarrierAttack(360F * this.getRNG().nextFloat());
            }

            // Hellfire Barriers:
            if(this.hellfireBarriers.size() > 0)
                this.hellfireBarrierUpdate();

            // Every 10 Secs:
            if(this.updateTick % 200 == 0) {
                int summonAmount = this.getRNG().nextInt(2); // 0-1 Hellfire Behemoth
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                    for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                        EntityBehemoth minion = (EntityBehemoth)CreatureManager.getInstance().getCreature("behemoth").createEntity(this.getEntityWorld());
                        this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                        this.hellfireBehemothMinions.add(minion);
                    }
            }

            // Every 20 Secs:
            if(this.updateTick % 400 == 0) {
                int summonAmount = this.getRNG().nextInt(4); // 0-3 Belphs
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                    EntityBelph minion = (EntityBelph)CreatureManager.getInstance().getCreature("belph").createEntity(this.getEntityWorld());
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                }
                summonAmount = this.getRNG().nextInt(3); // 0-2 Wraiths
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                    EntityWraith minion = (EntityWraith)CreatureManager.getInstance().getCreature("wraith").createEntity(this.getEntityWorld());
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                }
            }
        }

        if(this.hellfireWallTime <= 0)
            this.hellfireWallCleanup();
    }

    // ========== Minion Death ==========
    @Override
    public void onMinionDeath(LivingEntity minion, DamageSource damageSource) {
        if(minion instanceof EntityBelph && this.hellfireBelphMinions.contains(minion)) {
            this.hellfireBelphMinions.remove(minion);
            return;
        }
        if(minion instanceof EntityBehemoth && this.hellfireBehemothMinions.contains(minion)) {
            this.hellfireBehemothMinions.remove(minion);
            return;
        }
        if(this.hellfireBarriers.size() > 0) {
            if(minion instanceof EntityBehemoth)
                this.hellfireBarrierHealth -= 100;
            else
                this.hellfireBarrierHealth -= 50;
        }
        super.onMinionDeath(minion, damageSource);
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
    //                     Hellfire
    // ==================================================
    public static void updateHellfireOrbs(LivingEntity entity, long orbTick, int hellfireOrbMax, int hellfireOrbEnergy, float orbSize, List<EntityHellfireOrb> hellfireOrbs) {
        if(!entity.getEntityWorld().isRemote)
            return;

        int hellfireChargeCount = Math.round((float)Math.min(hellfireOrbEnergy, 100) / (100F / hellfireOrbMax));
        int hellfireOrbRotationTime = 5 * 20;
        double hellfireOrbAngle = 360 * ((float)(orbTick % hellfireOrbRotationTime) / hellfireOrbRotationTime);
        double hellfireOrbAngleOffset = 360.0D / hellfireOrbMax;

        // Add Required Orbs:
        while(hellfireOrbs.size() < hellfireChargeCount) {
            EntityHellfireOrb hellfireOrb = new EntityHellfireOrb(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireOrb.class), entity.getEntityWorld(), entity);
            hellfireOrb.clientOnly = true;
            hellfireOrbs.add(hellfireOrb);
            entity.getEntityWorld().addEntity(hellfireOrb);
            hellfireOrb.setProjectileScale(orbSize);
        }

        // Remove Excess Orbs:
        while(hellfireOrbs.size() > hellfireChargeCount) {
            hellfireOrbs.get(hellfireOrbs.size() - 1).remove();
            hellfireOrbs.remove(hellfireOrbs.size() - 1);
        }

        // Update Orbs:
        for(int i = 0; i < hellfireOrbs.size(); i++) {
            EntityHellfireOrb hellfireOrb = hellfireOrbs.get(i);
            double rotationRadians = Math.toRadians((hellfireOrbAngle + (hellfireOrbAngleOffset * i)) % 360);
            double x = (entity.getSize(Pose.STANDING).width * 1.25D) * Math.cos(rotationRadians) + Math.sin(rotationRadians);
            double z = (entity.getSize(Pose.STANDING).width * 1.25D) * Math.sin(rotationRadians) - Math.cos(rotationRadians);
            hellfireOrb.posX = entity.posX - x;
            hellfireOrb.posY = entity.posY + (entity.getSize(Pose.STANDING).height * 0.75F);
            hellfireOrb.posZ = entity.posZ - z;
            hellfireOrb.setPosition(entity.posX - x, entity.posY + (entity.getSize(Pose.STANDING).height * 0.75F), entity.posZ - z);
            hellfireOrb.projectileLife = 5;
        }
    }
	
	
	// ==================================================
    //                      Attacks
    // ==================================================
    public boolean canAttack(LivingEntity targetEntity) {
        if(targetEntity instanceof EntityBelph || targetEntity instanceof EntityBehemoth || targetEntity instanceof EntityWraith) {
            if(targetEntity instanceof TameableCreatureEntity)
                return ((TameableCreatureEntity)targetEntity).getOwner() instanceof PlayerEntity;
            else
                return false;
        }
        return super.canAttack(targetEntity);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile(EntityHellfireball.class, target, range, 0, new Vec3d(0, 0, 0), 1.2f, 8f, 1F);
        super.attackRanged(target, range);
    }

    // ========== Hellfire Wave ==========
    public void hellfireWaveAttack(double angle) {
        this.triggerAttackCooldown();
        this.playAttackSound();
        EntityHellfireWave hellfireWave = new EntityHellfireWave(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireWave.class), this.getEntityWorld(), this);
        hellfireWave.posY = this.posY;
        hellfireWave.rotation = angle;
        this.getEntityWorld().addEntity(hellfireWave);
    }

    // ========== Hellfire Wall ==========
    public void hellfireWallAttack(double angle) {
        this.playAttackSound();
        this.triggerAttackCooldown();

        this.hellfireWallTime = this.hellfireWallTimeMax;
        this.hellfireWallClockwise = this.getRNG().nextBoolean();
    }

    public void hellfireWallUpdate() {
        this.triggerAttackCooldown();

        double hellfireWallNormal = (double)this.hellfireWallTime / this.hellfireWallTimeMax;
        double hellfireWallAngle = 360;
        if(this.hellfireWallClockwise)
            hellfireWallAngle = -360;

        // Left (Positive) Wall:
        if(this.hellfireWallLeft == null) {
            this.hellfireWallLeft = new EntityHellfireBarrier(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireBarrier.class), this.getEntityWorld(), this);
            this.hellfireWallLeft.wall = true;
            this.getEntityWorld().addEntity(this.hellfireWallLeft);
        }
        this.hellfireWallLeft.time = 0;
        this.hellfireWallLeft.posX = this.posX;
        this.hellfireWallLeft.posY = this.posY;
        this.hellfireWallLeft.posZ = this.posZ;
        this.hellfireWallLeft.rotation = hellfireWallNormal * hellfireWallAngle;

        // Right (Negative) Wall:
        if(this.hellfireWallRight == null) {
            this.hellfireWallRight = new EntityHellfireBarrier(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireBarrier.class), this.getEntityWorld(), this);
            this.hellfireWallRight.wall = true;
            this.getEntityWorld().addEntity(this.hellfireWallRight);
        }
        this.hellfireWallRight.time = 0;
        this.hellfireWallRight.posX = this.posX;
        this.hellfireWallRight.posY = this.posY;
        this.hellfireWallRight.posZ = this.posZ;
        this.hellfireWallRight.rotation = 180 + (hellfireWallNormal * hellfireWallAngle);
    }

    public void hellfireWallCleanup() {
        if(this.hellfireWallLeft != null) {
            this.hellfireWallLeft.remove();
            this.hellfireWallLeft = null;
        }
        if(this.hellfireWallRight != null) {
            this.hellfireWallRight.remove();
            this.hellfireWallRight = null;
        }
    }

    // ========== Hellfire Barrier ==========
    public void hellfireBarrierAttack(double angle) {
    	if(this.hellfireBarriers.size() >= 10) {
    		return;
		}
        this.triggerAttackCooldown();
        this.playAttackSound();

        EntityHellfireBarrier hellfireBarrier = new EntityHellfireBarrier(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireBarrier.class), this.getEntityWorld(), this);
        this.getEntityWorld().addEntity(hellfireBarrier);
        hellfireBarrier.time = 0;
        hellfireBarrier.posX = this.posX;
        hellfireBarrier.posY = this.posY;
        hellfireBarrier.posZ = this.posZ;
        hellfireBarrier.rotation = angle;
        this.hellfireBarriers.add(hellfireBarrier);
    }

    public void hellfireBarrierUpdate() {
        if(this.hellfireBarrierHealth <= 0) {
            this.hellfireBarrierHealth = 100;
            if(this.hellfireBarriers.size() > 0) {
                EntityHellfireBarrier hellfireBarrier = this.hellfireBarriers.get(this.hellfireBarriers.size() - 1);
                hellfireBarrier.remove();
                this.hellfireBarriers.remove(this.hellfireBarriers.size() - 1);
            }
        }
        for(EntityHellfireBarrier hellfireBarrier : this.hellfireBarriers) {
            hellfireBarrier.time = 0;
            hellfireBarrier.posX = this.posX;
            hellfireBarrier.posY = this.posY;
            hellfireBarrier.posZ = this.posZ;
        }
    }

    public void hellfireBarrierCleanup() {
        if(this.getEntityWorld().isRemote || this.hellfireBarriers.size() < 1)
            return;
        for(EntityHellfireBarrier hellfireBarrier : this.hellfireBarriers) {
            hellfireBarrier.remove();
        }
        this.hellfireBarriers = new ArrayList<EntityHellfireBarrier>();
        this.hellfireBarrierHealth = 100;
    }


    // ==================================================
    //                     Movement
    // ==================================================
    // ========== Can Be Pushed ==========
    @Override
    public boolean canBePushed() {
        return false;
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isPotionApplicable(EffectInstance potionEffect) {
        if(potionEffect.getPotion() == Effects.WITHER)
            return false;
        if(ObjectManager.getEffect("decay") != null)
            if(potionEffect.getPotion() == ObjectManager.getEffect("decay")) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }

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
            if (!player.abilities.disableDamage && player.posY > this.posY + CreatureManager.getInstance().config.bossAntiFlight) {
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
        if(nbtTagCompound.contains("HellfireEnergy")) {
            this.hellfireEnergy = nbtTagCompound.getInt("HellfireEnergy");
        }
        if(nbtTagCompound.contains("HellfireWallTime")) {
            this.hellfireWallTime = nbtTagCompound.getInt("HellfireWallTime");
        }
        if(nbtTagCompound.contains("BelphIDs")) {
            ListNBT belphIDs = nbtTagCompound.getList("BelphIDs", 10);
            for(int i = 0; i < belphIDs.size(); i++) {
                CompoundNBT belphID = belphIDs.getCompound(i);
                if(belphID.contains("ID")) {
                    Entity entity = this.getEntityWorld().getEntityByID(belphID.getInt("ID"));
                    if(entity != null && entity instanceof EntityBelph)
                        this.hellfireBelphMinions.add((EntityBelph)entity);
                }
            }
        }
        if(nbtTagCompound.contains("BehemothIDs")) {
            ListNBT behemothIDs = nbtTagCompound.getList("BehemothIDs", 10);
            for(int i = 0; i < behemothIDs.size(); i++) {
                CompoundNBT behemothID = behemothIDs.getCompound(i);
                if(behemothID.contains("ID")) {
                    Entity entity = this.getEntityWorld().getEntityByID(behemothID.getInt("ID"));
                    if(entity != null && entity instanceof EntityBehemoth)
                        this.hellfireBehemothMinions.add((EntityBehemoth)entity);
                }
            }
        }
    }

    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeAdditional(CompoundNBT nbtTagCompound) {
        super.writeAdditional(nbtTagCompound);
        nbtTagCompound.putInt("HellfireEnergy", this.hellfireEnergy);
        nbtTagCompound.putInt("HellfireWallTime", this.hellfireWallTime);
        if(this.getBattlePhase() == 0) {
            ListNBT belphIDs = new ListNBT();
            for(EntityBelph entityBelph : this.hellfireBelphMinions) {
                CompoundNBT belphID = new CompoundNBT();
                belphID.putInt("ID", entityBelph.getEntityId());
                belphIDs.add(belphID);
            }
            nbtTagCompound.put("BelphIDs", belphIDs);
        }
        if(this.getBattlePhase() == 1) {
            ListNBT behemothIDs = new ListNBT();
            for(EntityBehemoth entityBehemoth : this.hellfireBehemothMinions) {
                CompoundNBT behemothID = new CompoundNBT();
                behemothID.putInt("ID", entityBehemoth.getEntityId());
                behemothIDs.add(behemothID);
            }
            nbtTagCompound.put("BehemothIDs", behemothIDs);
        }
    }


    // ==================================================
    //                       Sounds
    // ==================================================
    // ========== Step ==========
    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        if(this.hasArenaCenter())
            return;
        super.playStepSound(pos, block);
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
