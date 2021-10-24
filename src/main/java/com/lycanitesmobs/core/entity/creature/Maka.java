package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindMasterGoal;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class Maka extends AgeableCreatureEntity {

    public Maka(EntityType<? extends Maka> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;

        this.babySpawnChance = 0.1D;
        this.attackCooldownMax = 10;
        this.fleeHealthPercent = 1.0F;
        this.isAggressiveByDefault = false;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
		this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new TemptGoal(this).setIncludeDiet(true));
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));

		this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindMasterGoal(this).setTargetClass(MakaAlpha.class).setSightCheck(false));
    }

    @Override
    public void onFirstSpawn() {
        CreatureInfo alphaInfo = CreatureManager.getInstance().getCreature("makaalpha");
        if(alphaInfo != null) {
            float alphaChance = (float)alphaInfo.creatureSpawn.spawnWeight / Math.max(this.creatureInfo.creatureSpawn.spawnWeight, 1);
            if (this.getRandom().nextFloat() <= alphaChance) {
				MakaAlpha alpha = (MakaAlpha)CreatureManager.getInstance().getCreature("makaalpha").createEntity(this.getCommandSenderWorld());
                alpha.copyPosition(this);
                this.getCommandSenderWorld().addFreshEntity(alpha);
                this.discard();
            }
        }
        super.onFirstSpawn();
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
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

	@Override
	public boolean canAttack(LivingEntity target) {
		if(target instanceof Maka || target instanceof MakaAlpha)
			return false;
		return super.canAttack(target);
	}

	@Override
	public void setGrowingAge(int age) {
		if(age == 0 && this.getAge() < 0) {
            if (this.getRandom().nextFloat() >= 0.9F) {
				MakaAlpha alpha = (MakaAlpha)CreatureManager.getInstance().getCreature("makaalpha").createEntity(this.getCommandSenderWorld());
                alpha.copyPosition(this);
                this.getCommandSenderWorld().addFreshEntity(alpha);
                this.discard();
            }
        }
        super.setGrowingAge(age);
    }
}
