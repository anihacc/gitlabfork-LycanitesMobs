package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.AvoidGoal;
import com.lycanitesmobs.core.entity.projectile.EntityQuill;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityQuillbeast extends TameableCreatureEntity implements IMob {
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
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true).setEnabled(false));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(10.0F).setChaseTime(-1));
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
            projectile.getPositionVec().getY() -= this.getSize(Pose.STANDING).height / 4;

            // Accuracy:
            float accuracy = i * 1.0F * (this.getRNG().nextFloat() - 0.5F);

            // Set Velocities:
            double d0 = target.getPositionVec().getX() - this.getPositionVec().getX() + accuracy;
            double d1 = target.getPositionVec().getY() - projectile.getPositionVec().getY() + accuracy;
            double d2 = target.getPositionVec().getZ() - this.getPositionVec().getZ() + accuracy;
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
}
