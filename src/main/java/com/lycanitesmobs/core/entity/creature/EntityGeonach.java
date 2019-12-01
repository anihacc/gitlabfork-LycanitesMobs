package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityGeonach extends TameableCreatureEntity implements IMob, IFusable {
	
	public int blockBreakRadius = 0;

	public float fireDamageAbsorbed = 0;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGeonach(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();

        this.stepHeight = 1.0F;
        this.attackPhaseMax = 3;
        this.setAttackCooldownMax(10);
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
		this.targetTasks.addTask(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(EntitySilverfish.class));
		super.initEntityAI();
		this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }

	@Override
	public void loadCreatureFlags() {
		this.blockBreakRadius = this.creatureInfo.getFlag("blockBreakRadius", this.blockBreakRadius);
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

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
					if (this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.blockBreakRadius > -1 && !this.isTamed()) {
						this.destroyArea((int) this.posX, (int) this.posY, (int) this.posZ, 10, true, this.blockBreakRadius);
					}
				}
			}

			// Environmental Transformation:
			if(!this.isTamed() && !this.isRareSubspecies()) {
				if (this.updateTick % 40 == 0 && this.isInLava()) {
					this.transform(CreatureManager.getInstance().getEntityClass("volcan"), null, false);
				}
				if (this.fireDamageAbsorbed >= 10) {
					this.transform(CreatureManager.getInstance().getEntityClass("volcan"), null, false);
				}
			}
		}

		// Particles:
		if(this.getEntityWorld().isRemote)
			for(int i = 0; i < 2; ++i) {
				this.getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_CRACK,
						this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
						this.posY + this.rand.nextDouble() * (double) this.height,
						this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
						0.0D, 0.0D, 0.0D,
						Blocks.TALLGRASS.getStateId(Blocks.STONE.getDefaultState()));
			}
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
        // Silverfish Extermination:
        if(this.hasAttackTarget() && this.getAttackTarget() instanceof EntitySilverfish)
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
        if(target instanceof EntitySilverfish) {
            target.setDead();
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
			ItemStack heldItem = ItemStack.EMPTY;
			if(damageSrc.getTrueSource() instanceof EntityLivingBase) {
				EntityLivingBase entityLiving = (EntityLivingBase)damageSrc.getTrueSource();
				if(!entityLiving.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
					heldItem = entityLiving.getHeldItem(EnumHand.MAIN_HAND);
				}
			}
			if(ObjectLists.isPickaxe(heldItem.getItem())) {
				return 3.0F;
			}
		}
		return super.getDamageModifier(damageSrc);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
    	if(type.equals("cactus") || type.equals("inWall"))
    		return false;
    	if(source.isFireDamage()) {
    		this.fireDamageAbsorbed += damage;
    		return false;
		}
		return super.isDamageTypeApplicable(type, source, damage);
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
	public Class getFusionClass(IFusable fusable) {
		if(fusable instanceof EntityCinder) {
			return CreatureManager.getInstance().getEntityClass("volcan");
		}
		if(fusable instanceof EntityJengu) {
			return CreatureManager.getInstance().getEntityClass("spriggan");
		}
		if(fusable instanceof EntityDjinn) {
			return CreatureManager.getInstance().getEntityClass("banshee");
		}
		if(fusable instanceof EntityAegis) {
			return CreatureManager.getInstance().getEntityClass("vapula");
		}
		if(fusable instanceof EntityArgus) {
			return CreatureManager.getInstance().getEntityClass("tremor");
		}
		return null;
	}
}
