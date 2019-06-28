package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendEntitiesGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendVillageGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityAegis extends TameableCreatureEntity implements IFusable {
	public boolean chestProtection = true; // TODO Creature flags. Chest protection.

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAegis(EntityType<? extends EntityAegis> entityType, World world) {
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
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));

		this.targetSelector.addGoal(this.nextSpecialTargetIndex++, new DefendVillageGoal(this));
		this.targetSelector.addGoal(this.nextSpecialTargetIndex++, new DefendEntitiesGoal(this, VillagerEntity.class));
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
		if(damageSrc.getTrueSource() instanceof RavagerEntity)
			damage *= 0.5F;
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
	public EntityType<? extends LivingEntity> getFusionType(IFusable fusable) {
		if(fusable instanceof EntityCinder) {
			return CreatureManager.getInstance().getEntityType("wisp");
		}
		if(fusable instanceof EntityJengu) {
			return CreatureManager.getInstance().getEntityType("nymph");
		}
		if(fusable instanceof EntityGeonach) {
			return CreatureManager.getInstance().getEntityType("vapula");
		}
		if(fusable instanceof EntityDjinn) {
			return CreatureManager.getInstance().getEntityType("sylph");
		}
		if(fusable instanceof EntityArgus) {
			return CreatureManager.getInstance().getEntityType("spectre");
		}
		return null;
	}
}
