package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.AvoidTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.ParentTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeTargetingGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAspid extends EntityCreatureAgeable implements IGroupAnimal {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAspid(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
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
        this.field_70714_bg.addTask(0, new SwimmingGoal(this));
        this.field_70714_bg.addTask(1, new AttackMeleeGoal(this).setLongMemory(false));
        this.field_70714_bg.addTask(2, new AvoidGoal(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.field_70714_bg.addTask(3, new MateGoal(this));
        this.field_70714_bg.addTask(4, new TemptGoal(this).setItemList("Mushrooms"));
        this.field_70714_bg.addTask(5, new FollowParentGoal(this).setSpeed(1.0D));
        this.field_70714_bg.addTask(6, new WanderGoal(this));
        this.field_70714_bg.addTask(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new LookIdleGoal(this));

        this.field_70715_bh.addTask(1, new RevengeTargetingGoal(this).setHelpCall(true));
        this.field_70715_bh.addTask(2, new ParentTargetingGoal(this).setSightCheck(false).setDistance(32.0D));
        this.field_70715_bh.addTask(3, new AvoidTargetingGoal(this).setTargetClass(IGroupPredator.class));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
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
            BlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
            if(blockState.getMaterial() == Material.ORGANIC)
                return 10F;
            if(blockState.getMaterial() == Material.EARTH)
                return 7F;
        }
        return super.getBlockPathWeight(x, y, z);
    }
    
	// ========== Can leash ==========
    @Override
    public boolean canBeLeashedTo(PlayerEntity player) {
	    if(!this.hasAttackTarget() && !this.hasMaster())
	        return true;
	    return super.canBeLeashedTo(player);
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityAspid(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("Mushrooms", testStack);
    }
}
