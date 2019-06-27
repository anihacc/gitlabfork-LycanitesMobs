package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.entity.projectile.EntityQuill;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityQuillbeast extends TameableCreatureEntity implements IMob {
	AttackRangedGoal aiAttackRanged;
	AttackMeleeGoal aiAttackMelee;
	AvoidGoal aiAvoid;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityQuillbeast(EntityType<? extends EntityQuillbeast> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(1, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.aiAttackMelee = new AttackMeleeGoal(this).setLongMemory(true).setEnabled(false);
        this.goalSelector.addGoal(2, this.aiAttackMelee);
        this.aiAttackRanged = new AttackRangedGoal(this).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(10.0F).setChaseTime(-1);
        this.goalSelector.addGoal(2, this.aiAttackRanged);
        this.goalSelector.addGoal(3, this.aiSit);
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.aiAvoid = new AvoidGoal(this).setNearSpeed(1.5D).setFarSpeed(1.3D).setNearDistance(5.0D).setFarDistance(9.0D);
        this.goalSelector.addGoal(5, this.aiAvoid);
        this.goalSelector.addGoal(6, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RevengeOwnerGoal(this));
        this.targetSelector.addGoal(1, new CopyOwnerAttackTargetGoal(this));
        this.targetSelector.addGoal(2, new RevengeGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(3, new FindAttackTargetGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(3, new FindAttackTargetGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(4, new FindAvoidTargetGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(4, new FindAvoidTargetGoal(this).setTargetClass(IGroupHunter.class));
        this.targetSelector.addGoal(4, new FindAvoidTargetGoal(this).setTargetClass(IGroupPredator.class));
        this.targetSelector.addGoal(4, new FindAvoidTargetGoal(this).setTargetClass(IGroupAlpha.class));
        this.targetSelector.addGoal(5, new FindAvoidTargetGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(6, new DefendOwnerGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== AI Update ==========
	@Override
    public void updateAITasks() {
        // Avoid Attack Target:
        if(!this.getEntityWorld().isRemote) {
	        if(this.getAttackTarget() != null && this.getAttackTarget() != this.getAvoidTarget())
	        	this.setAvoidTarget(this.getAttackTarget());
        }
		
        super.updateAITasks();
	}
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        for(int i = -2; i < 12; i++) {
            // Type:
            EntityQuill projectile = new EntityQuill(ProjectileManager.getInstance().oldProjectileTypes.get(EntityQuill.class), this.getEntityWorld(), this);

            // Y Offset:
            projectile.posY -= this.getSize(Pose.STANDING).height / 4;

            // Accuracy:
            float accuracy = i * 1.0F * (this.getRNG().nextFloat() - 0.5F);

            // Set Velocities:
            double d0 = target.posX - this.posX + accuracy;
            double d1 = target.posY - projectile.posY + accuracy;
            double d2 = target.posZ - this.posZ + accuracy;
            float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
            float velocity = 1.2F;
            projectile.shoot(d0, d1 + (double) f1, d2, velocity, 6.0F);

            // Launch:
            if(i == 0)
                this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.getEntityWorld().addEntity(projectile);
        }

        super.attackRanged(target, range);
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
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
        return ObjectLists.inItemList("vegetables", testStack);
    }
}
