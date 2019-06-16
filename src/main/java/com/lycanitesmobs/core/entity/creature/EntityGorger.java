package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.entity.projectile.EntityMagma;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityGorger extends EntityCreatureTameable implements IGroupPredator, IGroupFire, IGroupHeavy {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGorger(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.hasAttackSound = false;

        this.babySpawnChance = 0.25D;
        this.growthTime = -120000;
        this.setupMob();
        this.hitAreaWidthScale = 1.5F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new SwimmingGoal(this).setSink(true));
        this.field_70714_bg.addTask(1, new StealthGoal(this).setStealthTime(60));
        this.field_70714_bg.addTask(2, this.aiSit);
        this.field_70714_bg.addTask(3, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(4, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.field_70714_bg.addTask(5, new AttackRangedGoal(this).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(8.0F));
        this.field_70714_bg.addTask(7, new WanderGoal(this));
        this.field_70714_bg.addTask(9, new BegGoal(this));

        this.field_70715_bh.addTask(0, new OwnerRevengeTargetingGoal(this));
        this.field_70715_bh.addTask(1, new OwnerAttackTargetingGoal(this));
        this.field_70715_bh.addTask(2, new RevengeTargetingGoal(this));
        this.field_70715_bh.addTask(2, new AttackTargetingGoal(this).setTargetClass(IGroupIce.class));
        this.field_70715_bh.addTask(2, new AttackTargetingGoal(this).setTargetClass(IGroupWater.class));
        this.field_70715_bh.addTask(2, new AttackTargetingGoal(this).setTargetClass(SnowGolemEntity.class));
        this.field_70715_bh.addTask(3, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(3, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(IGroupPlant.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(3, new AttackTargetingGoal(this).setTargetClass(IGroupAnimal.class));
            this.field_70715_bh.addTask(3, new AttackTargetingGoal(this).setTargetClass(AnimalEntity.class));
        }
        this.field_70715_bh.addTask(6, new OwnerDefenseTargetingGoal(this));
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
        if(this.isInWater())
            return 2.0F;
        return 1.0F;
    }

    // Pushed By Water:
    @Override
    public boolean isPushedByWater() {
        return false;
    }


    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        // Type:
        EntityProjectileBase projectile = new EntityMagma(this.getEntityWorld(), this);
        projectile.setProjectileScale(2f);

        // Y Offset:
        projectile.posY -= this.getSize(Pose.STANDING).height / 4;

        // Accuracy:
        float accuracy = 1.0F * (this.getRNG().nextFloat() - 0.5F);

        // Set Velocities:
        double d0 = target.posX - this.posX + accuracy;
        double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + accuracy;
        double d2 = target.posZ - this.posZ + accuracy;
        float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
        float velocity = 1.2F;
        projectile.shoot(d0, d1 + (double) f1, d2, velocity, 6.0F);

        // Launch:
        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.getEntityWorld().func_217376_c(projectile);

        // Random Projectiles:
        for(int i = 0; i < 10; i++) {
            projectile = new EntityMagma(this.getEntityWorld(), this);
            projectile.setProjectileScale(2f);
            projectile.shoot((this.getRNG().nextFloat()) - 0.5F, this.getRNG().nextFloat(), (this.getRNG().nextFloat()) - 0.5F, 0.5F, 3.0F);
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.getEntityWorld().func_217376_c(projectile);
        }

        super.attackRanged(target, range);
    }
    
    
    // ==================================================
   	//                      Stealth
   	// ==================================================
    @Override
    public boolean canStealth() {
        if(this.isTamed() && this.isSitting())
            return false;
        BlockState blockState = this.getEntityWorld().getBlockState(this.getPosition().add(0, -1, 0));
        if(blockState.getBlock() != Blocks.AIR) {
            if(blockState.getMaterial() == Material.EARTH) return true;
            if(blockState.getMaterial() == Material.ORGANIC) return true;
            if(blockState.getMaterial() == Material.LEAVES) return true;
            if(blockState.getMaterial() == Material.SAND) return true;
            if(blockState.getMaterial() == Material.CLAY) return true;
            if(blockState.getMaterial() == Material.SNOW) return true;
            if(blockState.getMaterial() == Material.SNOW_BLOCK) return true;
        }
        if(blockState.getBlock() == Blocks.NETHERRACK)
            return true;
        return false;
    }
    
    
    // ==================================================
   	//                     Abilities
   	// ==================================================
    public boolean canBeTempted() {
    	return this.isChild();
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	if(type.equals("inWall")) return false;
    	return super.isInvulnerableTo(type, source, damage);
    }

    @Override
    public boolean canBurn() { return false; }

    @Override
    public boolean waterDamage() { return true; }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canBreatheAboveWater() {
        return true;
    }


    // ==================================================
    //                    Taking Damage
    // ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
        if(damageSrc.isFireDamage())
            return 0F;
        else return super.getDamageModifier(damageSrc);
    }


    // ==================================================
    //                      Breeding
    // ==================================================
    // ========== Create Child ==========
    @Override
    public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
        return new EntityGorger(this.getEntityWorld());
    }

    // ========== Breeding Item ==========
    @Override
    public boolean isBreedingItem(ItemStack par1ItemStack) {
        return false;
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("cookedmeat", testStack);
    }
}
