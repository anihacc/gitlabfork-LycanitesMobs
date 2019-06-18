package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupDemon;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.AttackTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeTargetingGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class EntityTrite extends EntityCreatureBase implements IMob, IGroupDemon {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTrite(EntityType<? extends EntityTrite> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.hasAttackSound = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(3, new AttackMeleeGoal(this));
        this.goalSelector.addGoal(6, new WanderGoal(this).setSpeed(1.0D).setPauseRate(30));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RevengeTargetingGoal(this));
        this.targetSelector.addGoal(2, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(2, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(ChickenEntity.class));
            this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(3, 1));
            this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(AnimalEntity.class).setPackHuntingScale(3, 1));
        }
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

        // Leap:
        if(this.hasAttackTarget() && this.onGround && !this.getEntityWorld().isRemote && this.rand.nextInt(10) == 0)
        	this.leap(6.0F, 0.6D, this.getAttackTarget());
    }
    
    
    // ==================================================
   	//                      Attacks
   	// ==================================================
    @Override
    public boolean canAttack(LivingEntity target) {
        if(target instanceof EntityAstaroth ||  target instanceof EntityAsmodeus)
            return false;
        return super.canAttack(target);
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    // ========== Movement ==========
    @Override
    public boolean canClimb() { return true; }
    
    
    // ==================================================
  	//                     Immunities
  	// ==================================================
    @Override
    public boolean isPotionApplicable(EffectInstance potionEffect) {
		if(potionEffect.getPotion() == Effects.WITHER)
			return false;
		if(ObjectManager.getEffect("decay") != null)
			if(potionEffect.getPotion() == ObjectManager.getEffect("decay")) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }

    @Override
    public float getFallResistance() {
        return 10;
    }
    
    // ========== Environmental ==========
    @Override
    public boolean webProof() { return true; }
}
