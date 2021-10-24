package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class JousteAlpha extends AgeableCreatureEntity {

    public JousteAlpha(EntityType<? extends JousteAlpha> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.attackCooldownMax = 10;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
		this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(this.getClass()));

		super.registerGoals();

		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(Player.class).setLongMemory(false));
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }
	
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        BlockState blockState = this.getCommandSenderWorld().getBlockState(new BlockPos(x, y - 1, z));
        if(blockState.getBlock() != Blocks.AIR) {
            if(blockState.getMaterial() == Material.SAND)
                return 10F;
            if(blockState.getMaterial() == Material.CLAY)
                return 7F;
            if(blockState.getMaterial() == Material.STONE)
                return 5F;
        }
        return super.getBlockPathWeight(x, y, z);
    }

	@Override
	public boolean canAttackType(EntityType targetType) {
		if(targetType == this.getType())
			return true;
		return super.canAttackType(targetType);
	}

    @Override
    public void setTarget(LivingEntity entity) {
    	if(entity == null && this.getTarget() instanceof JousteAlpha && this.getHealth() < this.getMaxHealth()) {
    		this.heal((this.getMaxHealth() - this.getHealth()) / 2);
    		this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 10 * 20, 2, false, true));
    	}
    	super.setTarget(entity);
    }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus"))
    		return false;
    	return super.isVulnerableTo(type, source, damage);
    }

	@Override
	public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
		return (AgeableCreatureEntity) CreatureManager.getInstance().getCreature("jouste").createEntity(this.getCommandSenderWorld());
	}
}
