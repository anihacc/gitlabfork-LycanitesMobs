package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EntityVapula extends TameableCreatureEntity implements IMob {

	public int blockBreakRadius = 0;

	public float fireDamageAbsorbed = 0;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityVapula(EntityType<? extends EntityVapula> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();

        this.stepHeight = 1.0F;
        this.attackPhaseMax = 8;
        this.setAttackCooldownMax(60);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false).setMaxChaseDistanceSq(3.0F));
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(18.0F).setMinChaseDistance(10.0F).setCheckSight(false));
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
    public void livingTick() {
        super.livingTick();

        if(!this.getEntityWorld().isRemote) {
			if (this.getSubspeciesIndex() == 3 && !this.isPetType("familiar")){
				// Random Charging:
				if (this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) > 1 && this.getRNG().nextInt(20) == 0) {
					if (this.getPositionVec().getY() - 1 > this.getAttackTarget().getPositionVec().getY())
						this.leap(6.0F, -1.0D, this.getAttackTarget());
					else if (this.getPositionVec().getY() + 1 < this.getAttackTarget().getPositionVec().getY())
						this.leap(6.0F, 1.0D, this.getAttackTarget());
					else
						this.leap(6.0F, 0D, this.getAttackTarget());
					if (this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING) && this.blockBreakRadius > -1 && !this.isTamed()) {
						this.destroyArea((int) this.getPositionVec().getX(), (int) this.getPositionVec().getY(), (int) this.getPositionVec().getZ(), 10, true, this.blockBreakRadius);
					}
				}
			}
		}

        // Particles:
		if(this.getEntityWorld().isRemote && !CreatureManager.getInstance().config.disableBlockParticles) {
			for(int i = 0; i < 2; ++i) {
				this.getEntityWorld().addParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.DIAMOND_BLOCK.getDefaultState()),
						this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
						this.getPositionVec().getY() + this.rand.nextDouble() * (double) this.getSize(Pose.STANDING).height,
						this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
						0.0D, 0.0D, 0.0D);
			}
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
        return true;
    }

	// ========== Ranged Attack ==========
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile("crystalshard", target, range, 0, new Vec3d(0, 0, 0), 0.6f, 2f, 1F);
		this.nextAttackPhase();
		super.attackRanged(target, range);
	}

	@Override
	public int getRangedCooldown() {
		if(this.getAttackPhase() < 7)
			return super.getRangedCooldown() / 24;
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
			if(damageSrc.getTrueSource() instanceof LivingEntity) {
				LivingEntity entityLiving = (LivingEntity)damageSrc.getTrueSource();
				if(!entityLiving.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
					heldItem = entityLiving.getHeldItem(Hand.MAIN_HAND);
				}
			}
			if(ObjectLists.isPickaxe(heldItem)) {
				return 3.0F;
			}
		}
    	return super.getDamageModifier(damageSrc);
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
	public boolean canBreatheUnderwater() {
		return true;
	}
    
    @Override
    public boolean canBurn() {
    	return false;
    }
}
