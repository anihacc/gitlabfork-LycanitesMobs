package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupFire;
import com.lycanitesmobs.api.IGroupPlant;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class EntityTriffid extends TameableCreatureEntity implements IMob, IGroupPlant {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTriffid(EntityType<? extends EntityTriffid> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsUnderground = false;
        this.hasAttackSound = true;
        this.spreadFire = true;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(3, new AttackMeleeGoal(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.goalSelector.addGoal(4, new AttackMeleeGoal(this));
        this.goalSelector.addGoal(5, this.aiSit);
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(8, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RevengeOwnerGoal(this));
        this.targetSelector.addGoal(1, new CopyOwnerAttackTargetGoal(this));
        this.targetSelector.addGoal(2, new RevengeGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(3, new FindAttackTargetGoal(this).setTargetClass(IGroupFire.class));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(5, new FindAttackTargetGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(6, new DefendOwnerGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

        // Water Healing:
        if(this.getAir() >= 0) {
            if (this.isInWater())
                this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 3 * 20, 2));
            else if (this.isInWaterRainOrBubbleColumn())
                this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 3 * 20, 1));
        }
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
        if(damageSrc.isFireDamage())
            return 2.0F;
        if(damageSrc.getTrueSource() != null) {
            Item heldItem = null;
            if(damageSrc.getTrueSource() instanceof PlayerEntity) {
                PlayerEntity entityPlayer = (PlayerEntity)damageSrc.getTrueSource();
                if(!entityPlayer.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
                    heldItem = entityPlayer.getHeldItem(Hand.MAIN_HAND).getItem();
                }
            }
            else if(damageSrc.getTrueSource() instanceof LivingEntity) {
                LivingEntity entityLiving = (LivingEntity)damageSrc.getTrueSource();
                if(!entityLiving.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
                    heldItem = entityLiving.getHeldItem(Hand.MAIN_HAND).getItem();
                }
            }
            if(ObjectLists.isAxe(heldItem))
                return 2.0F;
        }
        return super.getDamageModifier(damageSrc);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
    	return 100;
    }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 10; }
	

    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
