package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.RapidFireProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import com.lycanitesmobs.core.entity.projectile.EntityBlizzard;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntitySerpix extends TameableCreatureEntity implements IGroupHeavy {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySerpix(EntityType<? extends EntitySerpix> entityType, World world) {
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
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.5D).setStaminaTime(100).setRange(12.0F).setMinChaseDistance(8.0F));
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
        List<RapidFireProjectileEntity> projectiles = new ArrayList<>();

        RapidFireProjectileEntity projectileEntry = new RapidFireProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(RapidFireProjectileEntity.class), EntityBlizzard.class, this.getEntityWorld(), this, 15, 3);
        projectiles.add(projectileEntry);

        RapidFireProjectileEntity projectileEntry2 = new RapidFireProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(RapidFireProjectileEntity.class), EntityBlizzard.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry2.offsetX += 1.0D;
        projectiles.add(projectileEntry2);

        RapidFireProjectileEntity projectileEntry3 = new RapidFireProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(RapidFireProjectileEntity.class), EntityBlizzard.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry3.offsetX -= 1.0D;
        projectiles.add(projectileEntry3);

        RapidFireProjectileEntity projectileEntry4 = new RapidFireProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(RapidFireProjectileEntity.class), EntityBlizzard.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry4.offsetZ += 1.0D;
        projectiles.add(projectileEntry4);

        RapidFireProjectileEntity projectileEntry5 = new RapidFireProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(RapidFireProjectileEntity.class), EntityBlizzard.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry5.offsetZ -= 1.0D;
        projectiles.add(projectileEntry5);

        RapidFireProjectileEntity projectileEntry6 = new RapidFireProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(RapidFireProjectileEntity.class), EntityBlizzard.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry6.offsetY += 1.0D;
        projectiles.add(projectileEntry6);

        RapidFireProjectileEntity projectileEntry7 = new RapidFireProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(RapidFireProjectileEntity.class), EntityBlizzard.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry7.offsetY -= 10D;
        projectiles.add(projectileEntry7);

        BlockPos launchPos = this.getFacingPosition(4D);
        for(RapidFireProjectileEntity projectile : projectiles) {
            projectile.setProjectileScale(1f);

            // Y Offset:
            projectile.posY -= this.getSize(Pose.STANDING).height / 4;

            // Accuracy:
            float accuracy = 1.0F * (this.getRNG().nextFloat() - 0.5F);

            // Set Velocities:
            double d0 = target.posX - launchPos.getX() + accuracy;
            double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + accuracy;
            double d2 = target.posZ - launchPos.getZ() + accuracy;
            float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
            float velocity = 1.2F;
            projectile.shoot(d0, d1 + (double)f1, d2, velocity, 6.0F);

            // Launch:
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            projectile.setPosition(launchPos.getX(), launchPos.getY(), launchPos.getZ());
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

    @Override
    public boolean canBreatheUnderwater() {
        return true;
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
        if(type.equals("ooze")) return false;
    	if(type.equals("cactus")) return false;
    	if(type.equals("inWall")) return false;
    	return super.isInvulnerableTo(type, source, damage);
    }
}
