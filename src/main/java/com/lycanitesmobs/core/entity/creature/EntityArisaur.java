package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityArisaur extends EntityCreatureAgeable implements IAnimals, IGroupAnimal, IGroupHeavy {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityArisaur(World world) {
        super(world);
        
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
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        this.field_70714_bg.addTask(1, new EntityAIAttackMelee(this).setLongMemory(false));
        this.field_70714_bg.addTask(2, new EntityAIAvoid(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.field_70714_bg.addTask(3, new EntityAIMate(this).setMateDistance(5.0D));
        this.field_70714_bg.addTask(4, new EntityAITempt(this).setItemList("vegetables"));
        this.field_70714_bg.addTask(5, new EntityAIFollowParent(this).setSpeed(1.0D).setStrayDistance(3.0D));
        this.field_70714_bg.addTask(6, new EntityAIFollowMaster(this).setSpeed(1.0D).setStrayDistance(18.0F));
        this.field_70714_bg.addTask(7, new EntityAIWander(this));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));
        this.field_70715_bh.addTask(1, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(2, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
        this.field_70715_bh.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
    }
	
	
	// ==================================================
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
		if(this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z)).getBlock() != Blocks.AIR) {
			BlockState blocState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
			if(blocState.getMaterial() == Material.GRASS)
				return 10F;
			if(blocState.getMaterial() == Material.GROUND)
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
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityArisaur(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("vegetables", testStack);
    }
}