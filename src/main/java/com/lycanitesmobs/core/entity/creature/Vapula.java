package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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

public class Vapula extends TameableCreatureEntity implements Enemy {

	public int blockBreakRadius = 0;

	public float fireDamageAbsorbed = 0;

    public Vapula(EntityType<? extends Vapula> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();

        this.maxUpStep = 1.0F;
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

	@Override
    public void aiStep() {
        super.aiStep();

        if(!this.getCommandSenderWorld().isClientSide) {
			if (this.isRareVariant() && !this.isPetType("familiar")){
				if (this.hasAttackTarget() && this.distanceTo(this.getTarget()) > 1 && this.getRandom().nextInt(20) == 0) {
					if (this.position().y() - 1 > this.getTarget().position().y())
						this.leap(6.0F, -1.0D, this.getTarget());
					else if (this.position().y() + 1 < this.getTarget().position().y())
						this.leap(6.0F, 1.0D, this.getTarget());
					else
						this.leap(6.0F, 0D, this.getTarget());
					if (this.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.blockBreakRadius > -1 && !this.isTamed()) {
						this.destroyArea((int) this.position().x(), (int) this.position().y(), (int) this.position().z(), 10, true, this.blockBreakRadius);
					}
				}
			}
		}

		if(this.getCommandSenderWorld().isClientSide && !CreatureManager.getInstance().config.disableBlockParticles) {
			for(int i = 0; i < 2; ++i) {
				this.getCommandSenderWorld().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIAMOND_BLOCK.defaultBlockState()),
						this.position().x() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width,
						this.position().y() + this.random.nextDouble() * (double) this.getDimensions(Pose.STANDING).height,
						this.position().z() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width,
						0.0D, 0.0D, 0.0D);
			}
		}
    }

    @Override
    public float getAISpeedModifier() {
        if(this.hasAttackTarget() && this.getTarget() instanceof Silverfish)
            return 4.0F;
        return super.getAISpeedModifier();
    }

    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;

        if(target instanceof Silverfish) {
            target.discard();
        }
        return true;
    }

	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile("crystalshard", target, range, 0, new Vec3(0, 0, 0), 0.6f, 2f, 1F);
		this.nextAttackPhase();
		super.attackRanged(target, range);
	}

	@Override
	public int getRangedCooldown() {
		if(this.getAttackPhase() < 7)
			return super.getRangedCooldown() / 24;
		return super.getRangedCooldown();
	}

    @Override
    public boolean isFlying() { return true; }

    public boolean petControlsEnabled() { return true; }

	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
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
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus") || type.equals("inWall"))
    		return false;
    	if(source.isFire()) {
    		this.fireDamageAbsorbed += damage;
    		return false;
		}
		return super.isVulnerableTo(type, source, damage);
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
