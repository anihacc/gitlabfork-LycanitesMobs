package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendEntitiesGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MakaAlpha extends AgeableCreatureEntity {

    public MakaAlpha(EntityType<? extends MakaAlpha> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.attackCooldownMax = 10;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
		this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(this.getClass()));
		this.targetSelector.addGoal(this.nextSpecialTargetIndex++, new DefendEntitiesGoal(this, Maka.class));

		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(Player.class).setLongMemory(false));
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }
	
	@Override
	public void aiStep() {
		super.aiStep();
		
		if(this.hasAttackTarget() && this.getTarget() instanceof MakaAlpha) {
			if(this.getHealth() / this.getMaxHealth() <= 0.25F || this.getTarget().getHealth() / this.getTarget().getMaxHealth() <= 0.25F) {
				this.setTarget(null);
			}
		}
	}

	@Override
	public boolean isProtective(Entity entity) {
		if(entity instanceof Maka) {
			return true;
		}
		return super.isProtective(entity);
	}
	
    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        BlockState blockState = this.getCommandSenderWorld().getBlockState(new BlockPos(x, y - 1, z));
        Block block = blockState.getBlock();
        if(block != Blocks.AIR) {
            if(blockState.getMaterial() == Material.GRASS)
                return 10F;
            if(blockState.getMaterial() == Material.DIRT)
                return 7F;
        }
        return super.getBlockPathWeight(x, y, z);
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return true;
    }

	@Override
    public boolean canAttack(LivingEntity target) {
		if(target instanceof Maka)
			return false;
    	if(target instanceof MakaAlpha && (this.getHealth() / this.getMaxHealth() <= 0.25F || target.getHealth() / target.getMaxHealth() <= 0.25F))
    		return false;
    	return super.canAttack(target);
    }

	@Override
	public boolean canAttackOwnSpecies() {
		return true;
	}

    @Override
    public void setTarget(LivingEntity entity) {
    	if(entity == null && this.getTarget() instanceof MakaAlpha) {
    		this.heal((this.getMaxHealth() - this.getHealth()) / 2);
    		this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 20, 2, false, false));
			this.getTarget().heal((this.getMaxHealth() - this.getHealth()) / 2);
			this.getTarget().addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 20, 2, false, false));
    	}
    	super.setTarget(entity);
    }

	@Override
	public boolean rollAttackTargetChance(LivingEntity target) {
    	if(target instanceof Player || target.getType() == this.getType())
    		return this.getRandom().nextDouble() <= 0.01D;
		return true;
	}
    
    

    public float getDamageModifier(DamageSource damageSrc) {
        float damageMod = super.getDamageModifier(damageSrc);
        if(damageSrc.getEntity() instanceof MakaAlpha)
            damageMod *= 2;
        return damageMod;
    }

	@Override
	public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
		return (AgeableCreatureEntity) CreatureManager.getInstance().getCreature("maka").createEntity(this.getCommandSenderWorld());
	}
}
