package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityTremor extends TameableCreatureEntity implements IMob {

	private AttackMeleeGoal meleeAttackAI;

	public int tremorExplosionStrength = 1; // TODO Creature flags.

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTremor(World world) {
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
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if(!this.getEntityWorld().isRemote && this.getSubspeciesIndex() == 3 && !this.isPetType("familiar")) {
	    	// Random Charging:
	    	if(this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) > 1 && this.getRNG().nextInt(20) == 0) {
	    		if(this.posY - 1 > this.getAttackTarget().posY)
	    			this.leap(6.0F, -1.0D, this.getAttackTarget());
	    		else if(this.posY + 1 < this.getAttackTarget().posY)
	    			this.leap(6.0F, 1.0D, this.getAttackTarget());
	    		else
	    			this.leap(6.0F, 0D, this.getAttackTarget());
	    	}
        }

        // Particles:
        if(this.getEntityWorld().isRemote)
			for(int i = 0; i < 2; ++i) {
				this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
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
		int explosionStrength = Math.max(1, this.tremorExplosionStrength);
		boolean damageTerrain = this.tremorExplosionStrength > 0 && this.getEntityWorld().getGameRules().getBoolean("mobGriefing");
		if(this.isPetType("familiar")) {
			explosionStrength = 1;
			damageTerrain = false;
		}
		this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, explosionStrength, damageTerrain);

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
   	//                     Immunities
   	// ==================================================
	// ========== Damage ==========
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
		if(source.isExplosion()) {
			this.heal(damage);
			return false;
		}
    	if(type.equals("cactus") || type.equals("inWall")) return false;
    	    return super.isDamageTypeApplicable(type, source, damage);
    }

	@Override
	public boolean isDamageEntityApplicable(Entity entity) {
		if(entity instanceof EntityWither) {
			return false;
		}
		return super.isDamageEntityApplicable(entity);
	}

	@Override
	public boolean isPotionApplicable(PotionEffect effectInstance) {
    	if(effectInstance.getPotion() == MobEffects.WITHER) {
    		return false;
		}
		return super.isPotionApplicable(effectInstance);
	}
    
    @Override
    public boolean canBurn() { return false; }

    @Override
	public boolean canBeTargetedBy(EntityLivingBase entity) {
    	if(entity instanceof EntityWither) {
    		return false;
		}
		return super.canBeTargetedBy(entity);
	}
}
