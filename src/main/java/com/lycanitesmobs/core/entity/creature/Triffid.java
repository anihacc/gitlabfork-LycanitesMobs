package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public class Triffid extends TameableCreatureEntity implements Enemy {

    public Triffid(EntityType<? extends Triffid> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.spawnsUnderground = false;
        this.hasAttackSound = true;
        this.spreadFire = true;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(Player.class).setLongMemory(false));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(this.getAirSupply() >= 0) {
            if (this.isInWater())
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 3 * 20, 2));
            else if (this.isInWaterRainOrBubble())
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 3 * 20, 1));
        }
    }
    
    

    public float getDamageModifier(DamageSource damageSrc) {
        if(damageSrc.isFire())
            return 2.0F;
        if(damageSrc.getEntity() != null) {
            ItemStack heldItem = ItemStack.EMPTY;
            if(damageSrc.getEntity() instanceof LivingEntity) {
                LivingEntity entityLiving = (LivingEntity)damageSrc.getEntity();
                if(!entityLiving.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                    heldItem = entityLiving.getItemInHand(InteractionHand.MAIN_HAND);
                }
            }
            if(ObjectLists.isAxe(heldItem)) {
                return 2.0F;
            }
        }
        return super.getDamageModifier(damageSrc);
    }
    
    

    @Override
    public float getFallResistance() {
    	return 100;
    }


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    public boolean petControlsEnabled() { return true; }
}