package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

import java.util.List;

public class EffectAuraGoal extends EntityAIBase {
	BaseCreatureEntity host;

    // Properties:
	protected Potion effect;
	protected float auraRange = 10F;
	protected int effectSeconds = 5;
	protected int effectAmplifier = 0;
	protected boolean checkSight = true;
	protected int phase = -1;
	protected float damageAmount = 0;
	protected int duration = 10 * 20;
	protected int cooldownDuration = 0;
	protected int tickRate = 40;
	protected byte targetTypes = BaseCreatureEntity.TARGET_TYPES.ENEMY.id;
	protected String targetCreatureType = null;

	public int abilityTime = 0;
	public int cooldownTime = this.cooldownDuration;

	/**
	 * Constrcutor
	 * @param setHost The creature using this goal.
	 */
	public EffectAuraGoal(BaseCreatureEntity setHost) {
        this.host = setHost;
    }

	/**
	 * Sets the battle phase to restrict this goal to.
	 * @param phase The phase to restrict to, if below 0 phases are ignored.
	 * @return This goal for chaining.
	 */
	public EffectAuraGoal setPhase(int phase) {
		this.phase = phase;
		return this;
	}

	/**
	 * Sets the duration of firing (in ticks).
	 * @param duration The firing duration.
	 * @return This goal for chaining.
	 */
	public EffectAuraGoal setDuration(int duration) {
		this.duration = duration;
		return this;
	}

	/**
	 * Sets the cooldown after firing (in ticks).
	 * @param cooldown The cooldown.
	 * @return This goal for chaining.
	 */
	public EffectAuraGoal setCooldown(int cooldown) {
		this.cooldownDuration = cooldown;
		this.cooldownTime = cooldown;
		return this;
	}

	/**
	 * Sets the tick rate that effects and damage should be applied.
	 * @param tickRate The tick rate.
	 * @return This goal for chaining.
	 */
	public EffectAuraGoal setTickRate(int tickRate) {
		this.tickRate = tickRate;
		return this;
	}

	/**
	 * Sets the effect to apply.
	 * @param effect The effect to apply.
	 * @return This goal for chaining.
	 */
	public EffectAuraGoal setEffect(Potion effect) {
		this.effect = effect;
		return this;
	}

	/**
	 * Sets the effect to apply.
	 * @param effectName The effect name to apply.
	 * @return This goal for chaining.
	 */
	public EffectAuraGoal setEffect(String effectName) {
		this.effect = ObjectManager.getEffect(effectName);
		return this;
	}

	/**
	 * Sets the range of this creature's effect aura.
	 * @param auraRange The effect aura range.
	 * @return This goal for chaining.
	 */
	public EffectAuraGoal setRange(float auraRange) {
    	this.auraRange = auraRange;
    	return this;
    }

	/**
	 * Sets how long in seconds the effect lasts for, this is scaled with creature stats.
	 * @param baseEffectSeconds The scalable effect duration (in seconds).
	 * @return This goal for chaining.
	 */
	public EffectAuraGoal setEffectSeconds(int baseEffectSeconds) {
		this.effectSeconds = baseEffectSeconds;
		return this;
	}

	/**
	 * Sets the amplifier of the effect.
	 * @param effectAmplifier The effect's amplifier.
	 * @return This goal for chaining.
	 */
	public EffectAuraGoal setAmplifier(int effectAmplifier) {
		this.effectAmplifier = effectAmplifier;
		return this;
	}

	/**
	 * Sets if the aura should check line of sight.
	 * @param checkSight True to enable sight checks.
	 * @return This goal for chaining.
	 */
	public EffectAuraGoal setCheckSight(boolean checkSight) {
		this.checkSight = checkSight;
		return this;
	}

	/**
	 * Sets the damage amount of this aura.
	 * @param damageAmount The aura's damage.
	 * @return This goal for chaining.
	 */
	public EffectAuraGoal setDamageAmount(float damageAmount) {
		this.damageAmount = damageAmount;
		return this;
	}

	/**
	 * Sets the target types to affect.
	 * @param targetTypes The target types to affect.
	 * @return This goal for chaining.
	 */
	public EffectAuraGoal setTargetTypes(byte targetTypes) {
		this.targetTypes = targetTypes;
		return this;
	}

	/**
	 * Sets a specific Creature Type to target.
	 * @param creatureTypeName The creature type to target.
	 * @return This goal for chaining.
	 */
	public EffectAuraGoal setTargetCreatureType(String creatureTypeName) {
		this.targetCreatureType = creatureTypeName;
		return this;
	}

	@Override
    public boolean shouldExecute() {
		if(!this.host.isEntityAlive()) {
			return false;
		}

		if(this.phase >= 0 && this.phase != this.host.getBattlePhase()) {
			return false;
		}

		return true;
    }

	@Override
    public void startExecuting() {
		this.cooldownTime = this.cooldownDuration;
		this.abilityTime = 0;
	}

	@Override
    public void updateTask() {
		if(this.cooldownTime-- > 0) {
			this.abilityTime = 0;
			return;
		}

		if(this.abilityTime++ >= this.duration && this.cooldownDuration > 0) {
			this.cooldownTime = this.cooldownDuration;
			return;
		}

		if(this.abilityTime % this.tickRate != 0) {
			return;
		}

		PotionEffect effectInstance = null;
		if(this.effect != null) {
			effectInstance = new PotionEffect(this.effect, this.host.getEffectDuration(this.effectSeconds), this.effectAmplifier);
		}

		List aoeTargets = this.host.getNearbyEntities(EntityLivingBase.class, entity -> {
			if(!(entity instanceof EntityLivingBase)) {
				return false;
			}

			boolean validTarget = false;
			if ((this.targetTypes & BaseCreatureEntity.TARGET_TYPES.SELF.id) > 0 && entity == this.host) {
				validTarget = true;
			}
			if((this.targetTypes & BaseCreatureEntity.TARGET_TYPES.ALLY.id) > 0) {
				if(this.host.isTamed() && this.host instanceof TameableCreatureEntity) {
					if(entity instanceof TameableCreatureEntity && ((TameableCreatureEntity)entity).getPlayerOwner() == ((TameableCreatureEntity)this.host).getPlayerOwner()) {
						validTarget = true;
					}
				}
				else if(entity instanceof BaseCreatureEntity && !((BaseCreatureEntity)entity).isTamed()) {
					validTarget = true;
				}
			}
			if((this.targetTypes & BaseCreatureEntity.TARGET_TYPES.ENEMY.id) > 0) {
				if (this.host.canAttackClass(entity.getClass()) || this.host.canAttackEntity((EntityLivingBase)entity)) {
					validTarget = true;
				}
			}
			if(!validTarget) {
				return false;
			}

			if(this.targetCreatureType != null) {
				if(entity instanceof BaseCreatureEntity) {
					return this.targetCreatureType.equals(((BaseCreatureEntity)entity).creatureInfo.creatureType.getName());
				}
				return false;
			}
			if(this.checkSight && !this.host.getEntitySenses().canSee(entity)) {
				return false;
			}
			return false;
		}, this.auraRange);

		for(Object entityObj : aoeTargets) {
			EntityLivingBase target = (EntityLivingBase) entityObj;

			// Apply Effect:
			if(effectInstance != null) {
				if (!target.isPotionApplicable(effectInstance)) {
					continue;
				}
				target.addPotionEffect(effectInstance);
			}

			// Apply Damage:
			if(this.damageAmount != 0) {
				DamageSource damageSource = new EntityDamageSource("mob", this.host);
				damageSource.setDamageIsAbsolute();
				damageSource.setDamageBypassesArmor();
				target.attackEntityFrom(damageSource, this.damageAmount);
				if(this.host.minions.contains(target)) {
					this.host.onTryToDamageMinion(target, this.damageAmount);
				}
			}
		}
    }
}
