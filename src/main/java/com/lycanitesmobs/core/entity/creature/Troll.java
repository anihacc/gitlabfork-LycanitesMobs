package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Troll extends TameableCreatureEntity implements Enemy {
	
	public boolean griefing = true;

	public boolean stoneForm = false;
    
    public Troll(EntityType<? extends Troll> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = false;

        //this.canGrow = false;
        //this.babySpawnChance = 0.01D;
        
        this.solidCollision = true;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));

		if(this.getNavigation() instanceof GroundPathNavigation) {
			GroundPathNavigation pathNavigateGround = (GroundPathNavigation)this.getNavigation();
			pathNavigateGround.setCanOpenDoors(true);
		}
    }

	@Override
	public void loadCreatureFlags() {
		this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
	}
    
    public String getTextureName() {
    	if(this.stoneForm)
    		return super.getTextureName() + "_stone";
    	return super.getTextureName();
    }

	@Override
    public void aiStep() {
        super.aiStep();
        
        if(!this.stoneForm) {
        	if(this.isDaytime() && this.getCommandSenderWorld().canSeeSkyFromBelowWater(this.blockPosition())) {
        		this.stoneForm = true;
        	}
        }
        else {
        	if(!this.isDaytime() || !this.getCommandSenderWorld().canSeeSkyFromBelowWater(this.blockPosition())) {
                this.stoneForm = false;
            }
        }

 		if(!this.getCommandSenderWorld().isClientSide)
 	        if(this.getTarget() != null && this.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.griefing) {
 		    	float distance = this.getTarget().distanceTo(this);
 		    		if(distance <= this.getDimensions(Pose.STANDING).width + 4.0F)
 		    			this.destroyArea((int)this.position().x(), (int)this.position().y(), (int)this.position().z(), 0.5F, true);
 	        }
    }

    @Override
    public float getAISpeedModifier() {
    	if(this.stoneForm)
    		return 0.125F;
    	return 1.0F;
    }

	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile("boulderblast", target, range, 0, new Vec3(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}

    public float getDamageModifier(DamageSource damageSrc) {
		if(damageSrc.getEntity() != null) {
			ItemStack heldItem = ItemStack.EMPTY;
			if(damageSrc.getEntity() instanceof LivingEntity) {
				LivingEntity entityLiving = (LivingEntity)damageSrc.getEntity();
				if(!entityLiving.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
					heldItem = entityLiving.getItemInHand(InteractionHand.MAIN_HAND);
				}
			}
			if(ObjectLists.isPickaxe(heldItem)) {
				return 3.0F;
			}
		}
		return super.getDamageModifier(damageSrc);
    }

	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public float getFallResistance() {
    	return 50;
    }
    
    @Override
    public boolean canBurn() { return !this.stoneForm; }

    public boolean petControlsEnabled() { return true; }
}
