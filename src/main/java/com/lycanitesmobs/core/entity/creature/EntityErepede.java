package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.entity.projectile.EntityMudshot;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityErepede extends EntityCreatureRideable implements IGroupPredator {
	
	int difficultyUpdate = -1;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityErepede(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;
        this.attackCooldownMax = 10;
        this.setupMob();
        
        // Stats:
        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new SwimmingGoal(this));
        this.field_70714_bg.addTask(1, new PlayerControlGoal(this));
        this.field_70714_bg.addTask(2, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.field_70714_bg.addTask(3, new AttackRangedGoal(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(6.0F));
		this.field_70714_bg.addTask(4, this.aiSit);
		this.field_70714_bg.addTask(5, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(6, new FollowParentGoal(this).setSpeed(1.0D));
        this.field_70714_bg.addTask(7, new WanderGoal(this));
        this.field_70714_bg.addTask(9, new BegGoal(this));
        this.field_70714_bg.addTask(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new LookIdleGoal(this));

		this.field_70715_bh.addTask(0, new OwnerRevengeTargetingGoal(this));
		this.field_70715_bh.addTask(1, new OwnerAttackTargetingGoal(this));
		this.field_70715_bh.addTask(2, new OwnerDefenseTargetingGoal(this));
        this.field_70715_bh.addTask(3, new RevengeTargetingGoal(this).setHelpCall(true));
        this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            if(CreatureManager.getInstance().getCreature("Joust") != null)
                this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(EntityJoust.class).setPackHuntingScale(1, 3));
            if(CreatureManager.getInstance().getCreature("JoustAlpha") != null)
                this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(EntityJoustAlpha.class).setPackHuntingScale(1, 1));
        }

        this.field_70715_bh.addTask(0, new ParentTargetingGoal(this).setSightCheck(false).setDistance(32.0D));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(this.hasRiderTarget()) {
            BlockState blockState = this.getEntityWorld().getBlockState(this.getPosition().add(0, -1, 0));
            if (blockState.getMaterial() == Material.SAND || (blockState.getMaterial() == Material.AIR && this.getEntityWorld().getBlockState(this.getPosition().add(0, -2, 0)).getMaterial() == Material.SAND))
                return 1.8F;
        }
    	return 1.0F;
    }
    
    @Override
    public double getMountedYOffset() {
        return (double)this.getSize(Pose.STANDING).height * 0.9D;
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    public void mountAbility(Entity rider) {
    	if(this.getEntityWorld().isRemote)
    		return;
    	
    	if(this.abilityToggled)
    		return;
    	if(this.getStamina() < this.getStaminaCost())
    		return;
    	
    	if(rider instanceof PlayerEntity) {
    		PlayerEntity player = (PlayerEntity)rider;
	    	EntityMudshot projectile = new EntityMudshot(this.getEntityWorld(), player);
	    	this.getEntityWorld().func_217376_c(projectile);
	    	this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	    	this.triggerAttackCooldown();
    	}
    	
    	this.applyStaminaCost();
    }
    
    public float getStaminaCost() {
    	return 5;
    }
    
    public int getStaminaRecoveryWarmup() {
    	return 5 * 20;
    }
    
    public float getStaminaRecoveryMax() {
    	return 1.0F;
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile(EntityMudshot.class, target, range, 0, new Vec3d(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 15; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	return super.isInvulnerableTo(type, source, damage);
    }
    
    @Override
    public float getFallResistance() {
    	return 10;
    }


	// ==================================================
	//                     Pet Control
	// ==================================================
	public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityErepede(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack par1ItemStack) {
		return false;
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("CookedMeat", testStack);
    }
}
