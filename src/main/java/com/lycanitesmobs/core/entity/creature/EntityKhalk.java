package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityKhalk extends EntityCreatureTameable implements IMob, IGroupFire, IGroupHeavy {

    public boolean khalkLavaDeath = true; // TODO Creature flags.

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityKhalk(EntityType<? extends EntityKhalk> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.isLavaCreature = true;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.01D;

        this.solidCollision = true;
        this.setupMob();

        this.setPathPriority(PathNodeType.LAVA, 0F);
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(2, new AttackMeleeGoal(this));
        this.goalSelector.addGoal(3, new StayByWaterGoal(this).setSpeed(1.25D));
        this.goalSelector.addGoal(4, this.aiSit);
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(6, new WanderGoal(this).setPauseRate(30));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new OwnerAttackTargetingGoal(this));
        this.targetSelector.addGoal(2, new RevengeTargetingGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(IGroupIce.class));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(IGroupWater.class));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(SnowGolemEntity.class));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(6, new AttackTargetingGoal(this).setTargetClass(IGroupPlant.class));
        this.targetSelector.addGoal(6, new OwnerDefenseTargetingGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Random Lunging:
        if(this.onGround && !this.getEntityWorld().isRemote) {
        	if(this.hasAttackTarget()) {
        		if(this.rand.nextInt(10) == 0)
        			this.leap(6.0F, 0.1D, this.getAttackTarget());
        	}
        }
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
        if(this.lavaContact())
            return 2.0F;
        return 1.0F;
    }

    // Pathing Weight:
    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;
        BlockPos pos = new BlockPos(x, y, z);
        if(this.getEntityWorld().getBlockState(pos).getBlock() == Blocks.LAVA)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getAttackTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.lavaContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }

    // Pushed By Water:
    @Override
    public boolean isPushedByWater() {
        return false;
    }
    
    
    // ==================================================
   	//                      Death
   	// ==================================================
    @Override
    public void onDeath(DamageSource damageSource) {
		if(!this.getEntityWorld().isRemote && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.khalkLavaDeath && !this.isTamed()) {
			int lavaWidth = (int)Math.floor(this.getSize(Pose.STANDING).width) - 1;
			int lavaHeight = (int)Math.floor(this.getSize(Pose.STANDING).height) - 1;
			for(int x = (int)this.posX - lavaWidth; x <= (int)this.posX + lavaWidth; x++) {
				for(int y = (int)this.posY; y <= (int)this.posY + lavaHeight; y++) {
					for(int z = (int)this.posZ - lavaWidth; z <= (int)this.posZ + lavaWidth; z++) {
						Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
						if(block == Blocks.AIR) {
							BlockState blockState = Blocks.LAVA.getDefaultState().with(FlowingFluidBlock.LEVEL, 4);
							if(x == (int)this.posX && y == (int)this.posY && z == (int)this.posZ)
								blockState = blockState = Blocks.LAVA.getDefaultState().with(FlowingFluidBlock.LEVEL, 5);
							this.getEntityWorld().setBlockState(new BlockPos(x, y, z), blockState, 3);
						}
					}
				}
			}
		}
        super.onDeath(damageSource);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return true; }
    
    @Override
    public boolean canBreatheUnderlava() {
        return true;
    }
    
    @Override
    public boolean canBreatheAir() {
        return true;
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFireDamage())
    		return 0F;
    	else return super.getDamageModifier(damageSrc);
    }
    
    
    // ==================================================
   	//                       Drops
   	// ==================================================
    // ========== Apply Drop Effects ==========
    /** Used to add effects or alter the dropped entity item. **/
    @Override
    public void applyDropEffects(EntityItemCustom entityitem) {
    	entityitem.setCanBurn(false);
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness() {
        return 1.0F;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
	

    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
