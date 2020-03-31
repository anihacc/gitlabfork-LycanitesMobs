package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.FindNearbyPlayersGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.*;
import com.lycanitesmobs.core.entity.goals.targeting.CopyMasterAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityAmalgalich extends BaseCreatureEntity implements IMob, IGroupHeavy, IGroupBoss {
    private ForceGoal consumptionGoalP0;
    private ForceGoal consumptionGoalP2;
    private int consumptionDuration = 15 * 20;
    private int consumptionWindUp = 3 * 20;
    private int consumptionAnimationTime = 0;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAmalgalich(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.hasAttackSound = true;
        this.setAttackCooldownMax(30);
        this.hasJumpSound = false;
        this.entityCollisionReduction = 1.0F;
        this.setupMob();
        this.hitAreaWidthScale = 2F;

        // Boss:
        this.damageMax = BaseCreatureEntity.BOSS_DAMAGE_LIMIT;
        this.damageLimit = BaseCreatureEntity.BOSS_DAMAGE_LIMIT;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        this.targetTasks.addTask(this.nextFindTargetIndex, new FindNearbyPlayersGoal(this));

        this.consumptionDuration = 15 * 20;
        this.consumptionWindUp = 3 * 20;
        this.consumptionAnimationTime = 0;
        int consumptionCooldown = 20 * 20;

        // All Phases:
        this.tasks.addTask(this.nextIdleGoalIndex, new FaceTargetGoal(this));
        this.tasks.addTask(this.nextIdleGoalIndex, new HealWhenNoPlayersGoal(this));
        this.tasks.addTask(this.nextIdleGoalIndex, new SummonMinionsGoal(this).setMinionInfo("banshee").setAntiFlight(true));
        this.tasks.addTask(this.nextIdleGoalIndex, new FireProjectilesGoal(this).setProjectile("spectralbolt").setFireRate(40).setVelocity(1.0F).setScale(8F).setAllPlayers(true));
        this.tasks.addTask(this.nextIdleGoalIndex, new FireProjectilesGoal(this).setProjectile("spectralbolt").setFireRate(60).setVelocity(1.0F).setScale(8F));
        this.tasks.addTask(this.nextIdleGoalIndex, new EffectAuraGoal(this).setEffect("decay").setAmplifier(0).setEffectSeconds(5).setRange(52).setCheckSight(false));

        // Phase 1:
        this.tasks.addTask(this.nextIdleGoalIndex, new SummonMinionsGoal(this).setMinionInfo("reaper").setSummonRate(20 * 5).setSummonCap(5).setPhase(0).setPerPlayer(true));
        this.consumptionGoalP0 = new ForceGoal(this).setRange(64F).setCooldown(consumptionCooldown).setDuration(this.consumptionDuration).setWindUp(this.consumptionWindUp).setForce(-0.75F).setPhase(0).setDismount(true);
        this.tasks.addTask(this.nextIdleGoalIndex, new EffectAuraGoal(this).setRange(1F).setCooldown(consumptionCooldown + this.consumptionWindUp).setDuration(this.consumptionDuration - this.consumptionWindUp).setTickRate(5).setDamageAmount(1000).setCheckSight(false).setTargetAll(true).setPhase(0));
        this.tasks.addTask(this.nextIdleGoalIndex, this.consumptionGoalP0);

        // Phase 2:
        this.tasks.addTask(this.nextIdleGoalIndex, new SummonMinionsGoal(this).setMinionInfo("geist").setSummonRate(20 * 5).setSummonCap(5).setPhase(1).setPerPlayer(true));
        this.tasks.addTask(this.nextIdleGoalIndex, new SummonMinionsGoal(this).setMinionInfo("epion").setSummonRate(20 * 5).setSummonCap(3).setPhase(1).setPerPlayer(true));

        // Phase 3:
        this.consumptionGoalP2 = new ForceGoal(this).setRange(64F).setCooldown(consumptionCooldown).setDuration(this.consumptionDuration).setWindUp(this.consumptionWindUp).setForce(-0.75F).setPhase(2).setDismount(true);
        this.tasks.addTask(this.nextIdleGoalIndex, this.consumptionGoalP2);
        this.tasks.addTask(this.nextIdleGoalIndex, new EffectAuraGoal(this).setRange(1F).setCooldown(consumptionCooldown + this.consumptionWindUp).setDuration(this.consumptionDuration - this.consumptionWindUp).setTickRate(5).setDamageAmount(1000).setCheckSight(false).setTargetAll(true).setPhase(2));
        this.tasks.addTask(this.nextIdleGoalIndex, new FireProjectilesGoal(this).setProjectile("lobdarklings").setFireRate(10 * 20).setVelocity(0.8F).setScale(2F).setRandomCount(3).setAngle(360).setPhase(2));

        super.initEntityAI();
    }

    /** Returns a larger bounding box for rendering this large entity. **/
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getEntityBoundingBox().grow(200, 50, 200).offset(0, -25, 0);
    }

    @Override
    public void onFirstSpawn() {
        super.onFirstSpawn();
        if(this.getArenaCenter() == null) {
            this.setArenaCenter(this.getPosition());
        }
    }


    // ==================================================
    //                      Updates
    // ==================================================
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

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

        // Consumption Animation:
        if(this.getEntityWorld().isRemote) {
            if(!this.extraAnimation01()) {
                this.consumptionAnimationTime = this.consumptionDuration;
            }
            else {
                this.consumptionAnimationTime--;
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
    public boolean rollWanderChance() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }
    
    
	// ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackEntity(EntityLivingBase target) {
        if(target instanceof EntityBanshee || target instanceof EntityReaper || target instanceof EntityGeist)
    		return false;
        return super.canAttackEntity(target);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("spectralbolt", target, range, 0, new Vec3d(0, 8, 0), 1.2f, 6f, 0F);
        super.attackRanged(target, range);
    }

    // ========== Consumption Attack ==========
    public float getConsumptionAnimation() {
        if(this.consumptionAnimationTime >= this.consumptionDuration) {
            return 0F;
        }
        int windUpThreshhold = this.consumptionDuration - this.consumptionWindUp;
        if(this.consumptionAnimationTime > windUpThreshhold) {
            return 1F - (float)(this.consumptionAnimationTime - windUpThreshhold) / this.consumptionWindUp;
        }
        float finishingTime = (float)this.consumptionWindUp / 2;
        if(this.consumptionAnimationTime < finishingTime) {
            return (float)this.consumptionAnimationTime / finishingTime;
        }
        return 1F;
    }

    public boolean extraAnimation01() {
        if(this.getEntityWorld().isRemote) {
            return super.extraAnimation01();
        }

        if(this.getBattlePhase() == 0) {
            return this.consumptionGoalP0.cooldownTime <= 0;
        }
        if(this.getBattlePhase() == 2) {
            return this.consumptionGoalP2.cooldownTime <= 0;
        }

        return super.extraAnimation01();
    }
	
	
	// ==================================================
   	//                      Minions
   	// ==================================================
    @Override
    public boolean addMinion(EntityLivingBase minion) {
        boolean minionAdded = super.addMinion(minion);
        if(minionAdded && minion instanceof BaseCreatureEntity) {
            BaseCreatureEntity minionCreature = (BaseCreatureEntity) minion;
            minionCreature.targetTasks.addTask(minionCreature.nextFindTargetIndex++, new CopyMasterAttackTargetGoal(minionCreature));
            if (minion instanceof EntityGeist) {
                minionCreature.tasks.addTask(minionCreature.nextIdleGoalIndex++, new GrowGoal(minionCreature).setGrowthAmount(0.1F).setTickRate(20));
                minionCreature.tasks.addTask(minionCreature.nextIdleGoalIndex++, new SuicideGoal(minionCreature).setCountdown(20 * 20));
            }
        }
        return minionAdded;
    }

    @Override
    public void onMinionDeath(EntityLivingBase minion, DamageSource damageSource) {
        super.onMinionDeath(minion, damageSource);

        // Shadowfire Clearing:
        if(minion instanceof EntityEpion) {
            int extinguishWidth = 10;
            int extinguishHeight = 30;
            if(!this.getEntityWorld().isRemote) {
                for(int x = (int)minion.posX - extinguishWidth; x <= (int)minion.posX + extinguishWidth; x++) {
                    for(int y = (int)minion.posY - extinguishHeight; y <= (int)minion.posY + 2; y++) {
                        for(int z = (int)minion.posZ - extinguishWidth; z <= (int)minion.posZ + extinguishWidth; z++) {
                            Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
                            if(block == ObjectManager.getBlock("shadowfire")) {
                                BlockPos placePos = new BlockPos(x, y, z);
                                this.getEntityWorld().setBlockToAir(placePos);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onTryToDamageMinion(EntityLivingBase minion, float damageAmount) {
        super.onTryToDamageMinion(minion, damageAmount);
        if(damageAmount >= 1000) {
            minion.setDead();
            this.heal(25);
        }
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    // ========== Damage ==========
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
        if(this.isBlocking())
            return true;
        return super.isDamageTypeApplicable(type, source, damage);
    }
    
    @Override
    public boolean canBurn() { return false; }

    // ========== Blocking ==========
    @Override
    public boolean isBlocking() {
        return super.isBlocking();
    }

    public boolean canAttackWhileBlocking() {
        return true;
    }

    @Override
    public boolean isDamageEntityApplicable(Entity entity) {
        if(entity instanceof EntityPigZombie) {
            entity.setDead();
            return false;
        }
        if(entity instanceof EntityIronGolem) {
            entity.setDead();
            return false;
        }
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (!player.capabilities.disableDamage && player.posY > this.posY + CreatureManager.getInstance().config.bossAntiFlight) {
                return false;
            }
        }
        return super.isDamageEntityApplicable(entity);
    }


    // ==================================================
    //                    Taking Damage
    // ==================================================
    // ========== Attacked From ==========
    /** Called when this entity has been attacked, uses a DamageSource and damage value. **/
    @Override
    public boolean attackEntityFrom(DamageSource damageSrc, float damageAmount) {
        if(this.playerTargets != null && damageSrc.getTrueSource() != null && damageSrc.getTrueSource() instanceof EntityPlayer) {
            if (!this.playerTargets.contains(damageSrc.getTrueSource()))
                this.playerTargets.add((EntityPlayer)damageSrc.getTrueSource());
        }
        return super.attackEntityFrom(damageSrc, damageAmount);
    }


    // ==================================================
    //                       NBT
    // ==================================================
    // ========== Read ===========
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
    }

    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
        super.writeEntityToNBT(nbtTagCompound);
    }


    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness() {
        return 1.0F;
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
}
