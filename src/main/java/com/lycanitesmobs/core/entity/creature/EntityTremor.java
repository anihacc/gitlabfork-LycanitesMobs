package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EntityTremor extends TameableCreatureEntity implements IMob {

	public int explosionStrength = 1;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTremor(EntityType<? extends EntityTremor> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;

		this.setupMob();

        this.stepHeight = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }

	@Override
	public void loadCreatureFlags() {
		this.explosionStrength = this.creatureInfo.getFlag("explosionStrength", this.explosionStrength);
	}


    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        if(!this.getEntityWorld().isRemote && this.isRareVariant() && !this.isPetType("familiar")) {
	    	// Random Charging:
	    	if(this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) > 1 && this.getRNG().nextInt(20) == 0) {
	    		if(this.getPositionVec().getY() - 1 > this.getAttackTarget().getPositionVec().getY())
	    			this.leap(6.0F, -1.0D, this.getAttackTarget());
	    		else if(this.getPositionVec().getY() + 1 < this.getAttackTarget().getPositionVec().getY())
	    			this.leap(6.0F, 1.0D, this.getAttackTarget());
	    		else
	    			this.leap(6.0F, 0D, this.getAttackTarget());
	    	}
        }

        // Particles:
        if(this.getEntityWorld().isRemote)
			for(int i = 0; i < 2; ++i) {
				this.getEntityWorld().addParticle(ParticleTypes.SMOKE, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
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
    	
    	// Explosion:
		int explosionStrength = Math.max(1, this.explosionStrength);
		Explosion.Mode damageTerrain = this.explosionStrength > 0 && this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? Explosion.Mode.BREAK : Explosion.Mode.NONE;
		if(this.isPetType("familiar")) {
			explosionStrength = 1;
			damageTerrain = Explosion.Mode.NONE;
		}
		this.getEntityWorld().createExplosion(this, this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ(), explosionStrength, damageTerrain);

        return true;
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
	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.BagSize; }
    // ==================================================
   	//                     Immunities
   	// ==================================================
	// ========== Damage ==========
    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
		if(source.isExplosion()) {
			this.heal(damage);
			return false;
		}
    	if(type.equals("cactus") || type.equals("inWall")) return false;
    	    return super.isVulnerableTo(type, source, damage);
    }

	@Override
	public boolean isVulnerableTo(Entity entity) {
		if(entity instanceof WitherEntity) {
			return false;
		}
		return super.isVulnerableTo(entity);
	}

	@Override
	public boolean isPotionApplicable(EffectInstance effectInstance) {
    	if(effectInstance.getPotion() == Effects.WITHER) {
    		return false;
		}
		return super.isPotionApplicable(effectInstance);
	}
    
    @Override
    public boolean canBurn() { return false; }

    @Override
	public boolean canBeTargetedBy(LivingEntity entity) {
    	if(entity instanceof WitherEntity) {
    		return false;
		}
		return super.canBeTargetedBy(entity);
	}
}
