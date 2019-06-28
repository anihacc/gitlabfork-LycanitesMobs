package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendEntitiesGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityMakaAlpha extends AgeableCreatureEntity implements IGroupAlpha {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityMakaAlpha(EntityType<? extends EntityMakaAlpha> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.attackCooldownMax = 10;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PaddleGoal(this));
        this.goalSelector.addGoal(5, new AttackMeleeGoal(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.goalSelector.addGoal(6, new AttackMeleeGoal(this));
        this.goalSelector.addGoal(9, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RevengeGoal(this).setHelpClasses(EntityMaka.class));
		this.targetSelector.addGoal(2, new DefendEntitiesGoal(this, VillagerEntity.class));
		this.targetSelector.addGoal(3, new FindAttackTargetGoal(this).setTargetClass(IGroupPredator.class));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).setTargetClass(EntityMakaAlpha.class).setChance(10));
        this.targetSelector.addGoal(5, new FindAttackTargetGoal(this).setTargetClass(PlayerEntity.class).setOnlyNearby(true).setChance(100));
        this.targetSelector.addGoal(6, new FindAttackTargetGoal(this).setTargetClass(VillagerEntity.class).setOnlyNearby(true).setChance(100));
    }
	
	
	// ==================================================
  	//                      Update
  	// ==================================================
	@Override
	public void livingTick() {
		super.livingTick();
		
		// Alpha Sparring Cooldown:
		if(this.hasAttackTarget() && this.getAttackTarget() instanceof EntityMakaAlpha) {
			if(this.getHealth() / this.getMaxHealth() <= 0.25F || this.getAttackTarget().getHealth() / this.getAttackTarget().getMaxHealth() <= 0.25F) {
				this.setAttackTarget(null);
			}
		}
	}
	
	
	// ==================================================
   	//                      Movement
   	// ==================================================
    // ========== Pathing Weight ==========
    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        BlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
        Block block = blockState.getBlock();
        if(block != Blocks.AIR) {
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
        return true;
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
    @Override
    public boolean canAttack(LivingEntity target) {
		if(target instanceof EntityMaka)
			return false;
    	if(target instanceof EntityMakaAlpha && (this.getHealth() / this.getMaxHealth() <= 0.25F || target.getHealth() / target.getMaxHealth() <= 0.25F))
    		return false;
    	else return super.canAttack(target);
    }
    
    // ========== Set Attack Target ==========
    @Override
    public void setAttackTarget(LivingEntity entity) {
    	if(entity == null && this.getAttackTarget() instanceof EntityMakaAlpha) {
    		this.heal((this.getMaxHealth() - this.getHealth()) / 2);
    		this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 20 * 20, 2, false, false));
			this.getAttackTarget().heal((this.getMaxHealth() - this.getHealth()) / 2);
			this.getAttackTarget().addPotionEffect(new EffectInstance(Effects.REGENERATION, 20 * 20, 2, false, false));
    	}
    	super.setAttackTarget(entity);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
        float damageMod = super.getDamageModifier(damageSrc);
        if(damageSrc.getTrueSource() instanceof EntityMakaAlpha)
            damageMod *= 2;
        return damageMod;
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
		return (AgeableCreatureEntity) CreatureManager.getInstance().getCreature("maka").createEntity(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return false;
    }
}
