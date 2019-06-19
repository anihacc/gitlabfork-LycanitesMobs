package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupFire;
import com.lycanitesmobs.api.IGroupPlant;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.entity.projectile.EntityLifeDrain;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class EntitySpriggan extends EntityCreatureTameable implements IMob, IGroupPlant {
	
	AttackRangedGoal rangedAttackAI;
	public int farmingRate = 20; // TODO Creature flags.

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySpriggan(EntityType<? extends EntitySpriggan> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;

        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(3, this.aiSit);
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.rangedAttackAI = new AttackRangedGoal(this).setSpeed(0.75D).setStaminaTime(100).setRange(8.0F).setMinChaseDistance(4.0F);
        this.goalSelector.addGoal(5, rangedAttackAI);
        this.goalSelector.addGoal(8, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new OwnerAttackTargetingGoal(this));
        this.targetSelector.addGoal(2, new RevengeTargetingGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(2, new AttackTargetingGoal(this).setTargetClass(IGroupFire.class));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(6, new OwnerDefenseTargetingGoal(this));
    }


    // ==================================================
    //                      Updates
    // ==================================================
	private int farmingTick = 0;
    // ========== Living Update ==========
    @Override
    public void livingTick() {
        super.livingTick();

		// Water Healing:
		if(this.getAir() >= 0) {
			if (this.isInWater())
				this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 3 * 20, 2));
			else if (this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(this.getPosition()))
				this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 3 * 20, 1));
		}

        // Farming:
        int currentFarmingRate = this.farmingRate;
        if(this.isTamed() && currentFarmingRate > 0) {
            if(this.isPetType("familiar"))
                currentFarmingRate = this.farmingRate * 10;
            if(this.subspecies != null)
                currentFarmingRate = Math.max(1, Math.round((float)this.farmingRate / 3));
        	this.farmingTick++;
	        int farmingRange = 16;
	        int farmingHeight = 4;
	        for(int x = (int)this.posX - farmingRange; x <= (int)this.posX + farmingRange; x++) {
	        	for(int y = (int)this.posY - farmingHeight; y <= (int)this.posY + farmingHeight; y++) {
	        		for(int z = (int)this.posZ - farmingRange; z <= (int)this.posZ + farmingRange; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
	        			Block farmingBlock = this.getEntityWorld().getBlockState(pos).getBlock();
	        			if(farmingBlock instanceof IPlantable && farmingBlock instanceof IGrowable && farmingBlock != Blocks.TALL_GRASS) {
	        				
		        			// Boost Crops Every X Seconds:
		        			if(!this.getEntityWorld().isRemote && this.farmingTick % (currentFarmingRate) == 0) {
                                /*if(farmingBlock.getTickRandomly()) {
                                    this.getEntityWorld().scheduleBlockUpdate(pos, farmingBlock, currentFarmingRate, 1);
                                }*/

		    	        		IGrowable growableBlock = (IGrowable)farmingBlock;
		    	        		if(growableBlock.canGrow(this.getEntityWorld(), pos, this.getEntityWorld().getBlockState(pos), this.getEntityWorld().isRemote())) {
	    	                        if(growableBlock.canUseBonemeal(this.getEntityWorld(), this.getRNG(), pos, this.getEntityWorld().getBlockState(pos))) {
	    	                        	growableBlock.grow(this.getEntityWorld(), this.getRNG(), pos, this.getEntityWorld().getBlockState(pos));
	    	                        }
		    	                }
		        			}
		        			
		        			// Crop Growth Effect:
		        			if(this.getEntityWorld().isRemote && this.farmingTick % 40 == 0) {
		        				double d0 = this.getRNG().nextGaussian() * 0.02D;
		                        double d1 = this.getRNG().nextGaussian() * 0.02D;
		                        double d2 = this.getRNG().nextGaussian() * 0.02D;
		        				this.getEntityWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, (double)((float)x + this.getRNG().nextFloat()), (double)y + (double)this.getRNG().nextFloat(), (double)((float)z + this.getRNG().nextFloat()), d0, d1, d2);
		        			}
	        			}
	    	        }
		        }
	        }
        }

        // Particles:
        if(this.getEntityWorld().isRemote)
            for(int i = 0; i < 2; ++i) {
                this.getEntityWorld().addParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.TALL_GRASS.getDefaultState()),
                        this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
                        this.posY + this.rand.nextDouble() * (double) this.getSize(Pose.STANDING).height,
                        this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
                        0.0D, 0.0D, 0.0D);
            }
    }


    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    EntityLifeDrain projectile = null;
    @Override
    public void attackRanged(Entity target, float range) {
    	// Update Laser:
    	if(this.projectile != null && this.projectile.isAlive()) {
    		this.projectile.setTime(20);
    	}
    	else {
    		this.projectile = null;
    	}
    	
    	// Create New Laser:
    	if(this.projectile == null) {
	    	// Type:
	    	this.projectile = new EntityLifeDrain(ProjectileManager.getInstance().oldProjectileTypes.get(EntityLifeDrain.class), this.getEntityWorld(), this, 25, 20);
	    	
	    	// Launch:
	        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	        this.getEntityWorld().addEntity(projectile);
    	}

    	super.attackRanged(target, range);
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
        if(damageSrc.isFireDamage())
            return 2.0F;
        if(damageSrc.getTrueSource() != null) {
            Item heldItem = null;
            if(damageSrc.getTrueSource() instanceof PlayerEntity) {
                PlayerEntity entityPlayer = (PlayerEntity)damageSrc.getTrueSource();
                if(!entityPlayer.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
                    heldItem = entityPlayer.getHeldItem(Hand.MAIN_HAND).getItem();
                }
            }
            else if(damageSrc.getTrueSource() instanceof LivingEntity) {
                LivingEntity entityLiving = (LivingEntity)damageSrc.getTrueSource();
                if(!entityLiving.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
                    heldItem = entityLiving.getHeldItem(Hand.MAIN_HAND).getItem();
                }
            }
            if(ObjectLists.isAxe(heldItem))
                return 2.0F;
        }
        return super.getDamageModifier(damageSrc);
    }


    // ==================================================
    //                     Abilities
    // ==================================================
    @Override
    public boolean isFlying() { return true; }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
}
