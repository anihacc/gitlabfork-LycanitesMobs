package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.MasterAttackTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.MasterTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.ParentTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeTargetingGoal;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityJoust extends EntityCreatureAgeable implements IGroupAnimal {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityJoust(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.babySpawnChance = 0.1D;
        this.canGrow = true;
        this.attackCooldownMax = 10;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new SwimmingGoal(this));
        this.field_70714_bg.addTask(1, new MateGoal(this));
        this.field_70714_bg.addTask(2, new TemptGoal(this).setItemList("CactusFood"));
        this.field_70714_bg.addTask(3, new AttackMeleeGoal(this).setLongMemory(false));
        this.field_70714_bg.addTask(4, new FollowParentGoal(this).setSpeed(1.0D));
        this.field_70714_bg.addTask(5, new FollowMasterGoal(this).setSpeed(1.0D).setStrayDistance(8.0F));
        this.field_70714_bg.addTask(6, new WanderGoal(this));
        this.field_70714_bg.addTask(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new LookIdleGoal(this));
        this.field_70715_bh.addTask(0, new RevengeTargetingGoal(this).setHelpClasses(EntityJoustAlpha.class));
        this.field_70715_bh.addTask(1, new MasterAttackTargetingGoal(this));
        this.field_70715_bh.addTask(2, new ParentTargetingGoal(this).setSightCheck(false).setDistance(32.0D));
        this.field_70715_bh.addTask(2, new MasterTargetingGoal(this).setTargetClass(EntityJoustAlpha.class).setSightCheck(false).setRange(64.0D));
    }


    // ==================================================
    //                      Spawn
    // ==================================================
    // ========== On Spawn ==========
    @Override
    public void onFirstSpawn() {
        // Random Alpha:
        CreatureInfo alphaInfo = CreatureManager.getInstance().getCreature("joustalpha");
        if(alphaInfo != null) {
            float alphaChance = (float)alphaInfo.creatureSpawn.spawnWeight / Math.max(this.creatureInfo.creatureSpawn.spawnWeight, 1);
            if (this.getRNG().nextFloat() <= alphaChance) {
                EntityJoustAlpha alpha = new EntityJoustAlpha(this.getEntityWorld());
                alpha.copyLocationAndAnglesFrom(this);
                this.getEntityWorld().func_217376_c(alpha);
                this.remove();
            }
        }
        super.onFirstSpawn();
    }
	
    
	// ==================================================
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        BlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
		if(blockState.getBlock() != Blocks.AIR) {
			if(blockState.getMaterial() == Material.SAND)
				return 10F;
			if(blockState.getMaterial() == Material.CLAY)
				return 7F;
			if(blockState.getMaterial() == Material.ROCK)
				return 5F;
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
   	//                      Attacks
   	// ==================================================
	@Override
	public boolean canAttack(LivingEntity target) {
		if(target instanceof EntityJoustAlpha)
			return false;
		return super.canAttack(target);
	}
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	return super.isInvulnerableTo(type, source, damage);
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable partener) {
		return new EntityJoust(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("cactusfood", testStack);
    }
    
    
    // ==================================================
    //                     Growing
    // ==================================================
	@Override
	public void setGrowingAge(int age) {
		if(age == 0 && this.getAge() < 0)
			if(this.getRNG().nextFloat() >= 0.9F) {
				EntityJoustAlpha alphaJoust = new EntityJoustAlpha(this.getEntityWorld());
				alphaJoust.copyLocationAndAnglesFrom(this);
				this.getEntityWorld().func_217376_c(alphaJoust);
				this.remove();
			}
        super.setGrowingAge(age);
    }
}
