package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.api.IGroupRock;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAegis extends EntityCreatureTameable implements IGroupRock, IFusable {
	protected BlockPos villagePos;
	public boolean chestProtection = true; // TODO Creature flags.

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAegis(EntityType<? extends EntityAegis> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();
		this.isAggressiveByDefault = false;
        this.stepHeight = 1.0F;
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
		this.targetSelector.addGoal(3, new DefendVillageTargetingGoal(this));
		this.targetSelector.addGoal(4, new DefenseTargetingGoal(this, VillagerEntity.class));
        //this.targetSelector.addGoal(5, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
		this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(EntityArgus.class));
		this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(PillagerEntity.class));
        this.targetSelector.addGoal(6, new OwnerDefenseTargetingGoal(this));
		this.targetSelector.addGoal(7, new FuseTargetingGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

        if(!this.getEntityWorld().isRemote) {
			/*if (!this.hasAttackTarget() && !this.isPetType("familiar") && this.updateTick % 40 == 0){
				BlockPos protectLocation = null;
				if(this.hasHome()) {
					protectLocation = this.getHomePosition();
				}
				else if(this.villagePos == null || this.updateTick % 400 == 0) {
					this.villagePos = this.getEntityWorld().findNearestStructure("Village", this.getPosition(), 128, false);
				}
				protectLocation = this.villagePos;

				// Monitor Nearest Player: TODO Global village reputation is no longer a thing, disabled for now, should be moved to AI Goal.
				if(protectLocation != null) {
					PlayerEntity player = this.getEntityWorld().getNearestAttackablePlayer(this, 64, 32);
					ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
					if (player != null) {
						if (this.chestProtection && Math.sqrt(player.getDistanceSq(new Vec3d(protectLocation))) <= 60)
							if ((player.openContainer instanceof ChestContainer)) {
								this.setAttackTarget(player);
								this.setFixateTarget(player);
							}
							else if (extendedPlayer != null && extendedPlayer.justBrokenBlock != null) {
								Block brokenBlock = extendedPlayer.justBrokenBlock.getBlock();
								if (brokenBlock instanceof ChestBlock || brokenBlock instanceof DoorBlock || brokenBlock == Blocks.GLOWSTONE) {
									this.setAttackTarget(player);
									this.setFixateTarget(player);
								}
							}
					}
				}
			}*/

			if(!this.hasAttackTarget()) {
				this.setBlocking();
			}
		}
    }

	@Override
	public boolean canBeTargetedBy(LivingEntity entity) {
		if(entity instanceof IronGolemEntity || entity instanceof VillagerEntity) {
			return false;
		}
		return super.canBeTargetedBy(entity);
	}
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

    @Override
	public boolean canAttackWhileBlocking() {
		return false;
	}
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }

    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus") || type.equals("inWall"))
    		return false;
		return super.isInvulnerableTo(type, source, damage);
    }


	// ==================================================
	//                   Taking Damage
	// ==================================================
	// ========== On Damage ==========
	/** Called when this mob has received damage. Here a random blocking chance is applied. **/
	@Override
	public void onDamage(DamageSource damageSrc, float damage) {
		if(this.getRNG().nextDouble() > 0.75D && this.getHealth() / this.getMaxHealth() > 0.25F)
			this.setBlocking();
		super.onDamage(damageSrc, damage);
	}

	// ========== Blocking ==========
	public void setBlocking() {
		this.currentBlockingTime = this.blockingTime + this.getRNG().nextInt(this.blockingTime / 2);
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
	public Class getFusionClass(IFusable fusable) {
		if(fusable instanceof EntityCinder) {
			return EntityWisp.class;
		}
		if(fusable instanceof EntityJengu) {
			return EntityNymph.class;
		}
		if(fusable instanceof EntityGeonach) {
			return EntityVapula.class;
		}
		if(fusable instanceof EntityDjinn) {
			return EntitySylph.class;
		}
		if(fusable instanceof EntityArgus) {
			return EntitySpectre.class;
		}
		return null;
	}
}
