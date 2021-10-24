package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.GetBlockGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.GetItemGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class Kobold extends TameableCreatureEntity implements Enemy {
    public boolean griefing = true;
    public boolean theivery = true;

    public Kobold(EntityType<? extends Kobold> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.spreadFire = false;

        this.canGrow = false;
        this.babySpawnChance = 0.1D;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
		if(this.theivery)
			this.goalSelector.addGoal(this.nextIdleGoalIndex++, new GetItemGoal(this).setDistanceMax(8).setSpeed(1.2D));
		if(this.griefing)
			this.goalSelector.addGoal(this.nextIdleGoalIndex++, new GetBlockGoal(this).setDistanceMax(8).setSpeed(1.2D).setBlockName("torch").setTamedLooting(false));

		super.registerGoals();

		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(Player.class).setLongMemory(false));
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }

	@Override
	public void loadCreatureFlags() {
		this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
		this.theivery = this.creatureInfo.getFlag("theivery", this.theivery);
	}

	@Override
	public void onRemovedFromWorld() {
		if(!this.isTamed() && this.inventory.hasBagItems()) {
			this.inventory.dropInventory();
		}
		super.onRemovedFromWorld();
	}

    private int torchLootingTime = 20;
	@Override
    public void aiStep() {
        super.aiStep();

        if(!this.isTamed() && this.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.griefing) {
	        if(this.torchLootingTime-- <= 0) {
	        	this.torchLootingTime = 60;
	        	int distance = 2;
	        	String targetName = "torch";
	        	List possibleTargets = new ArrayList<BlockPos>();
	            for(int x = (int)this.position().x() - distance; x < (int)this.position().x() + distance; x++) {
	            	for(int y = (int)this.position().y() - distance; y < (int)this.position().y() + distance; y++) {
	            		for(int z = (int)this.position().z() - distance; z < (int)this.position().z() + distance; z++) {
                            BlockPos pos = new BlockPos(x, y, z);
	            			Block searchBlock = this.getCommandSenderWorld().getBlockState(pos).getBlock();
	                    	if(searchBlock != null && searchBlock != Blocks.AIR) {
	                    		BlockPos possibleTarget = null;
	                			if(ObjectLists.isName(searchBlock, targetName)) {
	                				this.getCommandSenderWorld().destroyBlock(pos, true);
	                				break;
	                			}
	                    	}
	                    }
	                }
	            }
	        }
        }
    }

	@Override
	public boolean shouldCreatureGroupRevenge(LivingEntity target) {
		if(target instanceof Player && (target.getHealth() / target.getMaxHealth()) <= 0.5F)
			return true;
		return super.shouldCreatureGroupRevenge(target);
	}

	@Override
	public boolean shouldCreatureGroupHunt(LivingEntity target) {
		if(target instanceof Player && (target.getHealth() / target.getMaxHealth()) <= 0.5F)
			return true;
		return super.shouldCreatureGroupHunt(target);
	}

	@Override
	public boolean shouldCreatureGroupFlee(LivingEntity target) {
		if(target instanceof Player && (target.getHealth() / target.getMaxHealth()) <= 0.5F)
			return false;
		return super.shouldCreatureGroupFlee(target);
	}

    @Override
	public boolean canAttack(LivingEntity targetEntity) {
    	if((targetEntity.getHealth() / targetEntity.getMaxHealth()) > 0.5F)
			return false;
		return super.canAttack(targetEntity);
	}

	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.bagSize; }
    
    @Override
    public boolean canPickupItems() {
    	return this.theivery;
    }

    public boolean petControlsEnabled() { return true; }
}
