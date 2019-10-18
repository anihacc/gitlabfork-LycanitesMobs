package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendEntitiesGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendVillageGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityAegis extends TameableCreatureEntity implements IFusable {
	public boolean chestProtection = true; // TODO Creature flags. Chest protection.

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAegis(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();
        this.stepHeight = 1.0F;
	}

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));

		this.targetTasks.addTask(this.nextSpecialTargetIndex++, new DefendVillageGoal(this));
		this.targetTasks.addTask(this.nextSpecialTargetIndex++, new DefendEntitiesGoal(this, EntityVillager.class));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

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
					EntityPlayer player = this.getEntityWorld().getNearestAttackablePlayer(this, 64, 32);
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
	public boolean canBeTargetedBy(EntityLivingBase entity) {
		if(entity instanceof EntityIronGolem || entity instanceof EntityVillager) {
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
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
    	if(type.equals("cactus") || type.equals("inWall"))
    		return false;
		return super.isDamageTypeApplicable(type, source, damage);
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
			return CreatureManager.getInstance().getEntityClass("wisp");
		}
		if(fusable instanceof EntityJengu) {
			return CreatureManager.getInstance().getEntityClass("nymph");
		}
		if(fusable instanceof EntityGeonach) {
			return CreatureManager.getInstance().getEntityClass("vapula");
		}
		if(fusable instanceof EntityDjinn) {
			return CreatureManager.getInstance().getEntityClass("sylph");
		}
		if(fusable instanceof EntityArgus) {
			return CreatureManager.getInstance().getEntityClass("spectre");
		}
		return null;
	}
}
