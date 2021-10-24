package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.CustomItemEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Khalk extends TameableCreatureEntity implements Enemy, IGroupHeavy {

    public boolean lavaDeath = true;

    public Khalk(EntityType<? extends Khalk> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.isLavaCreature = true;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.01D;

        this.solidCollision = true;
        this.setupMob();

        this.setPathfindingMalus(BlockPathTypes.LAVA, 0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }

	@Override
	public void loadCreatureFlags() {
		this.lavaDeath = this.creatureInfo.getFlag("lavaDeath", this.lavaDeath);
	}

	@Override
    public void aiStep() {
        super.aiStep();
        
        if(this.onGround && !this.getCommandSenderWorld().isClientSide) {
        	if(this.hasAttackTarget()) {
        		if(this.random.nextInt(10) == 0)
        			this.leap(6.0F, 0.1D, this.getTarget());
        	}
        }
    }

    @Override
    public float getAISpeedModifier() {
        if(this.lavaContact())
            return 2.0F;
        return 1.0F;
    }

    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;
        BlockPos pos = new BlockPos(x, y, z);
        if(this.getCommandSenderWorld().getBlockState(pos).getBlock() == Blocks.LAVA)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.lavaContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void die(DamageSource damageSource) {
		if(!this.getCommandSenderWorld().isClientSide && this.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.lavaDeath && !this.isTamed()) {
			int lavaWidth = (int)Math.floor(this.getDimensions(Pose.STANDING).width) - 1;
			int lavaHeight = (int)Math.floor(this.getDimensions(Pose.STANDING).height) - 1;
			for(int x = (int)this.position().x() - lavaWidth; x <= (int)this.position().x() + lavaWidth; x++) {
				for(int y = (int)this.position().y(); y <= (int)this.position().y() + lavaHeight; y++) {
					for(int z = (int)this.position().z() - lavaWidth; z <= (int)this.position().z() + lavaWidth; z++) {
						Block block = this.getCommandSenderWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
						if(block == Blocks.AIR) {
							BlockState blockState = Blocks.LAVA.defaultBlockState().setValue(LiquidBlock.LEVEL, 4);
							if(x == (int)this.position().x() && y == (int)this.position().y() && z == (int)this.position().z())
								blockState = blockState = Blocks.LAVA.defaultBlockState().setValue(LiquidBlock.LEVEL, 5);
							this.getCommandSenderWorld().setBlock(new BlockPos(x, y, z), blockState, 3);
						}
					}
				}
			}
		}
        super.die(damageSource);
    }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return true; }
    
    @Override
    public boolean canBreatheUnderlava() {
        return true;
    }
    
    @Override
    public boolean canBreatheAir() {
        return true;
    }
    
    

    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFire())
    		return 0F;
    	else return super.getDamageModifier(damageSrc);
    }

    @Override
    public void applyDropEffects(CustomItemEntity entityitem) {
    	entityitem.setCanBurn(false);
    }

    public float getBrightness() {
        return 1.0F;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }

    public boolean petControlsEnabled() { return true; }
}
