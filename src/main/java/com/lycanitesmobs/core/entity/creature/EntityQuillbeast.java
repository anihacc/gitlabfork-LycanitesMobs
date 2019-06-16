package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.entity.projectile.EntityQuill;
import net.minecraft.entity.Entity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityQuillbeast extends EntityCreatureTameable implements IMob {
	EntityAIAttackRanged aiAttackRanged;
	EntityAIAttackMelee aiAttackMelee;
	EntityAIAvoid aiAvoid;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityQuillbeast(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        this.field_70714_bg.addTask(1, new EntityAITempt(this).setTemptDistanceMin(4.0D));
        this.aiAttackMelee = new EntityAIAttackMelee(this).setLongMemory(true).setEnabled(false);
        this.field_70714_bg.addTask(2, this.aiAttackMelee);
        this.aiAttackRanged = new EntityAIAttackRanged(this).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(10.0F).setChaseTime(-1);
        this.field_70714_bg.addTask(2, this.aiAttackRanged);
        this.field_70714_bg.addTask(3, this.aiSit);
        this.field_70714_bg.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.aiAvoid = new EntityAIAvoid(this).setNearSpeed(1.5D).setFarSpeed(1.3D).setNearDistance(5.0D).setFarDistance(9.0D);
        this.field_70714_bg.addTask(5, this.aiAvoid);
        this.field_70714_bg.addTask(6, new EntityAIWander(this));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAvoid(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAvoid(this).setTargetClass(IGroupHunter.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAvoid(this).setTargetClass(IGroupAlpha.class));
        this.field_70715_bh.addTask(5, new EntityAITargetAvoid(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(6, new EntityAITargetOwnerThreats(this));
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
            EntityQuill projectile = new EntityQuill(this.getEntityWorld(), this);

            // Y Offset:
            projectile.posY -= this.height / 4;

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
            this.getEntityWorld().spawnEntity(projectile);
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
