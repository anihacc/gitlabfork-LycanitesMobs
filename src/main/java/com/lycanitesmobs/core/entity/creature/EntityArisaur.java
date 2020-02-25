package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityArisaur extends AgeableCreatureEntity implements IGroupHeavy {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityArisaur(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        this.fleeHealthPercent = 1.0F;
        this.isAggressiveByDefault = false;
        //this.solidCollision = true;
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
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
		if(this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z)).getBlock() != Blocks.AIR) {
			IBlockState blocState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
			if(blocState.getMaterial() == Material.GRASS)
				return 10F;
			if(blocState.getMaterial() == Material.GROUND)
				return 7F;
		}
        return super.getBlockPathWeight(x, y, z);
    }
    
	// ========== Can leash ==========
    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
	    return true;
    }
}
