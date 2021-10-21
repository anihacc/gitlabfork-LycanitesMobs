package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.entity.goals.targeting.CopyMasterAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindMasterGoal;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class EntityJouste extends AgeableCreatureEntity {
    public EntityJouste(EntityType<? extends EntityJouste> entityType, Level world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.babySpawnChance = 0.1D;
        this.canGrow = true;
        this.attackCooldownMax = 10;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
		this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new TemptGoal(this).setIncludeDiet(true));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));

        this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindMasterGoal(this).setTargetClass(EntityJousteAlpha.class).setSightCheck(false));
		this.targetSelector.addGoal(this.nextFindTargetIndex++, new CopyMasterAttackTargetGoal(this));
    }

    @Override
    public void onFirstSpawn() {
        // Random Alpha:
        CreatureInfo alphaInfo = CreatureManager.getInstance().getCreature("joustealpha");
        if(alphaInfo != null) {
            float alphaChance = (float)alphaInfo.creatureSpawn.spawnWeight / Math.max(this.creatureInfo.creatureSpawn.spawnWeight, 1);
            if (this.getRandom().nextFloat() <= alphaChance) {
                EntityJousteAlpha alpha = (EntityJousteAlpha)CreatureManager.getInstance().getCreature("joustealpha").createEntity(this.getCommandSenderWorld());
                alpha.copyPosition(this);
                this.getCommandSenderWorld().addFreshEntity(alpha);
                this.remove();
            }
        }
        super.onFirstSpawn();
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
    public boolean canBeLeashed(Player player) {
	    if(!this.hasAttackTarget() && !this.hasMaster())
	        return true;
	    return super.canBeLeashed(player);
    }

	@Override
	public boolean canAttack(LivingEntity target) {
		if(target instanceof EntityJousteAlpha)
			return false;
		return super.canAttack(target);
	}

	@Override
	public boolean isProtective(Entity entity) {
		if(entity instanceof EntityJouste) {
			return true;
		}
		return super.isProtective(entity);
	}

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	return super.isVulnerableTo(type, source, damage);
    }

	@Override
	public void setGrowingAge(int age) {
		if(age == 0 && this.getAge() < 0)
			if(this.getRandom().nextFloat() >= 0.9F) {
				EntityJousteAlpha alpha = (EntityJousteAlpha)CreatureManager.getInstance().getCreature("joustealpha").createEntity(this.getCommandSenderWorld());
				alpha.copyPosition(this);
				this.getCommandSenderWorld().addFreshEntity(alpha);
				this.remove();
			}
        super.setGrowingAge(age);
    }
}
