package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

public class EntityVolcan extends EntityCreatureTameable implements IMob, IGroupRock, IGroupFire {

	private AttackMeleeGoal meleeAttackAI;

	public int volcanMeltRadius = 2; // TODO Creature flags.

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityVolcan(EntityType<? extends EntityVolcan> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.meleeAttackAI = new AttackMeleeGoal(this).setLongMemory(true);
        this.goalSelector.addGoal(2, meleeAttackAI);
        this.goalSelector.addGoal(3, this.aiSit);
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(8, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new OwnerAttackTargetingGoal(this));
        this.targetSelector.addGoal(2, new RevengeTargetingGoal(this).setHelpCall(true));
		this.targetSelector.addGoal(2, new AttackTargetingGoal(this).setTargetClass(IGroupIce.class));
		this.targetSelector.addGoal(2, new AttackTargetingGoal(this).setTargetClass(IGroupWater.class));
		this.targetSelector.addGoal(2, new AttackTargetingGoal(this).setTargetClass(SnowGolemEntity.class));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(SilverfishEntity.class));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
		this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(IGroupPlant.class));
        this.targetSelector.addGoal(6, new OwnerDefenseTargetingGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

		// Burning Aura Attack:
		if(!this.getEntityWorld().isRemote && this.updateTick % 40 == 0) {
			List aoeTargets = this.getNearbyEntities(LivingEntity.class, null, 4);
			for(Object entityObj : aoeTargets) {
				LivingEntity target = (LivingEntity)entityObj;
				if(target != this && !(target instanceof IGroupFire) && this.canAttack(target.getType()) && this.canAttack(target) && this.getEntitySenses().canSee(target)) {
					target.setFire(2);
				}
			}
		}

		// Melt Blocks:
		if(!this.getEntityWorld().isRemote && this.updateTick % 40 == 0 && this.volcanMeltRadius > 0 && !this.isTamed() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
			int range = this.volcanMeltRadius;
			for (int w = -((int) Math.ceil(this.getSize(Pose.STANDING).width) + range); w <= (Math.ceil(this.getSize(Pose.STANDING).width) + range); w++) {
				for (int d = -((int) Math.ceil(this.getSize(Pose.STANDING).width) + range); d <= (Math.ceil(this.getSize(Pose.STANDING).width) + range); d++) {
					for (int h = -((int) Math.ceil(this.getSize(Pose.STANDING).height) + range); h <= Math.ceil(this.getSize(Pose.STANDING).height); h++) {
						Block block = this.getEntityWorld().getBlockState(this.getPosition().add(w, h, d)).getBlock();
						if (block == Blocks.OBSIDIAN || block == Blocks.COBBLESTONE || block == Blocks.GRAVEL) {
							BlockState blockState = Blocks.LAVA.getDefaultState().with(FlowingFluidBlock.LEVEL, 5);
							if (block == Blocks.OBSIDIAN)
								blockState = Blocks.LAVA.getDefaultState();
							this.getEntityWorld().setBlockState(this.getPosition().add(w, h, d), blockState);
						}
						/*else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER || block == Blocks.ICE || block == Blocks.SNOW) {
							this.getEntityWorld().setBlockState(this.getPosition().add(w, h, d), Blocks.AIR.getDefaultState(), 3);
						}*/
					}
				}
			}
		}

		// Particles:
		if(this.getEntityWorld().isRemote) {
			for(int i = 0; i < 2; ++i) {
				this.getEntityWorld().addParticle(ParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.posY + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
				this.getEntityWorld().addParticle(ParticleTypes.DRIPPING_LAVA, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.posY + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
			}
			if(this.ticksExisted % 10 == 0)
				for(int i = 0; i < 2; ++i) {
					this.getEntityWorld().addParticle(ParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.posY + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
				}
		}
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;

        // Silverfish Extermination:
        if(target instanceof SilverfishEntity) {
            target.remove();
        }
        
        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

	// ========== Perform Command ==========
	@Override
	public void performCommand(String command, PlayerEntity player, ItemStack itemStack) {

		// Water:
		if(command.equals("Water")) {
			this.replacePlayersItem(player, itemStack, new ItemStack(Items.LAVA_BUCKET));
		}

		super.performCommand(command, player, itemStack);
	}
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


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
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus") || type.equals("inWall")) return false;
    	    return super.isInvulnerableTo(type, source, damage);
    }
    
    @Override
    public boolean canBurn() { return false; }

	@Override
	public boolean waterDamage() { return true; }
}
