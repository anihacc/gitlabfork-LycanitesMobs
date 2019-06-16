package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAbtu extends EntityCreatureTameable implements IMob, IGroupPredator {
	
	EntityAIWander wanderAI;
    int swarmLimit = 5; // TODO Creature Flags
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAbtu(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0.9D;
        this.canGrow = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(1, new EntityAIStayByWater(this));
        this.field_70714_bg.addTask(2, this.aiSit);
        this.field_70714_bg.addTask(3, new EntityAIAttackMelee(this).setLongMemory(false));
        this.wanderAI = new EntityAIWander(this);
        this.field_70714_bg.addTask(6, wanderAI.setPauseRate(0));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class));
            this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(AnimalEntity.class));
        }
        this.field_70715_bh.addTask(6, new EntityAITargetOwnerThreats(this));
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
		if(this.swarmLimit > 0 && this.nearbyCreatureCount(this.getClass(), 64D) < this.swarmLimit) {
			float random = this.rand.nextFloat();
			float spawnChance = 0.25F;
			if(random <= spawnChance)
				this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
		}
	}
	
    public void spawnAlly(double x, double y, double z) {
    	EntityCreatureAgeable minion = new EntityAbtu(this.getEntityWorld());
    	minion.setGrowingAge(minion.growthTime);
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
		minion.setMinion(true);
		minion.applySubspecies(this.getSubspeciesIndex());
    	this.getEntityWorld().func_217376_c(minion);
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
    public boolean canBreatheAboveWater() {
        return false;
    }
}
