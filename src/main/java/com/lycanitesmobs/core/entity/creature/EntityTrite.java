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
    public EntityTrite(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.hasAttackSound = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new SwimmingGoal(this));
        this.field_70714_bg.addTask(3, new AttackMeleeGoal(this));
        this.field_70714_bg.addTask(6, new WanderGoal(this).setSpeed(1.0D).setPauseRate(30));
        this.field_70714_bg.addTask(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new LookIdleGoal(this));

        this.field_70715_bh.addTask(0, new RevengeTargetingGoal(this));
        this.field_70715_bh.addTask(2, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(2, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(3, new AttackTargetingGoal(this).setTargetClass(ChickenEntity.class));
            this.field_70715_bh.addTask(5, new AttackTargetingGoal(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(3, 1));
            this.field_70715_bh.addTask(5, new AttackTargetingGoal(this).setTargetClass(AnimalEntity.class).setPackHuntingScale(3, 1));
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
		if(potionEffect.getPotion() == Effects.field_82731_v)
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
