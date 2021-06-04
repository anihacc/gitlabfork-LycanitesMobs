package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAspid extends AgeableCreatureEntity {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAspid(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        this.attackCooldownMax = 10;
        this.fleeHealthPercent = 1.0F;
        this.isAggressiveByDefault = false;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.addTask(this.nextDistractionGoalIndex++, new TemptGoal(this).setIncludeDiet(true));
		this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if(!this.getEntityWorld().isRemote && this.hasCustomName()) {
			//LycanitesMobs.logDebug("", "Distractions: " + this.nextDistractionGoalIndex);
		}
        
        // Trail:
        if(!this.getEntityWorld().isRemote && (this.ticksExisted % 10 == 0 || this.isMoving() && this.ticksExisted % 5 == 0)) {
        	int trailHeight = 2;
        	if(this.isChild())
        		trailHeight = 1;
        	for(int y = 0; y < trailHeight; y++) {
        		Block block = this.getEntityWorld().getBlockState(this.getPosition().add(0, y, 0)).getBlock();
        		if(block == Blocks.AIR || block == Blocks.SNOW || block == ObjectManager.getBlock("poisoncloud"))
        			this.getEntityWorld().setBlockState(this.getPosition().add(0, y, 0), ObjectManager.getBlock("poisoncloud").getDefaultState());
        	}
		}
    }
	
	
	// ==================================================
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        if(this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z)).getBlock() != Blocks.AIR) {
            IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
            if(blockState.getMaterial() == Material.GRASS)
                return 10F;
            if(blockState.getMaterial() == Material.GROUND)
                return 7F;
        }
        return super.getBlockPathWeight(x, y, z);
    }
    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }


    // ========== Can leash ==========
    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
	    if(!this.hasAttackTarget() && !this.hasMaster())
	        return true;
	    return super.canBeLeashedTo(player);
    }
}
