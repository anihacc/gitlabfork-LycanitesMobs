package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.api.IGroupRock;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class EntityGeonach extends EntityCreatureTameable implements IMob, IGroupRock, IFusable {
	
	public int geonachBlockBreakRadius = 0; // TODO Creature flags.
	public float fireDamageAbsorbed = 0;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGeonach(EntityType<? extends EntityGeonach> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();

        this.stepHeight = 1.0F;
        this.attackPhaseMax = 3;
        this.setAttackCooldownMax(10);
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
		this.goalSelector.addGoal(1, new FollowFuseGoal(this).setLostDistance(16));
        this.goalSelector.addGoal(2, new AttackMeleeGoal(this).setLongMemory(true));
        this.goalSelector.addGoal(3, this.aiSit);
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(8, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new OwnerAttackTargetingGoal(this));
        this.targetSelector.addGoal(2, new RevengeTargetingGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(SilverfishEntity.class));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(6, new OwnerDefenseTargetingGoal(this));
		this.targetSelector.addGoal(7, new FuseTargetingGoal(this));
    }

	// ========== Set Size ==========
	@Override
	public void setSizeScale(double scale) {
		if(this.isRareSubspecies()) {
			super.setSizeScale(scale * 1.5D);
			return;
		}
		super.setSizeScale(scale);
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

        if(!this.getEntityWorld().isRemote) {
			if (this.getSubspeciesIndex() == 3 && !this.isPetType("familiar")){
				// Random Charging:
				if (this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) > 1 && this.getRNG().nextInt(20) == 0) {
					if (this.posY - 1 > this.getAttackTarget().posY)
						this.leap(6.0F, -1.0D, this.getAttackTarget());
					else if (this.posY + 1 < this.getAttackTarget().posY)
						this.leap(6.0F, 1.0D, this.getAttackTarget());
					else
						this.leap(6.0F, 0D, this.getAttackTarget());
					if (this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.geonachBlockBreakRadius > -1 && !this.isTamed()) {
						this.destroyArea((int) this.posX, (int) this.posY, (int) this.posZ, 10, true, this.geonachBlockBreakRadius);
					}
				}
			}

			// Environmental Transformation:
			if(!this.isTamed()) {
				if (this.updateTick % 40 == 0 && this.isInLava()) {
					this.transform(CreatureManager.getInstance().getEntityType("volcan"), null, false);
				}
				if (this.fireDamageAbsorbed >= 10) {
					this.transform(CreatureManager.getInstance().getEntityType("volcan"), null, false);
				}
			}
		}

        // Particles:
        if(this.getEntityWorld().isRemote)
            for(int i = 0; i < 2; ++i) {
                this.getEntityWorld().addParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState()),
                        this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
                        this.posY + this.rand.nextDouble() * (double) this.getSize(Pose.STANDING).height,
                        this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
                        0.0D, 0.0D, 0.0D);
            }
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
        // Silverfish Extermination:
        if(this.hasAttackTarget() && this.getAttackTarget() instanceof SilverfishEntity)
            return 4.0F;
        return super.getAISpeedModifier();
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

		this.nextAttackPhase();
        return true;
    }

	@Override
	public int getMeleeCooldown() {
		if(this.getAttackPhase() == 2)
			return super.getMeleeCooldown() * 3;
		return super.getMeleeCooldown();
	}

	@Override
	public int getRangedCooldown() {
		if(this.getAttackPhase() == 2)
			return super.getRangedCooldown() * 3;
		return super.getRangedCooldown();
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
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    @Override
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.getTrueSource() != null) {
            // Silverfish Extermination:
            if(damageSrc.getTrueSource() instanceof SilverfishEntity) {
                return 0F;
            }

            // Pickaxe Damage:
    		Item heldItem = null;
    		if(damageSrc.getTrueSource() instanceof LivingEntity) {
                LivingEntity entityLiving = (LivingEntity)damageSrc.getTrueSource();
	    		if(!entityLiving.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
	    			heldItem = entityLiving.getHeldItem(Hand.MAIN_HAND).getItem();
	    		}
    		}
    		if(ObjectLists.isPickaxe(heldItem))
                return 4.0F;
    	}
    	return 1.0F;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus") || type.equals("inWall"))
    		return false;
    	if(source.isFireDamage()) {
    		this.fireDamageAbsorbed += damage;
    		return false;
		}
		return super.isInvulnerableTo(type, source, damage);
    }
    
    @Override
    public boolean canBurn() {
    	// Geonach can now burn in order to heat up and transform into Volcans.
    	return true;
    }


	// ==================================================
	//                      Fusion
	// ==================================================
	protected IFusable fusionTarget;

	@Override
	public IFusable getFusionTarget() {
		return this.fusionTarget;
	}

	@Override
	public void setFusionTarget(IFusable fusionTarget) {
		this.fusionTarget = fusionTarget;
	}

	@Override
	public EntityType<? extends LivingEntity> getFusionType(IFusable fusable) {
		if(fusable instanceof EntityCinder) {
			return CreatureManager.getInstance().getEntityType("volcan");
		}
		if(fusable instanceof EntityJengu) {
			return CreatureManager.getInstance().getEntityType("spriggan");
		}
		if(fusable instanceof EntityDjinn) {
			return CreatureManager.getInstance().getEntityType("banshee");
		}
		if(fusable instanceof EntityAegis) {
			return CreatureManager.getInstance().getEntityType("vapula");
		}
		if(fusable instanceof EntityArgus) {
			return CreatureManager.getInstance().getEntityType("tremor");
		}
		return null;
	}
}
