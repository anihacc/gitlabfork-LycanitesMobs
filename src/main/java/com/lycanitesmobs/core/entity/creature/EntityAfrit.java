package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupFire;
import com.lycanitesmobs.api.IGroupIce;
import com.lycanitesmobs.api.IGroupPlant;
import com.lycanitesmobs.api.IGroupWater;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.entity.projectile.EntityScorchfireball;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAfrit extends EntityCreatureTameable implements IMob, IGroupFire {

    protected boolean wantsToLand;
    protected boolean  isLanded;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAfrit(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.isLavaCreature = true;
        this.hasAttackSound = false;
        this.flySoundSpeed = 20;

        this.setupMob();

        this.stepHeight = 1.0F;

        this.setPathPriority(PathNodeType.LAVA, 0F);
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(2, new EntityAIAttackRanged(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(5.0F).setCheckSight(false));
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.tasks.addTask(5, new EntityAITempt(this).setTemptDistanceMin(4.0D));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupIce.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupWater.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntitySnowman.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupPlant.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Land/Fly:
        if(!this.getEntityWorld().isRemote) {
            if(this.isLanded) {
                this.wantsToLand = false;
                if(this.updateTick % (5 * 20) == 0 && this.getRNG().nextBoolean()) {
                    this.leap(1.0D, 1.0D);
                    this.isLanded = false;
                }
            }
            else {
                if(this.wantsToLand) {
                    if(!this.isLanded && this.isSafeToLand()) {
                        this.isLanded = true;
                    }
                }
                else {
                    if (this.updateTick % (5 * 20) == 0 && this.getRNG().nextBoolean()) {
                        this.wantsToLand = true;
                    }
                }
            }
        }

        // Particles:
        if(this.getEntityWorld().isRemote)
            for(int i = 0; i < 2; ++i) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
                this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
            }
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Get Wander Position ==========
    @Override
    public BlockPos getWanderPosition(BlockPos wanderPosition) {
        if(this.wantsToLand || !this.isLanded) {
            BlockPos groundPos;
            for(groundPos = wanderPosition.down(); groundPos.getY() > 0 && this.getEntityWorld().getBlockState(groundPos).getBlock() == Blocks.AIR; groundPos = groundPos.down()) {}
            if(this.getEntityWorld().getBlockState(groundPos).getMaterial().isSolid()) {
                return groundPos.up();
            }
        }
        return super.getWanderPosition(wanderPosition);
    }

    // ========== Get Flight Offset ==========
    @Override
    public double getFlightOffset() {
        if(!this.wantsToLand) {
            super.getFlightOffset();
        }
        return 0;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
        return super.canAttackClass(targetClass);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile(EntityScorchfireball.class, target, range, 0, new Vec3d(0, 0, 0), 0.8f, 2f, 6F);
        super.attackRanged(target, range);
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return !this.isLanded; }

    @Override
    public boolean isStrongSwimmer() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    @Override
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 5; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean canBurn() { return false; }

    @Override
    public boolean waterDamage() { return true; }

    @Override
    public float getFallResistance() {
        return 100;
    }


    // ==================================================
    //                       Drops
    // ==================================================
    // ========== Apply Drop Effects ==========
    /** Used to add effects or alter the dropped entity item. **/
    @Override
    public void applyDropEffects(EntityItemCustom entityitem) {
        entityitem.setCanBurn(false);
    }


    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    @Override
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFireDamage())
    		return 0F;
    	else return super.getDamageModifier(damageSrc);
    }


    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
        return ObjectLists.inItemList("CookedMeat", testStack);
    }
}
