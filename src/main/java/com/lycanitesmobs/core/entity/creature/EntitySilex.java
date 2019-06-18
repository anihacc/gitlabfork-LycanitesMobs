package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.AvoidTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.ParentTargetingGoal;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntitySilex extends EntityCreatureAgeable implements IGroupAnimal {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySilex(EntityType<? extends EntitySilex> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = false;

        this.babySpawnChance = 0.01D;
        this.canGrow = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new StayByWaterGoal(this));
        this.goalSelector.addGoal(2, new AttackMeleeGoal(this).setLongMemory(false));
        this.goalSelector.addGoal(3, new TemptGoal(this).setItemStack(new ItemStack(Items.LAPIS_LAZULI, 1)));
        this.goalSelector.addGoal(4, new AvoidGoal(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.goalSelector.addGoal(5, new MateGoal(this));
        this.goalSelector.addGoal(6, new FollowParentGoal(this).setSpeed(1.0D));
        this.goalSelector.addGoal(7, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));
        //this.targetSelector.addGoal(1, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetSelector.addGoal(2, new ParentTargetingGoal(this).setSightCheck(false).setDistance(32.0D));
        this.targetSelector.addGoal(3, new AvoidTargetingGoal(this).setTargetClass(IGroupPredator.class));
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
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("inWall")) return false;
        return super.isInvulnerableTo(type, source, damage);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAboveWater() {
        return false;
    }

    @Override
    public boolean canBurn() { return false; }


    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Breeding Item ==========
    @Override
    public boolean isBreedingItem(ItemStack testStack) {
        if(testStack == null || !this.isInWater())
            return false;
        return testStack.getItem() == Items.LAPIS_LAZULI;
    }
}
