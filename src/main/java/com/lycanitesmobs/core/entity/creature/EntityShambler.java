package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class EntityShambler extends TameableCreatureEntity implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityShambler(EntityType<? extends EntityShambler> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsUnderground = true;
        this.hasAttackSound = true;
        this.spreadFire = true;

        this.canGrow = true;
        this.babySpawnChance = 0.01D;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
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
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;
    	
    	// Leech:
    	float leeching = this.getEffectStrength(this.getAttackDamage(damageScale) / 2);
    	this.heal(leeching);
        
        return true;
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
        if(damageSrc.isFireDamage())
            return 2.0F;
        if(damageSrc.getTrueSource() != null) {
            ItemStack heldItem = ItemStack.EMPTY;
            if(damageSrc.getTrueSource() instanceof LivingEntity) {
                LivingEntity entityLiving = (LivingEntity)damageSrc.getTrueSource();
                if(!entityLiving.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
                    heldItem = entityLiving.getHeldItem(Hand.MAIN_HAND);
                }
            }
            if(ObjectLists.isAxe(heldItem)) {
                return 2.0F;
            }
        }
        return super.getDamageModifier(damageSrc);
    }
	

    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
