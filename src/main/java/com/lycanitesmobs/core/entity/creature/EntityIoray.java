package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.entity.projectile.EntityWaterJet;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityIoray extends EntityCreatureRideable implements IMob, IGroupPredator {

	WanderGoal wanderAI;
    AttackRangedGoal rangedAttackAI;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityIoray(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0D;
        this.canGrow = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(1, new StayByWaterGoal(this));
        this.field_70714_bg.addTask(2, new PlayerControlGoal(this));
        this.field_70714_bg.addTask(3, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.field_70714_bg.addTask(4, new AttackMeleeGoal(this).setLongMemory(false).setMaxChaseDistanceSq(4.0F));
        this.rangedAttackAI = new AttackRangedGoal(this).setSpeed(0.75D).setStaminaTime(100).setRange(8.0F).setMinChaseDistance(4.0F).setMountedAttacking(false);
        this.field_70714_bg.addTask(5, rangedAttackAI);
        this.field_70714_bg.addTask(6, this.aiSit);
        this.field_70714_bg.addTask(7, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.wanderAI = new WanderGoal(this);
        this.field_70714_bg.addTask(8, wanderAI.setPauseRate(60));
        this.field_70714_bg.addTask(9, new BegGoal(this));
        this.field_70714_bg.addTask(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new LookIdleGoal(this));

        this.field_70715_bh.addTask(0, new RiderRevengeTargetingGoal(this));
        this.field_70715_bh.addTask(1, new RiderAttackTargetingGoal(this));
        this.field_70715_bh.addTask(2, new OwnerRevengeTargetingGoal(this));
        this.field_70715_bh.addTask(3, new OwnerAttackTargetingGoal(this));
        this.field_70715_bh.addTask(4, new OwnerDefenseTargetingGoal(this));
        this.field_70715_bh.addTask(5, new RevengeTargetingGoal(this).setHelpCall(true));
        this.field_70715_bh.addTask(6, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(7, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(8, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(8, new AttackTargetingGoal(this).setTargetClass(IGroupAnimal.class));
            this.field_70715_bh.addTask(8, new AttackTargetingGoal(this).setTargetClass(AnimalEntity.class));
            this.field_70715_bh.addTask(8, new AttackTargetingGoal(this).setTargetClass(SquidEntity.class));
        }
        this.field_70715_bh.addTask(9, new OwnerDefenseTargetingGoal(this));
    }
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void riderEffects(LivingEntity rider) {
        rider.addPotionEffect(new EffectInstance(Effects.field_76427_o, (5 * 20) + 5, 1));
        super.riderEffects(rider);
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

    // ========== Mounted Offset ==========
    @Override
    public double getMountedYOffset() {
        return (double)this.getSize(Pose.STANDING).height * 0.6D;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAboveWater() {
        return false;
    }

    @Override
    public boolean canBurn() { return false; }


    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    EntityWaterJet projectile = null;
    @Override
    public void attackRanged(Entity target, float range) {
        // Update Laser:
        if(this.projectile != null && this.projectile.isAlive()) {
            this.projectile.setTime(20);
        }
        else {
            this.projectile = null;
        }

        // Create New Laser:
        if(this.projectile == null) {
            // Type:
            this.projectile = new EntityWaterJet(this.getEntityWorld(), this, 20, 10);
            this.projectile.setOffset(0, 0, 1);

            // Launch:
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.getEntityWorld().func_217376_c(projectile);
        }

        super.attackRanged(target, range);
    }


    // ==================================================
    //                   Mount Ability
    // ==================================================
    EntityWaterJet abilityProjectile = null;
    public void mountAbility(Entity rider) {
        if(this.getEntityWorld().isRemote)
            return;

        if(this.getStamina() < this.getStaminaRecoveryMax() * 2)
            return;

        if(this.hasAttackTarget())
            this.setAttackTarget(null);

        // Update Laser:
        if(this.abilityProjectile != null && this.abilityProjectile.isAlive()) {
            this.abilityProjectile.setTime(20);
        }
        else {
            this.abilityProjectile = null;
        }

        // Create New Laser:
        if(this.abilityProjectile == null) {
            // Type:
            if(this.getControllingPassenger() == null || !(this.getControllingPassenger() instanceof LivingEntity))
                return;

            this.abilityProjectile = new EntityWaterJet(this.getEntityWorld(), (LivingEntity)this.getControllingPassenger(), 25, 20, this);
            this.abilityProjectile.setOffset(0, 1, 1);

            // Launch:
            this.playSound(abilityProjectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.getEntityWorld().func_217376_c(abilityProjectile);
        }

        this.applyStaminaCost();
    }

    // Dismount:
    @Override
    public void onDismounted(Entity entity) {
        super.onDismounted(entity);
        if(entity != null && entity instanceof LivingEntity) {
            ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.field_76427_o, 5 * 20, 1));
        }
    }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 10; }


    // ==================================================
    //                      Breeding
    // ==================================================
    // ========== Create Child ==========
    @Override
    public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
        return new EntityIoray(this.getEntityWorld());
    }


    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
        return ObjectLists.inItemList("cookedmeat", testStack) || ObjectLists.inItemList("cookedfish", testStack);
    }


    // ==================================================
    //                     Pet Control
    // ==================================================
    @Override
    public boolean petControlsEnabled() { return true; }
}
