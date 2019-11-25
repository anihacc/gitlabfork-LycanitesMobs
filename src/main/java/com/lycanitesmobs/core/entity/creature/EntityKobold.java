package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.GetBlockGoal;
import com.lycanitesmobs.core.entity.goals.actions.GetItemGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityKobold extends TameableCreatureEntity implements IMob {
    public boolean torchGreifing = true; // TODO Creature flags.
    public boolean theivery = true;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityKobold(EntityType<? extends EntityKobold> entityType, World world) {
        super(entityType, world);

        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spreadFire = false;

        this.canGrow = false;
        this.babySpawnChance = 0.1D;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
		this.goalSelector.addGoal(this.nextIdleGoalIndex++, new GetItemGoal(this).setDistanceMax(8).setSpeed(1.2D));
		if(this.torchGreifing)
			this.goalSelector.addGoal(this.nextIdleGoalIndex++, new GetBlockGoal(this).setDistanceMax(8).setSpeed(1.2D).setBlockName("torch").setTamedLooting(false));

		super.registerGoals();

		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }
	
	
	// ==================================================
  	//                     Spawning
  	// ==================================================
    // ========== Despawning ==========
    @Override
    protected boolean canDespawnNaturally() {
    	if(this.inventory.hasBagItems()) return false;
        return super.canDespawnNaturally();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
    private int torchLootingTime = 20;
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Torch Looting:
        if(!this.isTamed() && this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING) && this.torchGreifing) {
	        if(this.torchLootingTime-- <= 0) {
	        	this.torchLootingTime = 60;
	        	int distance = 2;
	        	String targetName = "torch";
	        	List possibleTargets = new ArrayList<BlockPos>();
	            for(int x = (int)this.posX - distance; x < (int)this.posX + distance; x++) {
	            	for(int y = (int)this.posY - distance; y < (int)this.posY + distance; y++) {
	            		for(int z = (int)this.posZ - distance; z < (int)this.posZ + distance; z++) {
                            BlockPos pos = new BlockPos(x, y, z);
	            			Block searchBlock = this.getEntityWorld().getBlockState(pos).getBlock();
	                    	if(searchBlock != null && searchBlock != Blocks.AIR) {
	                    		BlockPos possibleTarget = null;
	                			if(ObjectLists.isName(searchBlock, targetName)) {
	                				this.getEntityWorld().destroyBlock(pos, true);
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
		if(target instanceof PlayerEntity && (target.getHealth() / target.getMaxHealth()) <= 0.5F)
			return true;
		return super.shouldCreatureGroupRevenge(target);
	}

	@Override
	public boolean shouldCreatureGroupHunt(LivingEntity target) {
		if(target instanceof PlayerEntity && (target.getHealth() / target.getMaxHealth()) <= 0.5F)
			return true;
		return super.shouldCreatureGroupHunt(target);
	}

	@Override
	public boolean shouldCreatureGroupFlee(LivingEntity target) {
		if(target instanceof PlayerEntity && (target.getHealth() / target.getMaxHealth()) <= 0.5F)
			return false;
		return super.shouldCreatureGroupFlee(target);
	}
    
	
    // ==================================================
    //                     Attacks
    // ==================================================
    @Override
	public boolean canAttack(LivingEntity targetEntity) {
    	if((targetEntity.getHealth() / targetEntity.getMaxHealth()) > 0.5F)
			return false;
		return super.canAttack(targetEntity);
	}
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 10; }
    public int getBagSize() { return 10; }
    
    @Override
    public boolean canPickupItems() {
    	return this.theivery;
    }
	

    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
