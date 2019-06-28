package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityArisaur extends AgeableCreatureEntity implements IGroupHeavy {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityArisaur(EntityType<? extends EntityArisaur> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        this.fleeHealthPercent = 1.0F;
        this.isAggressiveByDefault = false;
        this.solidCollision = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
		this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new TemptGoal(this).setItemList("vegetables"));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
    }
	
	
	// ==================================================
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
		if(this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z)).getBlock() != Blocks.AIR) {
			BlockState blocState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
			if(blocState.getMaterial() == Material.ORGANIC)
				return 10F;
			if(blocState.getMaterial() == Material.EARTH)
				return 7F;
		}
        return super.getBlockPathWeight(x, y, z);
    }
    
	// ========== Can leash ==========
    @Override
    public boolean canBeLeashedTo(PlayerEntity player) {
	    return true;
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("vegetables", testStack);
    }
}
