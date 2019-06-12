package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.api.IGroupShadow;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityWraamon extends EntityCreatureTameable implements IMob, IGroupShadow {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityWraamon(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIStealth(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.tasks.addTask(2, new EntityAIAttackMelee(this));
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.tasks.addTask(6, new EntityAIWander(this).setSpeed(1.0D).setPauseRate(30));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityChicken.class));
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(3, 1));
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityAnimal.class).setPackHuntingScale(3, 1));
        }
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Leap:
        if(!this.getEntityWorld().isRemote && this.hasAttackTarget() && this.onGround && !this.getEntityWorld().isRemote && this.rand.nextInt(10) == 0)
        	this.leap(6.0F, 0.6D, this.getAttackTarget());
    }
    
    
    // ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
        // Disable Knockback:
        double targetKnockbackResistance = 0;
        if(target instanceof LivingEntity) {
            targetKnockbackResistance = ((LivingEntity)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue();
            ((LivingEntity)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        }

        // Melee Attack:
    	if(!super.attackMelee(target, damageScale))
    		return false;

        // Restore Knockback:
        if(target instanceof LivingEntity)
            ((LivingEntity)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
        
        return true;
    }


    // ==================================================
    //                     Abilities
    // ==================================================
    // ========== Movement ==========
    @Override
    public boolean canClimb() { return false; }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
  	//                     Immunities
  	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("inWall"))
            return false;
        return super.isInvulnerableTo(type, source, damage);
    }

    @Override
    public float getFallResistance() {
        return 10;
    }
}
