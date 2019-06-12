package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.entity.projectile.EntityThrowingScythe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityClink extends EntityCreatureTameable implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityClink(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;
        this.setupMob();
        
        // Stats:
        this.attackPhaseMax = 3;
        this.setAttackCooldownMax(10);
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackRanged(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(4.0F));
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.tasks.addTask(6, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile(EntityThrowingScythe.class, target, range, 0, new Vec3d(0, 0, 0), 1.2f, 2f, 1F);
		this.nextAttackPhase();
		super.attackRanged(target, range);
	}

    @Override
	public int getMeleeCooldown() {
    	if(this.getAttackPhase() == 2)
    		return super.getMeleeCooldown();
		return Math.round((float)super.getMeleeCooldown() / 6);
	}

	@Override
	public int getRangedCooldown() {
		if(this.getAttackPhase() == 2)
			return super.getRangedCooldown();
		return Math.round((float)super.getRangedCooldown() / 6);
	}
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	return super.isInvulnerableTo(type, source, damage);
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
