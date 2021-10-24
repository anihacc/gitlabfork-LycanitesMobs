package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class Abtu extends TameableCreatureEntity implements Enemy {
    int swarmLimit = 5;
    
    public Abtu(EntityType<? extends Abtu> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0.9D;
        this.canGrow = true;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
    }

	@Override
	public void loadCreatureFlags() {
		this.swarmLimit = this.creatureInfo.getFlag("swarmLimit", this.swarmLimit);
	}

	@Override
    public void aiStep() {
        if(this.hasAttackTarget() && this.updateTick % 20 == 0) {
			this.allyUpdate();
		}

        if(!this.getCommandSenderWorld().isClientSide) {
            if(this.hasAttackTarget() && this.isBaby() && (this.isInWater() || this.onGround)) {
                if(this.getRandom().nextInt(10) == 0)
                    this.leap(4.0F, 0.6D, this.getTarget());
            }
        }
		
        super.aiStep();
    }

	public void allyUpdate() {
		if(this.getCommandSenderWorld().isClientSide || this.isBaby())
			return;

		if(this.swarmLimit > 0 && this.nearbyCreatureCount(this.getType(), 64D) < this.swarmLimit) {
			float random = this.random.nextFloat();
			float spawnChance = 0.25F;
			if(random <= spawnChance)
				this.spawnAlly(this.position().x() - 2 + (random * 4), this.position().y(), this.position().z() - 2 + (random * 4));
		}
	}
	
    public void spawnAlly(double x, double y, double z) {
    	AgeableCreatureEntity minion = (AgeableCreatureEntity)this.creatureInfo.createEntity(this.getCommandSenderWorld());
    	minion.setGrowingAge(minion.growthTime);
    	minion.moveTo(x, y, z, this.random.nextFloat() * 360.0F, 0.0F);
		minion.setMinion(true);
		minion.applyVariant(this.getVariantIndex());
    	this.getCommandSenderWorld().addFreshEntity(minion);
        if(this.getTarget() != null)
        	minion.setLastHurtByMob(this.getTarget());
    }

	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;

        Block block = this.getCommandSenderWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
        if(block == Blocks.WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        if(this.getCommandSenderWorld().isRaining() && this.getCommandSenderWorld().canSeeSkyFromBelowWater(new BlockPos(x, y, z)))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }

	@Override
	public boolean isStrongSwimmer() {
		return true;
	}

	@Override
	public boolean canWalk() {
		return false;
	}
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public void die(DamageSource par1DamageSource) {
    	allyUpdate();
        super.die(par1DamageSource);
    }
    
    

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAir() {
        return false;
    }
}
