package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAbtu extends TameableCreatureEntity implements IMob {
    int swarmLimit = 5;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAbtu(EntityType<? extends EntityAbtu> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
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
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
		// Summon Allies:
        if(this.hasAttackTarget() && this.updateTick % 20 == 0) {
			this.allyUpdate();
		}

        // Random Leaping:
        if(!this.getEntityWorld().isRemote) {
            if(this.hasAttackTarget() && this.isChild() && (this.isInWater() || this.onGround)) {
                if(this.getRNG().nextInt(10) == 0)
                    this.leap(4.0F, 0.6D, this.getAttackTarget());
            }
        }
		
        super.livingTick();
    }
    
    // ========== Spawn Minions ==========
	public void allyUpdate() {
		if(this.getEntityWorld().isRemote || this.isChild())
			return;
		
		// Spawn Minions:
		if(this.swarmLimit > 0 && this.nearbyCreatureCount(this.getType(), 64D) < this.swarmLimit) {
			float random = this.rand.nextFloat();
			float spawnChance = 0.25F;
			if(random <= spawnChance)
				this.spawnAlly(this.getPositionVec().getX() - 2 + (random * 4), this.getPositionVec().getY(), this.getPositionVec().getZ() - 2 + (random * 4));
		}
	}
	
    public void spawnAlly(double x, double y, double z) {
    	AgeableCreatureEntity minion = (AgeableCreatureEntity)this.creatureInfo.createEntity(this.getEntityWorld());
    	minion.setGrowingAge(minion.growthTime);
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
		minion.setMinion(true);
		minion.applyVariant(this.getVariantIndex());
    	this.getEntityWorld().addEntity(minion);
        if(this.getAttackTarget() != null)
        	minion.setRevengeTarget(this.getAttackTarget());
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;

        Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
        if(block == Blocks.WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        if(this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(new BlockPos(x, y, z)))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getAttackTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }
	
	// Swimming:
	@Override
	public boolean isStrongSwimmer() {
		return true;
	}
	
	// Walking:
	@Override
	public boolean canWalk() {
		return false;
	}
    
    
    // ==================================================
    //                       Death
    // ==================================================
    @Override
    public void onDeath(DamageSource par1DamageSource) {
    	allyUpdate();
        super.onDeath(par1DamageSource);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAir() {
        return false;
    }
}
