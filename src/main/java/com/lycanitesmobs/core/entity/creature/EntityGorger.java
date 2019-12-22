package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import com.lycanitesmobs.core.entity.projectile.EntityMagma;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityGorger extends TameableCreatureEntity implements IGroupHeavy {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGorger(EntityType<? extends EntityGorger> entityType, World world) {
        super(entityType, world);
        
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
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextPriorityGoalIndex++, new StealthGoal(this).setStealthTime(60));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(8.0F));
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
        BaseProjectileEntity projectile = new EntityMagma(ProjectileManager.getInstance().oldProjectileTypes.get(EntityMagma.class), this.getEntityWorld(), this);
        projectile.setProjectileScale(2f);

        // Y Offset:
        projectile.getPositionVec().getY() -= this.getSize(Pose.STANDING).height / 4;

        // Accuracy:
        float accuracy = 1.0F * (this.getRNG().nextFloat() - 0.5F);

        // Set Velocities:
        double d0 = target.getPositionVec().getX() - this.getPositionVec().getX() + accuracy;
        double d1 = target.getPositionVec().getY() + (double)target.getEyeHeight() - 1.100000023841858D - projectile.getPositionVec().getY() + accuracy;
        double d2 = target.getPositionVec().getZ() - this.getPositionVec().getZ() + accuracy;
        float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
        float velocity = 1.2F;
        projectile.shoot(d0, d1 + (double) f1, d2, velocity, 6.0F);

        // Launch:
        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.getEntityWorld().addEntity(projectile);

        // Random Projectiles:
        for(int i = 0; i < 10; i++) {
            projectile = new EntityMagma(ProjectileManager.getInstance().oldProjectileTypes.get(EntityMagma.class), this.getEntityWorld(), this);
            projectile.setProjectileScale(2f);
            projectile.shoot((this.getRNG().nextFloat()) - 0.5F, this.getRNG().nextFloat(), (this.getRNG().nextFloat()) - 0.5F, 0.5F, 3.0F);
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.getEntityWorld().addEntity(projectile);
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
    public boolean canBreatheAir() {
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
}
