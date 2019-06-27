package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.FindAvoidTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindParentGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeGoal;
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

public class EntityArisaur extends AgeableCreatureEntity implements IGroupAnimal, IGroupHeavy {
	
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
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(1, new AttackMeleeGoal(this).setLongMemory(false));
        this.goalSelector.addGoal(2, new AvoidGoal(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.goalSelector.addGoal(3, new MateGoal(this).setMateDistance(5.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this).setItemList("vegetables"));
        this.goalSelector.addGoal(5, new FollowParentGoal(this).setSpeed(1.0D).setStrayDistance(3.0D));
        this.goalSelector.addGoal(6, new FollowMasterGoal(this).setSpeed(1.0D).setStrayDistance(18.0F));
        this.goalSelector.addGoal(7, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));
        this.targetSelector.addGoal(1, new RevengeGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(2, new FindParentGoal(this).setSightCheck(false).setDistance(32.0D));
        this.targetSelector.addGoal(3, new FindAvoidTargetGoal(this).setTargetClass(IGroupPredator.class));
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
